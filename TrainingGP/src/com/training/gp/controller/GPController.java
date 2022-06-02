package com.training.gp.controller;

import java.io.FileNotFoundException;

public class GPController {
    private DataController dataController;

    public GPController(String pathToData) throws FileNotFoundException {
        this.dataController = new DataController(pathToData);
    }
}
