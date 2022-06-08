package com.training.gp.controller;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    private final static double stopBondOfSlots = 51;
    private final static int maxDepth =5;
    private final static int populationSize = 50;
    private final static double numberOfSlots = 101;
    private final static double startBondOfSlots = -50;
    private final static Map<String, Integer> numberOfUniqueAnswersForDataset =new HashMap<>(){{
        put("wineQualityRed",10);
        put("dryBeam",7);
        put("wineQualityWhite",10);
        put("mushroom",2);
        put("iris",3);
        put("seeds",3);
    }};

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static double getNumberOfSlots() {
        return numberOfSlots;
    }

    public static double getStartBondOfSlots() {
        return startBondOfSlots;

    public static double getStopBondOfSlots() {
        return stopBondOfSlots;
    }

    public static Map<String, Integer> getNumberOfUniqueAnswersForDataset() {
        return numberOfUniqueAnswersForDataset;
    }

    }
}
