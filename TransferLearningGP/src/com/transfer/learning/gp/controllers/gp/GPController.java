package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.data.SourceTaskDataController;
import com.transfer.learning.gp.controllers.data.TargetTaskDataController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;
import com.transfer.learning.gp.data.objects.xml.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GPController {
    private static Random random = new Random();
    private TransferLearningController transferLearningController;

    private TargetTaskDataController targetTaskDataController;
    private SourceTaskDataController sourceTaskDataController;
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

    public List<ChromosomeWrapper> getTopPercentageOfPopulation(int size) {

        return populationController.getTopChromosomes(size);
    }

    public String getPathToData() {
        return pathToData;
    }

    private List<Integer> selectParents(int numberOfParentsNeeded) {
        List<Integer> indexes = new ArrayList<>();
        for (int counter =0; counter< numberOfParentsNeeded; counter++) {
            int newIndexes;
            do {
                newIndexes = selectParent();
            }while (indexes.contains(newIndexes));
            indexes.add(newIndexes);
        }
        return indexes;
    }

    private int findBestChromosomes(List<Map<String, Double>> dataLines) {
        double maxFitness =0;
        double fitness;
        int chromosomesIndex=0;
        for (int counter = 0; counter < ConfigController.getPopulationSize(); counter++){

            double numberOfCorrect =0;

            for (Map<String, Double> dataLine: dataLines) {
                if (populationController.evaluateChromosomes(dataLine,counter) == dataLine.get("ans"))
                    numberOfCorrect++;
            }
            fitness= numberOfCorrect/((double)dataLines.size()) * 100;

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

    private double getAccuracyOnTestingDataset(int counter, List<Map<String, Double>> trainingDataSet){
        double numberOfCorrect =0;
        for (Map<String, Double> dataLine: trainingDataSet) {
            if (populationController.evaluateChromosomes(dataLine,counter) == dataLine.get("ans"))
                numberOfCorrect++;
        }
        return numberOfCorrect/(double)trainingDataSet.size() * 100;
    }

    private void convertObjectToXML(Experiment experiment, boolean hasTransferLearning, String transferLearningType) throws JAXBException {
        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(Experiment.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Write to File
        if (hasTransferLearning)
            marshaller.marshal(experiment, new File(pathToData +"\\Experiment\\"+dataSetName+"\\"+startTime+"_TL_"+transferLearningType+"_Experiment.xml"));
        else
            marshaller.marshal(experiment, new File(pathToData +"\\Experiment\\"+dataSetName+"\\"+startTime+"_Experiment.xml"));
    }

    public void trainWithoutTransferLearning(String dataSetName) throws FileNotFoundException, JAXBException {
        this.dataSetName = dataSetName;

        this.startTime = System.currentTimeMillis();
        Experiment experiment = new Experiment(dataSetName);
        for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
            System.out.println("Start run "+runCounter);
            experiment.addRun(runGPWithoutTransferLearning(runCounter));
            convertObjectToXML(experiment, false,"");
            System.out.println("End run "+runCounter);
        }
    }

    public void trainWithTransferLearning(String dataSetName) throws IOException, JAXBException {
        System.out.println("Please set a transfer learning method, by entering the number:");
        System.out.println("1 Full tree");
        System.out.println("2 Sub-tree");
        System.out.println("3 Best gen");
        System.out.println("4 GPCR");
        System.out.println("5 PST");
        System.out.println("6 All");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        transferLearningMod = Integer.parseInt(reader.readLine());
        this.startTime = System.currentTimeMillis();

        this.dataSetName = dataSetName;

        if (transferLearningMod == 6){
            transferLearningMod=1;
            while (transferLearningMod < 6){
                if (transferLearningMod == 1){
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Full tree",transferLearningController.getPathToFolder(transferLearningMod));
                    Experiment experiment = new Experiment(dataSetName,transferLearningMethod);
                    for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                        System.out.println("Start run "+runCounter+" for Full tree");
                        experiment.addRun(runGPWithFullTreeTransferLearning(runCounter));
                        convertObjectToXML(experiment,true,"FullTree");
                        System.out.println("End run "+runCounter+" for Full tree");
                    }
                }
                else if (transferLearningMod == 2){
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Sub-tree",transferLearningController.getPathToFolder(transferLearningMod));
                    Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                    for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                        System.out.println("Start run "+runCounter+" for Sub-tree");
                        experiment.addRun(runGPWithSubTreeTransferLearning(runCounter));
                        convertObjectToXML(experiment,true,"SubTree");
                        System.out.println("End run "+runCounter+" for Sub-tree");
                    }
                }
                else if (transferLearningMod == 3){
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Best gen",transferLearningController.getPathToFolder(transferLearningMod));
                    Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                    for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                        System.out.println("Start run "+runCounter+" for BestGen");
                        experiment.addRun(runGPWithBestGenTransferLearning(runCounter));
                        convertObjectToXML(experiment,true,"BestGen");
                        System.out.println("End run "+runCounter+" for BestGen");
                    }
                }
                else if (transferLearningMod == 4){
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("GPCR",transferLearningController.getPathToFolder(transferLearningMod));
                    Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                    for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                        System.out.println("Start run "+runCounter+" for GPCR");
                        experiment.addRun(runGPWithGPCRTransferLearning(runCounter));
                        convertObjectToXML(experiment,true,"GPCR");
                        System.out.println("End run "+runCounter+" for GPCR");
                    }
                }
                else if (transferLearningMod == 5){
                    TransferLearningMethod transferLearningMethod = new TransferLearningMethod("PST", transferLearningController.getPathToFolder(transferLearningMod));
                    Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                    for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++) {
                        System.out.println("Start run "+runCounter+" for PST");
                        experiment.addRun(runGPWithPSTTransferLearning(runCounter));
                        convertObjectToXML(experiment, true, "PST");
                        System.out.println("End run "+runCounter+" for PST");
                    }
                }
                this.startTime = System.currentTimeMillis();
                transferLearningMod++;
            }
        }
        else if (transferLearningMod == 1){
            TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Full tree",transferLearningController.getPathToFolder(transferLearningMod));
            Experiment experiment = new Experiment(dataSetName,transferLearningMethod);
            for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                System.out.println("Start run "+runCounter);
                experiment.addRun(runGPWithFullTreeTransferLearning(runCounter));
                convertObjectToXML(experiment,true,"FullTree");
                System.out.println("End run "+runCounter);
            }
        }
        else if (transferLearningMod == 2){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Sub-tree",transferLearningController.getPathToFolder(transferLearningMod));
                Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    System.out.println("Start run "+runCounter);
                    experiment.addRun(runGPWithSubTreeTransferLearning(runCounter));
                    convertObjectToXML(experiment,true,"SubTree");
                    System.out.println("End run "+runCounter);
                }
            }
        else if (transferLearningMod == 3){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("Best gen",transferLearningController.getPathToFolder(transferLearningMod));
                Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    System.out.println("Start run "+runCounter);
                    experiment.addRun(runGPWithBestGenTransferLearning(runCounter));
                    convertObjectToXML(experiment,true,"BestGen");
                    System.out.println("End run "+runCounter);
                }
            }
        else if (transferLearningMod == 4){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("GPCR",transferLearningController.getPathToFolder(transferLearningMod));
                Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++){
                    System.out.println("Start run "+runCounter);
                    experiment.addRun(runGPWithGPCRTransferLearning(runCounter));
                    convertObjectToXML(experiment,true,"GPCR");
                    System.out.println("End run "+runCounter);
                }
            }
        else if (transferLearningMod == 5){
                TransferLearningMethod transferLearningMethod = new TransferLearningMethod("PST", transferLearningController.getPathToFolder(transferLearningMod));
                Experiment experiment = new Experiment(dataSetName,transferLearningMethod);

                for (int runCounter = 1; runCounter <= ConfigController.getNumberOfRuns(); runCounter++) {
                    System.out.println("Start run "+runCounter);
                    experiment.addRun(runGPWithPSTTransferLearning(runCounter));
                    convertObjectToXML(experiment, true, "PST");
                    System.out.println("End run "+runCounter);
                }
            }
    }

    private Run runGPWithoutTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        long stopTimeStamp;
        int genCounter = 0;

        populationController.resetSets();

        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);
        long startTimeStamp = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        Run run = new Run(runNumber, seed, startTimeStamp);
        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
                targetDataSet.setNumberOfGenerations(genCounter);
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }

    private Run runGPWithFullTreeTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        int genCounter = 0;
        long stopTimeStamp;

        populationController.resetSets();
        sourceTaskDataController = new SourceTaskDataController(pathToData+"\\Cleaned");

        long startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);


        Run run = new Run(runNumber, seed, startTimeStampSource);
        SourceDataSet sourceDataSet = new SourceDataSet(startTimeStampSource);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(bestChromosomesAccuracy);
            sourceDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(0);
            sourceDataSet.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSet.setNumberOfGenerations(genCounter);
        sourceDataSet.setStopTimeStamp(stopTimeStamp);

        run.setSourceDataSetOne(sourceDataSet);
        if (sourceDataSet.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        transferLearningController.exportFullTree(startTimeStampSource, runNumber);
        populationController.resetSets();
        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);

        long startTimeStamp = System.currentTimeMillis();
        populationController.replaceChromosomes(transferLearningController.importFullTree(startTimeStampSource, runNumber));

        indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);

        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }

    private Run runGPWithSubTreeTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        int genCounter = 0;
        long stopTimeStamp;

        populationController.resetSets();
        sourceTaskDataController = new SourceTaskDataController(pathToData+"\\Cleaned");

        long startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        Run run = new Run(runNumber, seed, startTimeStampSource);
        SourceDataSet sourceDataSet = new SourceDataSet(startTimeStampSource);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(bestChromosomesAccuracy);
            sourceDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(0);
            sourceDataSet.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSet.setNumberOfGenerations(genCounter);
        sourceDataSet.setStopTimeStamp(stopTimeStamp);

        run.setSourceDataSetOne(sourceDataSet);
        if (sourceDataSet.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        transferLearningController.exportSubTree(startTimeStampSource, runNumber);
        populationController.resetSets();
        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);

        long startTimeStamp = System.currentTimeMillis();
        populationController.replaceChromosomes(transferLearningController.importSubTree(startTimeStampSource, runNumber));

        indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);

        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }

    private Run runGPWithBestGenTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        int genCounter = 0;
        long stopTimeStamp;

        populationController.resetSets();
        sourceTaskDataController = new SourceTaskDataController(pathToData+"\\Cleaned");

        long startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);


        Run run = new Run(runNumber, seed, startTimeStampSource);
        SourceDataSet sourceDataSet = new SourceDataSet(startTimeStampSource);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
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
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(bestChromosomesAccuracy);
            sourceDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(0);
            sourceDataSet.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSet.setNumberOfGenerations(genCounter);
        sourceDataSet.setStopTimeStamp(stopTimeStamp);

        run.setSourceDataSetOne(sourceDataSet);
        if (sourceDataSet.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        transferLearningController.exportBestGen(startTimeStampSource, runNumber);
        populationController.resetSets();
        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);

        long startTimeStamp = System.currentTimeMillis();
        populationController.replaceChromosomes(transferLearningController.importBestGen(startTimeStampSource, runNumber));

        indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }

    private Run runGPWithGPCRTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        int genCounter = 0;
        long stopTimeStamp;

        populationController.resetSets();
        sourceTaskDataController = new SourceTaskDataController(pathToData+"\\Cleaned");

        long startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);


        Run run = new Run(runNumber, seed, startTimeStampSource);
        SourceDataSet sourceDataSet = new SourceDataSet(startTimeStampSource);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(bestChromosomesAccuracy);
            sourceDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSet.setAccuracy(0);
            sourceDataSet.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSet.setNumberOfGenerations(genCounter);
        sourceDataSet.setStopTimeStamp(stopTimeStamp);

        run.setSourceDataSetOne(sourceDataSet);
        if (sourceDataSet.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        transferLearningController.exportGPCR(startTimeStampSource, runNumber);
        populationController.resetSets();
        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);

        long startTimeStamp = System.currentTimeMillis();
        populationController.addAliens(transferLearningController.importGPCR(startTimeStampSource, runNumber));
        populationController.createPopulation();

        indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }
                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }

    private Run runGPWithPSTTransferLearning(int runNumber) throws FileNotFoundException {
        seed = random.nextLong();
        random.setSeed(seed);

        int counterChange =0;
        int genCounter = 0;
        long stopTimeStamp;

        populationController.resetSets();
        sourceTaskDataController = new SourceTaskDataController(pathToData+"\\Cleaned");

        long startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();

        int indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        Run run = new Run(runNumber, seed, startTimeStampSource);
        SourceDataSet sourceDataSetOne = new SourceDataSet(startTimeStampSource);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet1());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }

                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSetOne.setAccuracy(bestChromosomesAccuracy);
            sourceDataSetOne.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSetOne.setAccuracy(0);
            sourceDataSetOne.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSetOne.setNumberOfGenerations(genCounter);
        sourceDataSetOne.setStopTimeStamp(stopTimeStamp);

        run.setSourceDataSetOne(sourceDataSetOne);
        if (sourceDataSetOne.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask1 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());
        sourceDataSetOne.setStopTimeStamp(System.currentTimeMillis());
        run.setSourceDataSetOne(sourceDataSetOne);

        startTimeStampSource = System.currentTimeMillis();
        populationController.createPopulation();
        indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet2());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        SourceDataSet sourceDataSetTwo = new SourceDataSet(startTimeStampSource);
        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(sourceTaskDataController.getDataSet2());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }

                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSetTwo.setAccuracy(bestChromosomesAccuracy);
            sourceDataSetTwo.setHasError(false);
        }
        catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            sourceDataSetTwo.setAccuracy(0);
            sourceDataSetTwo.setHasError(true);
            exception.printStackTrace();
        }
        sourceDataSetTwo.setNumberOfGenerations(genCounter);

        run.setSourceDataSetTwo(sourceDataSetTwo);
        if (sourceDataSetTwo.isHasError()){
            run.setStopTimeStamp(System.currentTimeMillis());
            return run;
        }

        List<ChromosomeWrapper> topPercentageOfPopulationForSourceTask2 = getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInGPCRMethod());
        sourceDataSetTwo.setStopTimeStamp(stopTimeStamp);
        run.setSourceDataSetTwo(sourceDataSetTwo);

        transferLearningController.exportPST(topPercentageOfPopulationForSourceTask1, topPercentageOfPopulationForSourceTask2, startTimeStampSource, runNumber);
        populationController.resetSets();
        targetTaskDataController = new TargetTaskDataController(pathToData+"\\Cleaned",dataSetName);

        long startTimeStamp = System.currentTimeMillis();
        populationController.addFunctions(transferLearningController.importPST(startTimeStampSource, runNumber));
        populationController.createPopulation();

        indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
        bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

        counterChange =0;
        genCounter = 0;

        TargetDataSet targetDataSet = new TargetDataSet(startTimeStamp);

        try {
            while ( bestChromosomesAccuracy < ConfigController.getMaxAccuracy()){
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
                indexOfBestChromosomes =findBestChromosomes(targetTaskDataController.getTrainingDataSet());
                bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(indexOfBestChromosomes);

                if ((bestChromosomesAccuracy + ConfigController.getPadding() >= oldBestChromosomesAccuracy ) &&
                        (bestChromosomesAccuracy - ConfigController.getPadding() <= oldBestChromosomesAccuracy ))
                    counterChange++;
                else
                    counterChange =0;
                if (counterChange>= ConfigController.getNumberOfSameBeforeEnding()) {
                    break;
                }

                genCounter = genCounter+1;
            }
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(bestChromosomesAccuracy);
            targetDataSet.setTestingAccuracy(getAccuracyOnTestingDataset(indexOfBestChromosomes,targetTaskDataController.getTestingDataSet()));
            targetDataSet.setHasError(false);
        }catch (Exception exception){
            stopTimeStamp = System.currentTimeMillis();
            targetDataSet.setTrainingAccuracy(0);
            targetDataSet.setTestingAccuracy(0);
            targetDataSet.setHasError(true);
            exception.printStackTrace();
        }

        targetDataSet.setNumberOfGenerations(genCounter);
        targetDataSet.setStopTimeStamp(stopTimeStamp);

        run.setStopTimeStamp(stopTimeStamp);
        run.setTargetDataSet(targetDataSet);
        return run;
    }
}
