package com.training.gp.controller;

public class ConfigController {
    private static int maxDepth =5;
    private static int populationSize = 100000;

    public static int getMaxDepth() {
        return maxDepth;
    }

    public static int getPopulationSize() {
        return populationSize;
    }
}
