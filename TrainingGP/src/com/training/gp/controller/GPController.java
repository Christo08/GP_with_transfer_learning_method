package com.training.gp.controller;

import java.io.FileNotFoundException;
import java.util.Random;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;

    public GPController(String pathToData) throws FileNotFoundException {
        this.dataController = new DataController(pathToData);
        long seed = random.nextLong();
        random.setSeed(seed);
        System.out.println("Seed: "+ seed);
    }

    public static Random getRandom() {
        return random;
    }
}
