package com.transfer.learning.gp.controllers.data;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.gp.PopulationController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SourceTaskDataController extends DataController {

    private List<Map<String, Double>> dataSet1;
    private String dataSet1Name;

    private List<Map<String, Double>> dataSet2;
    private String dataSet2Name;

    public SourceTaskDataController(String pathToData) throws FileNotFoundException {
        super();
        String pathToFile = pathToData+ ConfigController.getPathToTrainingDataset().get("WineQualityRed");
        File file = new File(pathToFile);
        Scanner reader = new Scanner(file);
        dataSet1 = new LinkedList<>();
        double numberOfLines =0;
        dataSet1Name = pathToFile.substring(pathToFile.lastIndexOf("\\")+1, pathToFile.lastIndexOf("."));
        double counterOfLines = ConfigController.getSizeOfDataset().get(dataSet1Name);
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
                printProgress(numberOfLines, counterOfLines, dataSet1Name);
            }
        }
        reader.close();
        System.out.println();

        pathToFile = pathToData+ConfigController.getPathToTrainingDataset().get("WineQualityWhite");
        file = new File(pathToFile);
        reader = new Scanner(file);
        dataSet2 = new LinkedList<>();
        numberOfLines =0;
        dataSet2Name = pathToFile.substring(pathToFile.lastIndexOf("\\")+1, pathToFile.lastIndexOf("."));
        counterOfLines = ConfigController.getSizeOfDataset().get(dataSet2Name);
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
                dataSet2.add(dataLine);
                printProgress(numberOfLines, counterOfLines, dataSet2Name);
            }
        }
        reader.close();
        System.out.println();
        dataSetName = dataSet2Name;
        chanceMod();
    }

    @Override
    public void chanceMod() {
        if (dataSetName.equals(dataSet1Name)){
            dataSetName = dataSet2Name;
            dataSet = dataSet2;
        }else{
            dataSetName = dataSet1Name;
            dataSet = dataSet1;
        }
    }
}
