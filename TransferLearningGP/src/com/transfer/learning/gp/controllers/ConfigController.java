package com.transfer.learning.gp.controllers;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    private final static int maxDepth =5;
    private final static int populationSize = 100;
    private final static int numberOfSlots = 40;
    private final static double startBondOfSlots = -20;
    private final static double stopBondOfSlots = 20;
    private final static Map<String, Integer> numberOfUniqueAnswersForDataset =new HashMap<>(){{
        put("Avila_tr",12);
        put("Avila_ts",12);
        put("DryBeam_tr",7);
        put("DryBeam_ts",7);
        put("Iris_tr",3);
        put("Iris_ts",3);
        put("Seeds_tr",3);
        put("Seeds_ts",3);
        put("wineQualityRed",10);
        put("wineQualityWhite",10);
    }};
    private final static Map<String, Integer> sizeOfDataset =new HashMap<>(){{
        put("Avila_tr",15649);
        put("Avila_ts",5217);
        put("DryBeam_tr",10208);
        put("DryBeam_ts",3103);
        put("Iris_tr",112);
        put("Iris_ts",38);
        put("Seeds_tr",157);
        put("Seeds_ts",53);
        put("wineQualityRed",1599);
        put("wineQualityWhite",4898);
    }};
    private final static Map<String, String> pathToTrainingDataset = new HashMap<>(){{
        put("Avila","\\Avila\\avila_tr.txt");
        put("DryBean","\\DryBeanDataset\\dryBeam_tr.txt");
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
    private final static int maxNumberOfGenerations = 1000;
    private final static double minAccuracy = 95;
    private final static int tournamentSize = 4;
    private final static int reproductionSize = 20;
    private final static int crossoverSize = 50;
    private final static int mutationSize = 30;
    private final static int numberOfGenerationsBeforeEvolveMap = 50;
    private final static int numberOfBatch = 4;
    private final static int numberOfRuns = 20;
    private static int maxDepthOfCrossover = 100;

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static int getNumberOfSlots() {
        return numberOfSlots;
    }

    public static double getStartBondOfSlots() {
        return startBondOfSlots;
    }

    public static double getStopBondOfSlots() {
        return stopBondOfSlots;
    }

    public static Map<String, Integer> getNumberOfUniqueAnswersForDataset() {
        return numberOfUniqueAnswersForDataset;
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

    public static int getMaxNumberOfGenerations() {
        return maxNumberOfGenerations;
    }

    public static double getMinAccuracy() {
        return minAccuracy;
    }

    public static int getTournamentSize() {
        return tournamentSize;
    }

    public static int getReproductionSize() {
        return reproductionSize;
    }

    public static int getCrossoverSize() {
        return crossoverSize;
    }

    public static int getMutationSize() {
        return mutationSize;
    }

    public static int getNumberOfGenerationsBeforeEvolveMap() {
        return numberOfGenerationsBeforeEvolveMap;
    }

    public static int getNumberOfBatch() {
        return numberOfBatch;
    }

    public static int getNumberOfRuns() {
        return numberOfRuns;
    }

    public static int getMaxDepthOfCrossover() {
        return maxDepthOfCrossover;
    }
}
