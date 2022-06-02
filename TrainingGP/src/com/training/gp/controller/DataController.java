package com.training.gp.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class DataController {
    private List<Map<String, Double>> data;
    private NumberFormat formatter = new DecimalFormat("#0.00");

    public DataController(String pathToFile) throws FileNotFoundException {
        File file = new File(pathToFile);
        Scanner reader = new Scanner(file);
        data = new LinkedList<>();
        double counterOfLines = 0;
        if (pathToFile.contains("Red")){
            counterOfLines = 1599;
        }
        else if (pathToFile.contains("White")){
            counterOfLines = 4898;
        }
        double numberOfLines =0;
        String fileName = pathToFile.substring(pathToFile.lastIndexOf("\\")+1, pathToFile.lastIndexOf("."));
        while (reader.hasNextLine()){
            String line = reader.nextLine().trim();
            if (!line.isEmpty()){
                numberOfLines++;

                List<String> splitLine = Arrays.asList(line.split(","));
                Map<String, Double> dataLine = new HashMap<>();
                for (int counter =0; counter < splitLine.size(); counter++)
                {
                    double number = Double.parseDouble(splitLine.get(counter));
                    if (counter == splitLine.size()-1)
                    {
                        dataLine.put("ans", number);
                    }
                    else
                    {
                        dataLine.put(String.valueOf((char)(65+counter)), number);
                    }
                }
                data.add(dataLine);
                printProgress(numberOfLines, counterOfLines, fileName);
            }
        }
        reader.close();
        System.out.println();
    }

    public List<Map<String, Double>> getData() {
        return data;
    }

    private void printProgress(double current, double total, String dataSetName) {

        StringBuilder string = new StringBuilder(140);
        double percent = (current/total)* 100;
        string.append('\r')
                .append(formatter.format(percent))
                .append("% of ")
                .append(dataSetName)
                .append(" dataset loaded.");

        System.out.print(string);
    }
}
