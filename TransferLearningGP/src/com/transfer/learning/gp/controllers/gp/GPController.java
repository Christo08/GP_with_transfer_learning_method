package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.data.DataController;
import com.transfer.learning.gp.controllers.data.SourceTaskDataController;
import com.transfer.learning.gp.controllers.data.TargetTaskDataController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;
import com.transfer.learning.gp.data.objects.xml.Experiment;
import com.transfer.learning.gp.data.objects.xml.GeneticOperatorsConfig;
import com.transfer.learning.gp.data.objects.xml.Run;
import com.transfer.learning.gp.data.objects.xml.TransferLearningMethod;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GPController {
    private static Random random = new Random();
    private TransferLearningController transferLearningController;

    private DataController dataController;
    private PopulationController populationController;
    private long seed;
    private double oldBestChromosomesAccuracy;
    private String pathToData;
    private long startTime;
    private int transferLearningMod;
    private String dataSetName;

    public GPController(String pathToData, long seed) throws FileNotFoundException {
        this.pathToData = pathToData;
        this.seed =seed;

        this.transferLearningMod = -1;

        random.setSeed(seed);

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

    public List<ChromosomeWrapper> getTopPercentageOfPopulation(int size) {

        return populationController.getTopChromosomes(size);
    }

    public String getPathToData() {
        return pathToData;
    }

    private List<Integer> selectParents(int numberOfParentsNeeded) {
        List<Integer> indexes = new LinkedList<>();
        for (int counter =0; counter< numberOfParentsNeeded; counter++) {
            int newIndexes;
            do {
                newIndexes = selectParent();
            }while (indexes.contains(newIndexes));
            indexes.add(newIndexes);
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
        double trainingDataSize = dataController.getDataSet().size();
        dataController.chanceMod();
        return numberOfCorrect/trainingDataSize * 100;

    }

    private void convertObjectToXML(Experiment experiment, boolean hasTransferLearning) throws JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Write to File
        if (hasTransferLearning)
            marshaller.marshal(experiment, new File(pathToData +"\\Experiment\\"+dataSetName+"\\"+startTime+"_TL_Experiment.xml"));
        else
            marshaller.marshal(experiment, new File(pathToData +"\\Experiment\\"+dataSetName+"\\"+startTime+"_Experiment.xml"));
    }

    public void trainWithoutTransferLearning(String dataSetName) throws FileNotFoundException, JAXBException {
        this.dataSetName = dataSetName;
        dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.initPopulation();
        this.startTime = System.currentTimeMillis();

        Experiment experiment = new Experiment(dataSetName);
        TransferLearningMethod transferLearningMethod = new TransferLearningMethod();
        experiment.setTransferLearningMethod(transferLearningMethod);
        for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
            experiment.addRun(runGPWithoutTransferLearning(runCounter));
            convertObjectToXML(experiment, false);
            populationController.recreatePopulation();
        }
    }

    public void trainWithTransferLearning(String dataSetName) throws IOException, JAXBException {
            System.out.println("Please set a transfer learning method, by entering the number:");
            System.out.println("0 exit");
            System.out.println("1 Full tree");
            System.out.println("2 Sub-tree");
            System.out.println("3 Best gen");
            System.out.println("4 GPCR");
            System.out.println("5 PST");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            transferLearningMod = Integer.parseInt(reader.readLine());

            this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");

            this.dataSetName = dataSetName;

            Experiment experiment = new Experiment(dataSetName);
            populationController.initPopulation();
            this.startTime = System.currentTimeMillis();
            if (transferLearningMod == 1){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Full tree",transferLearningController.getPathToFolder(transferLearningMod));
                experiment.setTransferLearningMethod(transferLearningMethod);
                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    experiment.addRun(runGPWithFullTreeTransferLearning(runCounter));
                    convertObjectToXML(experiment,true);
                    populationController.resetSets();
                    this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");
                    populationController.recreatePopulation();
                }
            }
            else if (transferLearningMod == 2){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Sub-tree",transferLearningController.getPathToFolder(transferLearningMod));
                experiment.setTransferLearningMethod(transferLearningMethod);
                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    experiment.addRun(runGPWithSubTreeTransferLearning(runCounter));
                    convertObjectToXML(experiment,true);
                    populationController.resetSets();
                    this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");
                    populationController.recreatePopulation();
                }
            }
            else if (transferLearningMod == 3){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Best gen",transferLearningController.getPathToFolder(transferLearningMod));
                experiment.setTransferLearningMethod(transferLearningMethod);
                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    experiment.addRun(runGPWithBestGenTransferLearning(runCounter));
                    convertObjectToXML(experiment,true);
                    populationController.resetSets();
                    this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");
                    populationController.recreatePopulation();
                }
            }
            else if (transferLearningMod == 4){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("GPCR",transferLearningController.getPathToFolder(transferLearningMod));
                experiment.setTransferLearningMethod(transferLearningMethod);
                experiment.addGeneticOperatorsConfig(new GeneticOperatorsConfig(2));
                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    experiment.addRun(runGPWithGPCRTransferLearning(runCounter));
                    convertObjectToXML(experiment,true);
                    populationController.resetSets();
                    this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");
                    populationController.recreatePopulation();
                }
            }
            else if (transferLearningMod == 5){
                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++) {
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("PST", transferLearningController.getPathToFolder(transferLearningMod));
                    experiment.setTransferLearningMethod(transferLearningMethod);
                    experiment.addRun(runGPWithPSTTransferLearning(runCounter));
                    convertObjectToXML(experiment, true);
                    populationController.resetSets();
                    this.dataController = new SourceTaskDataController(pathToData+"\\Cleaned");
                    populationController.recreatePopulation();
                }
            }
            else {
                return;
            }
    }

    private Run runGPWithoutTransferLearning(int runNumber){
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);
        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }
        return run;
    }

    private Run runGPWithFullTreeTransferLearning(int runNumber) throws FileNotFoundException {
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);

        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfSourceDatasetOne(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetOne(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetOne(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetOne(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }

        transferLearningController.exportFullTree(startTime, runNumber);
        populationController.resetSets();
        this.dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.replaceChromosomes(transferLearningController.importFullTree(startTime, runNumber));

        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Testing data");
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnTrainingDataset(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunTargetSuccessful(true);
        }catch (Exception exception){
            run.setRunTargetSuccessful(false);
            exception.printStackTrace();
        }

        return run;
    }

    private Run runGPWithSubTreeTransferLearning(int runNumber) throws FileNotFoundException {
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);

        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfSourceDatasetOne(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetOne(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetOne(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetOne(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }

        transferLearningController.exportSubTree(startTime, runNumber);
        populationController.resetSets();
        this.dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.replaceChromosomes(transferLearningController.importSubTree(startTime, runNumber));

        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnTrainingDataset(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunTargetSuccessful(true);
        }
        catch (Exception exception){
            run.setRunTargetSuccessful(false);
            exception.printStackTrace();
        }

        return run;
    }

    private Run runGPWithBestGenTransferLearning(int runNumber) throws FileNotFoundException {
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);

        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfSourceDatasetOne(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                transferLearningController.addChromosomesToBestGenArray(getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInBestGenMethod()));

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetOne(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetOne(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetOne(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }

        transferLearningController.exportBestGen(startTime, runNumber);
        populationController.resetSets();
        this.dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.replaceChromosomes(transferLearningController.importBestGen(startTime, runNumber));

        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Testing data");
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnTrainingDataset(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunTargetSuccessful(true);
        }catch (Exception exception){
            run.setRunTargetSuccessful(false);
            exception.printStackTrace();
        }

        return run;
    }

    private Run runGPWithGPCRTransferLearning(int runNumber) throws FileNotFoundException {
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);

        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfSourceDatasetOne(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetOne(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetOne(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetOne(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }

        transferLearningController.exportGPCR(startTime, runNumber);
        populationController.resetSets();
        this.dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.addAliens(transferLearningController.importGPCR(startTime, runNumber));
        populationController.recreatePopulation();

        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training data");
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionTwo())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationTwo());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverTwo());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverTwo(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                List<Integer> crossbreedingIndexes = selectParents(ConfigController.getPercentOfCrossbreeding());
                for (int index: crossbreedingIndexes){
                    newChromosomes.add(populationController.crossbreedingChromosomes(index));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnTrainingDataset(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunTargetSuccessful(true);
        }
        catch (Exception exception){
            run.setRunTargetSuccessful(false);
            exception.printStackTrace();
        }

        return run;
    }

    private Run runGPWithPSTTransferLearning(int runNumber) throws FileNotFoundException {
        Run run = new Run();
        run.setRunNumber(runNumber);
        seed = random.nextLong();
        random.setSeed(seed);
        run.setSeed(seed);

        int indexOfBestChromosomes =findBestChromosomes();
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        int counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training on source data set 1");
        run.setStartTimeStampOfSourceDatasetOne(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetOne(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetOne(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetOne(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetOne(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceOneSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceOneSuccessful(false);
            exception.printStackTrace();
        }

        List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask1 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());
        dataController.chanceMod();

        populationController.recreatePopulation();
        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("Training on source data set 2");
        run.setStartTimeStampOfSourceDatasetTwo(System.currentTimeMillis());
        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnSourceDatasetTwo(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfSourceDatasetTwo(System.currentTimeMillis());
            run.setAccuracyOnSourceTrainingDatasetTwo(bestChromosomesAccuracy);
            run.setAccuracyOnSourceTestingDatasetTwo(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunSourceTwoSuccessful(true);
        }
        catch (Exception exception){
            run.setRunSourceTwoSuccessful(false);
            exception.printStackTrace();
        }

        List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask2 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());

        transferLearningController.exportPST(topPercentageOfPopulationForSourceTask1, topPercentageOfPopulationForSourceTask2, startTime, runNumber);

        populationController.resetSets();
        this.dataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        populationController.addFunctions(transferLearningController.importPST(startTime, runNumber));
        populationController.recreatePopulation();

        indexOfBestChromosomes =findBestChromosomes();
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);
        counterChange =0;
        System.out.println("Run "+runNumber+" seed: "+seed);
        System.out.println("testing data");
        run.setStartTimeStampOfTargetDataset(System.currentTimeMillis());

        try {
            int counter = 0;
            while ( bestChromosomesAccuracy < ConfigController.getMinAccuracy()){
                List<Chromosome> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
                newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getPercentOfReproductionOne())));

                List<Integer> mutationIndexes = selectParents(ConfigController.getPercentOfMutationOne());
                for (int index: mutationIndexes){
                    newChromosomes.add(populationController.mutationChromosomes(index));
                }

                List<Integer> crossoverIndexes = selectParents(ConfigController.getPercentOfCrossoverOne());
                for (int counter1 = 0; counter1 < ConfigController.getPercentOfCrossoverOne(); counter1+=2){
                    newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
                }

                populationController.setChromosomes(newChromosomes);

                oldBestChromosomesAccuracy = bestChromosomesAccuracy;
                indexOfBestChromosomes =findBestChromosomes();
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                System.out.println("Generations "+counter+" best chromosome's accuracy "+bestChromosomesAccuracy+"% Number of times the same: "+counterChange);
                run.setNumberOfGenerationsOnTrainingDataset(counter+1);
                counter = counter+1;
            }
            run.setStopTimeStampOfTargetDataset(System.currentTimeMillis());
            run.setAccuracyOnTargetTrainingDataset(bestChromosomesAccuracy);
            run.setAccuracyOnTargetTestingDataset(getAccuracyOnTestingDataset(indexOfBestChromosomes));
            run.setRunTargetSuccessful(true);
        }catch (Exception exception){
            run.setRunTargetSuccessful(false);
            exception.printStackTrace();
        }

        return run;
    }
}
