package com.transfer.learning.gp.controllers.data;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.gp.GPController;
import com.transfer.learning.gp.controllers.gp.PopulationController;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class SourceTaskDataController {

    private List<Map<String, Double>> dataSet1;

    private List<Map<String, Double>> dataSet2;

    private static NumberFormat formatter = new DecimalFormat("#0.00");

    public SourceTaskDataController(String pathToData) throws FileNotFoundException {
        super();
        String pathToFile = pathToData+ ConfigController.getPathToTrainingDataset().get("WineQualityRed");
        File file = new File(pathToFile);
        Scanner reader = new Scanner(file);
        dataSet1 = new ArrayList<>();
        double numberOfLines =0;
        int counterOfLines = (int) (ConfigController.getSizeOfDataset().get("WineQualityRed"));
        while (reader.hasNextLine()){
            String line = reader.nextLine().trim();
            if (!line.isEmpty()){
                numberOfLines++;

                List<String> splitLine = Arrays.asList(line.split(","));
                Map<String, Double> dataLine = new HashMap<>();
                for (int counter =0; counter < splitLine.size(); counter++)
                {
                    double number = Double.parseDouble(splitLine.get(counter))-1;
                    if (counter == splitLine.size()-1)
                    {
                        dataLine.put("ans", number);
                        PopulationController.addClassToConsequentSet(String.valueOf(number));
                    }
                    else
                    {
                        String attributeName = String.valueOf((char) (65 + counter));
                        if (numberOfLines ==1)
                        {
                            PopulationController.addAttributeToMathSet(attributeName);
                        }
                        dataLine.put(attributeName, number);
                    }
                }
                dataSet1.add(dataLine);
            }
        }
        reader.close();

        pathToFile = pathToData+ConfigController.getPathToTrainingDataset().get("WineQualityWhite");
        file = new File(pathToFile);
        reader = new Scanner(file);
        dataSet2 = new ArrayList<>();
        numberOfLines =0;
        counterOfLines = (int) (ConfigController.getSizeOfDataset().get("WineQualityWhite"));
        while (reader.hasNextLine()){
            String line = reader.nextLine().trim();
            if (!line.isEmpty() ){
                numberOfLines++;

                List<String> splitLine = Arrays.asList(line.split(","));
                Map<String, Double> dataLine = new HashMap<>();
                for (int counter =0; counter < splitLine.size(); counter++)
                {
                    double number = Double.parseDouble(splitLine.get(counter))-1;
                    if (counter == splitLine.size()-1)
                    {
                        dataLine.put("ans", number);
                        PopulationController.addClassToConsequentSet(String.valueOf(number));
                    }
                    else
                    {
                        String attributeName = String.valueOf((char) (65 + counter));
                        if (numberOfLines ==1)
                        {
                            PopulationController.addAttributeToMathSet(attributeName);
                        }
                        dataLine.put(attributeName, number);
                    }
                }
                dataSet2.add(dataLine);
            }
        }
        reader.close();
    }

    public List<Map<String, Double>> getDataSet1() {
        return dataSet1;
    }

    public List<Map<String, Double>> getDataSet2() {
        return dataSet2;
    }

    private List<Integer> getLineNumberOfTrainingData(int totalNumberOfLines) {
        List<Integer> lineNumbers = new ArrayList<>();
        int lineNumber;
        while (lineNumbers.size() < totalNumberOfLines*ConfigController.getPercentOfTrainingData()){
            do {
                lineNumber = GPController.getRandom().nextInt(totalNumberOfLines);
            }while (lineNumbers.contains(lineNumber));
            lineNumbers.add(lineNumber);
        }
        return lineNumbers;
    }

    private static void printProgress(double current, int total, String dataSetName) {

        StringBuilder string = new StringBuilder(140);
        double percent = (current/(double) total)* 100;
        string.append('\r')
                .append(formatter.format(percent))
                .append("% of ")
                .append(dataSetName)
                .append(" dataset cleaned.");

        System.out.print(string);
    }
}
