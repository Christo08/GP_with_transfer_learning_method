package com.data.cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DataCleaner {

    static Map<String, String> irisClassMap;

    public static void main(String[] args) {
        String pathToRawDataFolder ="C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Raw";
        String pathToCleanDataFolder="C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Cleaned";

        System.out.println("Creating maps");
        initialiseMaps();
        System.out.println("Finished creating maps");

        System.out.println("Cleaning iris data set");
        cleanIrisDataSet(pathToRawDataFolder, pathToCleanDataFolder);
        System.out.println("Finished cleaning iris data set");
    }

    private static void initialiseMaps() {
        irisClassMap = new HashMap<>();
        irisClassMap.put("Iris-setosa","1");
        irisClassMap.put("Iris-versicolor","2");
        irisClassMap.put("Iris-virginica","3");
    }

    private static void cleanIrisDataSet(String pathToRawDataFolder, String pathToCleanDataFolder) {
        try {
            String pathsToRawIrisDataset = pathToRawDataFolder+"\\Iris\\iris.data";
            File irisFile = new File(pathsToRawIrisDataset);
            Scanner irisReader = new Scanner(irisFile);
            String output ="";
            while (irisReader.hasNextLine()){
                String line = irisReader.nextLine().trim();
                if (!line.isEmpty()){
                    List<String> splitLine = Arrays.asList(line.split(","));
                    splitLine.set(splitLine.size()-1, irisClassMap.get(splitLine.get(splitLine.size()-1)));
                    output += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                }
            }
            irisReader.close();

            String pathsToCleanIrisDataset = pathToCleanDataFolder+"\\Iris\\iris.txt";
            File cleanIrisFile = new File(pathsToCleanIrisDataset);
            cleanIrisFile.createNewFile();
            FileWriter irisWriter = new FileWriter(cleanIrisFile);
            irisWriter.write(output);
            irisWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open iris data set.");
        } catch (IOException e) {
            System.out.println("Can not create iris data set file.");
        }
    }
}
