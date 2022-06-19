package com.transfer.learning.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.gp.GPController;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        try {
            int mod;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please enter 1 to train the gp and export the data, 2 to train the gp with transfer learning or 3 without transfer learning");
            mod = Integer.parseInt(reader.readLine());

            String datasetNames = "";
            String datasetName = "WineQualityWhite";
            if (mod != 1){
                int counter = 1;
                List<String> keys = new ArrayList<>(ConfigController.getPathToTestingDataset().keySet());
                keys = keys.stream()
                        .sorted(Comparator.naturalOrder())
                        .collect(Collectors.toList());
                for (String key:keys) {
                    datasetNames += counter+" "+key+"\n";
                    counter++;
                }
                System.out.println("Please enter a number to select a dataset:");
                System.out.print(datasetNames);
                datasetName = keys.get(Integer.parseInt(reader.readLine())-1);
            }


            GPController gpController = new GPController(args[0], datasetName, mod);
            if (mod == 1){
                gpController.exportData();
            }else if (mod == 2){
                gpController.importData();
            }else{
                gpController.experiment();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can not reader the file.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
