package com.training.gp;

import com.training.gp.controller.GPController;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        try {
            GPController gpController = new GPController("C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Cleaned\\Wine\\wineQualityWhite.txt");

        } catch (FileNotFoundException e) {
            System.out.println("Can not reader the file.");
            e.printStackTrace();
        }
    }
}
