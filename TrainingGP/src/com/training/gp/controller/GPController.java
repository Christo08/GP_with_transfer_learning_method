package com.training.gp.controller;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Random;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;
    private PopulationController populationController;
    private SDRSController sdrsController;

    public GPController(String pathToData) throws FileNotFoundException {
        long seed = random.nextLong();
        random.setSeed(seed);
        System.out.println("Seed: "+ seed);

        this.dataController = new DataController(pathToData);
        this.populationController = new PopulationController();
        this.sdrsController = new SDRSController(this, dataController, populationController);
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
