package com.training.gp.controllers.data;

import com.training.gp.controllers.ConfigController;
import com.training.gp.controllers.gp.PopulationController;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TargetTaskDataController extends DataController {

    private List<Map<String, Double>> trainingDataSet;
    private int trainingSizeOfBatchForDataset;
    protected String dataSetTrainingName;

    private List<Map<String, Double>> testingDataSet;
    private int testingSizeOfBatchForDataset;
    protected String dataSetTestingName;

    public TargetTaskDataController(String pathToData, String dataSetName) throws FileNotFoundException {
        super();
        String pathToFile = pathToData+ ConfigController.getPathToTrainingDataset().get(dataSetName);
        File file = new File(pathToFile);
        Scanner reader = new Scanner(file);
        trainingDataSet = new LinkedList<>();
        double numberOfLines =0;
        dataSetTrainingName = dataSetName+"_tr";
        double counterOfLines = ConfigController.getSizeOfDataset().get(dataSetTrainingName);
        trainingSizeOfBatchForDataset = (int) Math.round(counterOfLines/(ConfigController.getNumberOfBatch()));
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
                        String attributeName = String.valueOf((char)(65+counter));
                        if (numberOfLines ==1)
                        {
                            PopulationController.addAttributeToFunctionSet(attributeName);
                        }
                        dataLine.put(attributeName, number);
                    }
                }
                trainingDataSet.add(dataLine);
                printProgress(numberOfLines, counterOfLines, dataSetName+" training");
            }
        }
        reader.close();
        System.out.println();

        pathToFile = pathToData+ConfigController.getPathToTestingDataset().get(dataSetName);
        file = new File(pathToFile);
        reader = new Scanner(file);
        dataSetTestingName = dataSetName+"_ts";
        this.dataSetName=dataSetTestingName;
        testingDataSet = new LinkedList<>();
        numberOfLines =0;
        counterOfLines = ConfigController.getSizeOfDataset().get(dataSetTestingName);
        testingSizeOfBatchForDataset = (int) Math.round(counterOfLines/(ConfigController.getNumberOfBatch()));
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
                        String attributeName = String.valueOf((char)(65+counter));
                        if (numberOfLines ==1)
                        {
                            PopulationController.addAttributeToFunctionSet(attributeName);
                        }
                        dataLine.put(attributeName, number);
                    }
                }
                testingDataSet.add(dataLine);
                printProgress(numberOfLines, counterOfLines, dataSetName+" testing");
            }
        }
        reader.close();
        System.out.println();
        chanceMod();
    }

    @Override
    public void chanceMod() {
        if (dataSetName.equals(dataSetName)){
            dataSet = testingDataSet;
            sizeOfBatchForDataset = testingSizeOfBatchForDataset;
            this.dataSetName=dataSetTestingName;
        }else{
            dataSet = trainingDataSet;
            sizeOfBatchForDataset = trainingSizeOfBatchForDataset;
            this.dataSetName=dataSetTrainingName;
        }
    }


}
