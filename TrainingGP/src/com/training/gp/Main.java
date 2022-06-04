package com.training.gp;

import com.training.gp.controller.Chromosomes;
import com.training.gp.controller.ConfigController;
import com.training.gp.controller.GPController;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        try {
            GPController gpController = new GPController("C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Cleaned\\Wine\\wineQualityWhite.txt");

            Chromosomes growChromosomes = new Chromosomes(ConfigController.getMaxDepth(),false);
            System.out.println("Grow method constructors: "+growChromosomes);

            Chromosomes fullChromosomes = new Chromosomes(ConfigController.getMaxDepth(), true);
            System.out.println("Full method constructors: "+fullChromosomes);
        } catch (FileNotFoundException e) {
            System.out.println("Can not reader the file.");
            e.printStackTrace();
        }
    }
}
