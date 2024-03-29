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
            do {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Do you want to set the seed? 1 yes and 2 no");
                long seed;
                if (Integer.parseInt(reader.readLine()) == 1){
                    System.out.println("Please enter the seed:");
                    seed = Long.parseLong(reader.readLine());
                }else{
                    seed = GPController.getRandom().nextLong();
                }
                System.out.println("Seed: "+seed);
                System.out.println("Please enter 1 to train the gp with transfer learning, 2 without transfer learning or 0 to exit");
                mod = Integer.parseInt(reader.readLine());

                String datasetNames = "";
                String datasetName;
                int counter = 1;
                List<String> keys = new ArrayList<>(ConfigController.getSizeOfDataset().keySet());
                keys = keys.stream()
                           .filter(key -> !key.contains("Wine"))
                           .sorted(Comparator.naturalOrder())
                           .collect(Collectors.toList());
                for (String key:keys) {
                    datasetNames += counter+" "+key+"\n";
                    counter++;
                }
                System.out.println("Please enter a number to select a dataset:");
                System.out.print(datasetNames);
                datasetName = keys.get(Integer.parseInt(reader.readLine())-1);

                GPController gpController = new GPController(args[0], seed);
                if (mod == 1){
                    gpController.trainWithTransferLearning(datasetName);
                }else {
                    gpController.trainWithoutTransferLearning(datasetName);
                }
            }while (mod ==0);
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
