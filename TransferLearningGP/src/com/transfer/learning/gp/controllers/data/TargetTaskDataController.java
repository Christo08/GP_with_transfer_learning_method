package com.transfer.learning.gp.controllers.data;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.gp.GPController;
import com.transfer.learning.gp.controllers.gp.PopulationController;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class TargetTaskDataController {

    private List<Map<String, Double>> trainingDataSet;
    private List<Map<String, Double>> testingDataSet;
    private static NumberFormat formatter = new DecimalFormat("#0.00");

    public TargetTaskDataController(String pathToData, String dataSetName) throws FileNotFoundException {
        String pathToFile = pathToData+ ConfigController.getPathToTrainingDataset().get(dataSetName);
        File file = new File(pathToFile);
        Scanner reader = new Scanner(file);
        int numberOfLines =0;
        int counterOfLines = ConfigController.getSizeOfDataset().get(dataSetName);
        List<Integer> trainingDataLines = getLineNumberOfTrainingData(counterOfLines);
        trainingDataSet = new ArrayList<>(trainingDataLines.size());
        testingDataSet = new ArrayList<>(counterOfLines-trainingDataLines.size());
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
                        dataLine.put("ans", number-1);
                        PopulationController.addClassToConsequentSet(String.valueOf(number-1));
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
                if(trainingDataLines.contains(numberOfLines))
                    trainingDataSet.add(dataLine);
                else
                    testingDataSet.add(dataLine);
            }
        }
        reader.close();
    }

    public List<Map<String, Double>> getTrainingDataSet() {
        return trainingDataSet;
    }

    public List<Map<String, Double>> getTestingDataSet() {
        return testingDataSet;
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
