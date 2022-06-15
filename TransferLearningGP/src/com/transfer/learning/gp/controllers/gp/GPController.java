package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.data.DataController;
import com.transfer.learning.gp.controllers.data.SourceTaskDataController;
import com.transfer.learning.gp.controllers.data.TargetTaskDataController;
import com.transfer.learning.gp.data.objects.Chromosomes;
import com.transfer.learning.gp.data.objects.xml.Experiment;
import com.transfer.learning.gp.data.objects.xml.Run;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;
    private PopulationController populationController;
    private SDRSController sdrsController;
    private String dataSetName;
    private long seed;
    private double oldBestChromosomesAccuracy;
    private Experiment experiment;
    private String pathToExperiment;

    public GPController(String pathToData, String dataSetName, int mod) throws FileNotFoundException {
        seed = random.nextLong();
        this.dataSetName = dataSetName;
        pathToExperiment = pathToData;
        random.setSeed(seed);
        System.out.println("Seed: "+ seed);

        if (mod == 1)
            this.dataController = new SourceTaskDataController(pathToExperiment);
        else
            this.dataController = new TargetTaskDataController(pathToExperiment,dataSetName);

        this.populationController = new PopulationController();
        this.sdrsController = new SDRSController(this, dataController, populationController);
    }

    private Chromosomes evolveAnswers(int runNumber){
        Run run = new Run();
        run.setRunNumber(runNumber);
        run.setStartTimeStamp(System.currentTimeMillis());
        Chromosomes bestChromosomes = populationController.getChromosomes(findBestChromosomes());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(bestChromosomes);
        int indexOfBestChromosomes =0;
        int counterChange =0;
        System.out.println("Run "+runNumber);
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosomes> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getReproductionSize())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getMutationSize());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getCrossoverSize());
                for (int counter1 = 0; counter1 < ConfigController.getCrossoverSize() - 1; counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                if (counter != 0 && counter % ConfigController.getNumberOfGenerationsBeforeEvolveMap() ==0){
                    sdrsController.evolveMap();
                    System.out.println();
                }
                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomes = populationController.getChromosomes(indexOfBestChromosomes);
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(bestChromosomes);

                if (bestChromosomesAccuracy != oldBestChromosomesAccuracy)
                    counterChange =0;
                else
                    counterChange++;
                if (counterChange>= 100) {
                    run.setRunSuccessful(false);
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerations(counter+1);
                counter = counter+1;
                // System.out.println(bestChromosomes);
            }
            run.setStopTimeStamp(System.currentTimeMillis());
            run.setAccuracyOnTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSuccessful(true);
        }catch (Exception exception){
            run.setRunSuccessful(false);
            exception.printStackTrace();
        }
        experiment.addRun(run);
        return bestChromosomes;
    }

    private List<Integer> selectParents(int numberOfParentsNeeded) {
        List<Integer> indexes = new LinkedList<>();
        for (int counter =0; counter< numberOfParentsNeeded; counter++) {
            indexes.add(selectParent());
        }
        return indexes;
    }

    private int findBestChromosomes() {
        double maxFitness =0;
        double fitness =0;
        int chromosomesIndex=0;
        for (int counter = 0; counter < ConfigController.getPopulationSize(); counter++){
            fitness = fitnessOfChromosomes(counter);

            populationController.addFitnessOfChromosomes(counter, fitness);

            if (fitness > maxFitness){
                maxFitness =fitness;
                chromosomesIndex = counter;
            }
        }
        return chromosomesIndex;
    }

    private int selectParent(){
        List<Integer> parentsIndex = new ArrayList<>(ConfigController.getTournamentSize());
        int randomIndex;
        for (int counter = 0; counter < ConfigController.getTournamentSize(); counter++){
            do {
                randomIndex = random.nextInt(ConfigController.getPopulationSize());
            }while (parentsIndex.contains(randomIndex));
            parentsIndex.add(randomIndex);
        }
        int bestIndex =0;
        double bestFitness =0;
        double fitness =0;
        for (int index: parentsIndex){
            fitness = populationController.getFitnessOfChromosomes(index);
            if (fitness > bestFitness){
                bestFitness = fitness;
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    public static Random getRandom() {
        return random;
    }

    public double fitnessOfChromosomes(int counter) {
        double numberOfCorrect =0;
        for (Map<String, Double> dataLine: dataController.getDataSet()) {
            String chromosomesOutput = sdrsController.getClass(populationController.evaluateChromosomes(dataLine,counter));
            if (chromosomesOutput.equals(dataLine.get("ans").toString()))
                numberOfCorrect++;
        }
        return numberOfCorrect/((double)dataController.getDataSet().size()) * 100;
    }

    private double getAccuracyOnTestingDataset(int counter){
        double numberOfCorrect =0;
        dataController.chanceMod();
        for (Map<String, Double> dataLine: dataController.getDataSet()) {
            String chromosomesOutput = sdrsController.getClass(populationController.evaluateChromosomes(dataLine,counter));
            if (chromosomesOutput.equals(dataLine.get("ans").toString()))
                numberOfCorrect++;
        }
        dataController.chanceMod();
        return numberOfCorrect/((double)dataController.getDataSet().size()) * 100;

    }

    public void experiment() throws JAXBException {
        experiment = new Experiment(dataSetName, seed);
        for (int counter =0; counter < ConfigController.getNumberOfRuns(); counter++){
            evolveAnswers((counter+1));
            convertObjectToXML(experiment);
        }
    }

    private void convertObjectToXML(Experiment experiment) throws JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to File
        marshaller.marshal(experiment, new File(pathToExperiment.substring(0,pathToExperiment.lastIndexOf("\\"))+"\\Experiment\\"+experiment.getDataSetName()+"Experiment.xml"));
    }
}
