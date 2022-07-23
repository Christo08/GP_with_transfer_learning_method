package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.data.DataController;
import com.transfer.learning.gp.controllers.data.SourceTaskDataController;
import com.transfer.learning.gp.controllers.data.TargetTaskDataController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;
import com.transfer.learning.gp.data.objects.xml.Experiment;
import com.transfer.learning.gp.data.objects.xml.Run;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GPController {
    private static Random random = new Random();
    private TransferLearningController transferLearningController;
    private int mod;

    private DataController dataController;
    private PopulationController populationController;
    private String dataSetName;
    private long seed;
    private double oldBestChromosomesAccuracy;
    private Experiment experiment;
    private String pathToExperiment;
    private long startTime;
    private int transferLearningMod;

    public GPController(String pathToData, String dataSetName, int mod) throws FileNotFoundException {
        this.dataSetName = dataSetName;
        this.pathToExperiment = pathToData;
        this.mod = mod;
        this.startTime = System.currentTimeMillis();
        this.transferLearningMod = -1;
        seed = random.nextLong();

        random.setSeed(seed);
        System.out.println("Seed: "+ seed);

        if (mod == 1)
            this.dataController = new SourceTaskDataController(pathToExperiment+"\\Cleaned");
        else
            this.dataController = new TargetTaskDataController(pathToExperiment+"\\Cleaned",dataSetName);

        this.populationController = new PopulationController();
        this.transferLearningController = new TransferLearningController(this);
    }

    public static Random getRandom() {
        return random;
    }

    public double fitnessOfChromosomes(int counter) {
        double numberOfCorrect =0;
        for (Map<String, Double> dataLine: dataController.getDataSet()) {
            if (populationController.evaluateChromosomes(dataLine,counter) == dataLine.get("ans"))
                numberOfCorrect++;
        }
        return numberOfCorrect/((double)dataController.getDataSet().size()) * 100;
    }

    public void experiment() throws JAXBException {
        experiment = new Experiment(dataSetName, seed);
        experiment.setTransferLearningMethod(transferLearningMod);
        for (int counter =0; counter < ConfigController.getNumberOfRuns(); counter++){
            evolveAnswers((counter+1));
            convertObjectToXML(experiment);
        }
        System.out.println("Finished");
    }

    public List<ChromosomeWrapper> getTopPercentageOfPopulation(int size) {

        return populationController.getTopChromosomes(size);
    }

    public void exportData() throws IOException, JAXBException {

        dataController.chanceMod();

        experiment = new Experiment(dataSetName, seed);

        do {
            System.out.println("Please set a transfer learning method, by entering the number:");
            System.out.println("0 Exit");
            System.out.println("1 Full tree");
            System.out.println("2 Sub-tree");
            System.out.println("3 Best gen");
            System.out.println("4 GPCR");
            System.out.println("5 PST");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            transferLearningMod = Integer.parseInt(reader.readLine());
            experiment.setTransferLearningMethod(transferLearningMod);
            if (transferLearningMod == 0)
                return;

            Chromosome bestChromosomes = evolveAnswers(1);
            convertObjectToXML(experiment);

            if (transferLearningMod == 1) {
                transferLearningController.exportFullTree();
            } else if (transferLearningMod == 2) {
                transferLearningController.exportSubTree();
            } else if (transferLearningMod == 3) {
                transferLearningController.exportBestGen();
            }else if (transferLearningMod == 4) {
                List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask1 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());

                this.populationController.clearPopulation();
                dataController.chanceMod();
                evolveAnswers(1);
                convertObjectToXML(experiment);
                dataController.chanceMod();

                List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask2 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());

                transferLearningController.exportCSRP(topPercentageOfPopulationForSourceTask1, topPercentageOfPopulationForSourceTask2);
            }else if (transferLearningMod == 5) {
                transferLearningController.exportPST(bestChromosomes);
            }
        }while (transferLearningMod!= 0);
    }

    public void importData() throws IOException, JAXBException {
        experiment = new Experiment(dataSetName, seed);

        do {
            System.out.println("Please set a transfer learning method, by entering the number:");
            System.out.println("0 Exit");
            System.out.println("1 Full tree");
            System.out.println("2 Sub-tree");
            System.out.println("3 Best gen");
            System.out.println("4 GPCR");
            System.out.println("5 PST");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            transferLearningMod = Integer.parseInt(reader.readLine());
            experiment.setTransferLearningMethod(transferLearningMod);
            if (transferLearningMod == 0)
                return;

            if (transferLearningMod == 1) {
                populationController.replaceChromosomes(transferLearningController.importFullTree());
            } else if (transferLearningMod == 2) {
                populationController.replaceChromosomes(transferLearningController.importSubTree());
            } else if (transferLearningMod == 3) {
                populationController.replaceChromosomes(transferLearningController.importBestGen());
            }else if (transferLearningMod == 4) {

            }else if (transferLearningMod == 5) {

            }

            for (int counter =0; counter < ConfigController.getNumberOfRuns(); counter++){
                evolveAnswers((counter+1));
                convertObjectToXML(experiment);
            }
        }while (transferLearningMod!= 0);
    }

    public String getPathToExperiment() {
        return pathToExperiment;
    }

    private Chromosome evolveAnswers(int runNumber){
        Run run = new Run();
        run.setRunNumber(runNumber);
        run.setStartTimeStamp(System.currentTimeMillis());
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);
        int indexOfBestChromosomes =findBestChromosomes();
        Chromosome bestChromosomes = populationController.getChromosomes(indexOfBestChromosomes);
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproduction())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutation());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossover());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossover(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomes = populationController.getChromosomes(indexOfBestChromosomes);
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                    (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    run.setRunSuccessful(false);
                    break;
                }
                if (transferLearningMod == 3){
                    transferLearningController.addChromosomesToBestGenArray(getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInBestGenMethod()));
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerations(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStamp(System.currentTimeMillis());
            run.setAccuracyOnTrainingDataset(bestChromosomesAccuracy);
            if (transferLearningMod == -1)
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
        double fitness;
        int chromosomesIndex=0;
        for (int counter = 0; counter < ConfigController.getPopulationSize(); counter++){
            fitness = fitnessOfChromosomes(counter);

            populationController.addFitnessOfChromosomes(counter, fitness);

            if (fitness > maxFitness){
                maxFitness =fitness;
                chromosomesIndex = counter;
            }
            System.out.println(counter);
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
        int bestIndex = parentsIndex.stream()
                                    .sorted()
                                    .collect(Collectors.toList())
                                    .get(0);
        return bestIndex;
    }

    private double getAccuracyOnTestingDataset(int counter){
        double numberOfCorrect =0;
        dataController.chanceMod();
        for (Map<String, Double> dataLine: dataController.getDataSet()) {
            if (populationController.evaluateChromosomes(dataLine,counter) == dataLine.get("ans"))
                numberOfCorrect++;
        }
        dataController.chanceMod();
        return numberOfCorrect/((double)dataController.getDataSet().size()) * 100;

    }

    private void convertObjectToXML(Experiment experiment) throws JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to File
        marshaller.marshal(experiment, new File(pathToExperiment+"\\Experiment\\"+dataSetName+"\\"+startTime+"-Experiment.xml"));
    }
}
