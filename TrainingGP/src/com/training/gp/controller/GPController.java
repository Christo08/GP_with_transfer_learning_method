package com.training.gp.controller;

import java.io.FileNotFoundException;
import java.util.Random;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;

    public GPController(String pathToData) throws FileNotFoundException {
        long seed = random.nextLong();
        random.setSeed(-5691349453815312671L);
        System.out.println("Seed: "+ seed);
    }

    public static Random getRandom() {
        return random;
    }

    public double fitnessOfChromosomes(int counter) {
        double numberOfCorrect =0;
        for (Map<String, Double> dataLine: dataController.getData()) {
            String chromosomesOutput = sdrsController.getClass(populationController.evaluateChromosomes(dataLine,counter));
            if (chromosomesOutput.equals(dataLine.get("ans").toString()))
                numberOfCorrect++;
        }
        return numberOfCorrect/((double)dataController.getData().size());
    }
}
