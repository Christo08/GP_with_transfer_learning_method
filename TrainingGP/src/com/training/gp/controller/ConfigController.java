package com.training.gp.controller;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    private final static int maxDepth =5;
    private final static int populationSize = 100;
    private final static int numberOfSlots = 100;
    private final static double startBondOfSlots = -50;
    private final static double stopBondOfSlots = 50;
    private final static Map<String, Integer> numberOfUniqueAnswersForDataset =new HashMap<>(){{
        put("avila_tr",12);
        put("avila_ts",12);
        put("dryBeam_tr",7);
        put("dryBeam_ts",7);
        put("iris_tr",3);
        put("iris_ts",3);
        put("seeds_tr",3);
        put("seeds_ts",3);
        put("wineQualityRed",10);
        put("wineQualityWhite",10);
    }};
    private final static Map<String, Integer> sizeOfDataset =new HashMap<>(){{
        put("avila_tr",15649);
        put("avila_ts",5217);
        put("dryBeam_tr",10208);
        put("dryBeam_ts",3103);
        put("iris_tr",112);
        put("iris_ts",38);
        put("seeds_tr",157);
        put("seeds_ts",53);
        put("wineQualityRed",1599);
        put("wineQualityWhite",4898);
    }};
    private final static Map<String, String> pathToDataset = new HashMap<>(){{
        put()
    }};
    private final static int maxNumberOfGenerations = 1000;
    private final static double minAccuracy = 90;
    private final static int tournamentSize = 4;
    private final static int reproductionSize = 20;
    private final static int crossoverSize = 50;
    private final static int mutationSize = 30;
    private final static int numberOfGenerationsToEvolveMap = 10;
    private final static int numberOfBatch = 4;

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

    public static int getNumberOfGenerationsToEvolveMap() {
        return numberOfGenerationsToEvolveMap;
    }

    public static int getNumberOfBatch() {
        return numberOfBatch;
    }
}
