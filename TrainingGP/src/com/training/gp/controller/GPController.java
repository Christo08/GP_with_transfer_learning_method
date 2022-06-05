package com.training.gp.controller;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;
    private PopulationController populationController;
    private NumberFormat formatter = new DecimalFormat("#0.000");

    public GPController(String pathToData) throws FileNotFoundException {
        long seed = random.nextLong();
        random.setSeed(-5691349453815312671L);
        System.out.println("Seed: "+ seed);

        this.dataController = new DataController(pathToData);
        this.populationController = new PopulationController();
    }

    public static Random getRandom() {
        return random;
    }
}
