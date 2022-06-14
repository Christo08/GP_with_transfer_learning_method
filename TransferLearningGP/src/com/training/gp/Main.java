package com.training.gp;

import com.training.gp.controllers.ConfigController;
import com.training.gp.controllers.gp.GPController;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            int mod =1;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter 1 to train the gp with transfer learning or 2 without transfer learning");
            mod = Integer.parseInt(reader.readLine());

            String datasetNames = "";
            int counter = 1;
            List<String> keys = new ArrayList<>(ConfigController.getPathToTestingDataset().keySet());
            for (String datasetName:keys) {
                datasetNames += counter+" "+datasetName+"\n";
                counter++;
            }

            System.out.println("Please enter a number to select a dataset:");
            System.out.print(datasetNames);
            String datasetName = keys.get(Integer.parseInt(reader.readLine()));

            GPController gpController = new GPController(args[0], datasetName, mod);
            gpController.evolveAnswers();
        } catch (FileNotFoundException e) {
            System.out.println("Can not reader the file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}