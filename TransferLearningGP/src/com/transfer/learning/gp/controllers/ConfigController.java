package com.transfer.learning.gp.controllers;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    //Experiment config
    //Max depth of newly created trees
    private final static int maxDepth =5;
    //Size of the population
    private final static int populationSize = 200;
    //Min accuracy of final classifier before GP stops
    private final static double minAccuracy = 95;
    //Number of times the accuracy can be in a bond
    private final static int numberOfSameBeforeEnding = 25;
    //The bonds. See numberOfSameBeforeEnding
    private final static double padding = 1;
    //Number of runs in an experiment
    private final static int numberOfRuns = 20;
    //Size of tournament select
    private final static int tournamentSize = 8;
    //Percent of genetic operators used
    private final static double percentOfReproductionOne = 0.1;
    private final static double percentOfCrossoverOne = 0.3;
    private final static double percentOfMutationOne = 0.6;
    private final static double percentOfReproductionTwo = 0.1;
    private final static double percentOfCrossoverTwo = 0.25;
    private final static double percentOfMutationTwo = 0.25;
    private final static double percentOfCrossbreeding=0.4;
    //Crossover can only be applied before this depth.
    private final static int maxDepthOfCrossover = 100;
    //Transfer learning method config
    //Percent of the population which is saved
    private final static double percentOfChromosomeToSaveInFullTreeMethod = 0.5;
    private final static double percentOfChromosomeToSaveInSubTreeMethod = 0.5;
    private final static double percentOfChromosomeToSaveInGenMethod = 0.5;
    private final static double percentOfChromosomeToSaveInGPCRMethod = 0.5;
    //Other config
    //Size of data set
    private final static Map<String, Integer> sizeOfDataset =new HashMap<>(){{
        put("Avila_ts",15649);
        put("Avila_tr",5217);
        put("DryBeam_ts",10208);
        put("DryBeam_tr",3103);
        put("Iris_ts",112);
        put("Iris_tr",38);
        put("Seeds_ts",157);
        put("Seeds_tr",53);
        put("wineQualityRed",1599);
        put("wineQualityWhite",4898);
    }};
    //Paths to data sets
    private final static Map<String, String> pathToTrainingDataset = new HashMap<>(){{
        put("Avila","\\Avila\\avila_tr.txt");
        put("DryBeam","\\DryBeanDataset\\dryBeam_tr.txt");
        put("Iris","\\Iris\\iris_tr.txt");
        put("Seeds","\\Seeds\\seeds_tr.txt");
        put("WineQualityRed","\\WineRed\\wineQualityRed.txt");
        put("WineQualityWhite","\\WineWhite\\wineQualityWhite.txt");
    }};
    private final static Map<String, String> pathToTestingDataset = new HashMap<>(){{
        put("Avila","\\Avila\\avila_ts.txt");
        put("DryBeam","\\DryBeanDataset\\dryBeam_ts.txt");
        put("Iris","\\Iris\\iris_ts.txt");
        put("Seeds","\\Seeds\\seeds_ts.txt");
    }};

    private final static int depthOfPSTTree = 5;

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static Map<String, Integer> getSizeOfDataset() {
        return sizeOfDataset;
    }

    public static Map<String, String> getPathToTrainingDataset() {
        return pathToTrainingDataset;
    }

    public static Map<String, String> getPathToTestingDataset() {
        return pathToTestingDataset;
    }

    public static double getMinAccuracy() {
        return minAccuracy;
    }

    public static int getTournamentSize() {
        return tournamentSize;
    }

    public static int getPercentOfReproductionOne() {
        return (int) Math.round(percentOfReproductionOne * populationSize);
    }

    public static int getPercentOfCrossoverOne() {
        return (int) Math.round(percentOfCrossoverOne * populationSize);
    }

    public static int getPercentOfMutationOne() {
        return  (int) Math.round(percentOfMutationOne * populationSize);
    }

    public static int getPercentOfReproductionTwo() {
        return (int) Math.round(percentOfReproductionTwo * populationSize);
    }

    public static int getPercentOfCrossoverTwo() {
        return (int) Math.round(percentOfCrossoverTwo * populationSize);
    }

    public static int getPercentOfMutationTwo() {
        return  (int) Math.round(percentOfMutationTwo * populationSize);
    }

    public static int getPercentOfCrossbreeding() {
        return  (int) Math.round(percentOfCrossbreeding * populationSize);
    }

    public static int getNumberOfRuns() {
        return numberOfRuns;
    }

    public static int getMaxDepthOfCrossover() {
        return maxDepthOfCrossover;
    }

    public static int getNumberOfSameBeforeEnding() {
        return numberOfSameBeforeEnding;
    }

    public static double getPadding() {
        return padding;
    }

    public static int getPercentOfChromosomeToSaveInFullTreeMethod() {
        return  (int) Math.round(percentOfChromosomeToSaveInFullTreeMethod * populationSize);
    }

    public static int getPercentOfChromosomeToSaveInSubTreeMethod() {
        return  (int) Math.round(percentOfChromosomeToSaveInSubTreeMethod * populationSize);
    }

    public static int getPercentOfChromosomeToSaveInBestGenMethod() {
        return  (int) Math.round(percentOfChromosomeToSaveInGenMethod * populationSize);
    }

    public static int getPercentOfChromosomeToSaveInGPCRMethod() {
        return  (int) Math.round(percentOfChromosomeToSaveInGPCRMethod * populationSize);
    }

    public static int getDepthOfPSTTree() {
        return depthOfPSTTree;
    }
}
