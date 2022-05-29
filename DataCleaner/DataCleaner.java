package com.data.cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class DataCleaner {

    static Map<String, String> dryBeamClassesMap;
    static Map<String, String> irisClassesMap;
    static NumberFormat formatter;

    public static void main(String[] args) {
        String pathToRawDataFolder ="C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Raw";
        String pathToCleanDataFolder="C:\\Users\\User\\OneDrive\\tuks\\honors\\year 2\\COS 700\\Research\\Data\\Cleaned";
        formatter = new DecimalFormat("#0.00");

        System.out.println("Creating maps");
        initialiseMaps();
        System.out.println("Finished creating maps");

        System.out.println("Cleaning dry beam data set");
        cleanDryBeamDataSet(pathToRawDataFolder, pathToCleanDataFolder);
        System.out.println("\nFinished cleaning dry beam data set");

        System.out.println("Cleaning iris data set");
        cleanIrisDataSet(pathToRawDataFolder, pathToCleanDataFolder);
        System.out.println("\nFinished cleaning iris data set");
    }

    private static void initialiseMaps() {
        dryBeamClassesMap = new HashMap<>();
        dryBeamClassesMap.put("SEKER","1");
        dryBeamClassesMap.put("BARBUNYA","2");
        dryBeamClassesMap.put("BOMBAY","3");
        dryBeamClassesMap.put("CALI","4");
        dryBeamClassesMap.put("DERMASON","5");
        dryBeamClassesMap.put("HOROZ","6");
        dryBeamClassesMap.put("SIRA","7");

        irisClassesMap = new HashMap<>();
        irisClassesMap.put("Iris-setosa","1");
        irisClassesMap.put("Iris-versicolor","2");
        irisClassesMap.put("Iris-virginica","3");
    }

    private static void cleanDryBeamDataSet(String pathToRawDataFolder, String pathToCleanDataFolder) {
        try {
            String pathsToRawDryBeamDataset = pathToRawDataFolder+"\\DryBeanDataset\\Dry_Bean_Dataset.csv";
            File dryBeamFile = new File(pathsToRawDryBeamDataset);
            Scanner dryBeamReader = new Scanner(dryBeamFile);
            String output ="";
            double numberOfLines =0;
            while (dryBeamReader.hasNextLine()){
                String line = dryBeamReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        List<String> splitLine = Arrays.asList(line.split(","));
                        splitLine.set(splitLine.size()-1, dryBeamClassesMap.get(splitLine.get(splitLine.size()-1)));
                        output += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                        printProgress(numberOfLines, 13611, "dry beam");
                    }
                }
                numberOfLines++;
            }
            dryBeamReader.close();

            String pathsToCleanDryBeamDataset = pathToCleanDataFolder+"\\DryBeanDataset\\dryBeam.txt";
            File cleanDryBeamFile = new File(pathsToCleanDryBeamDataset);
            cleanDryBeamFile.createNewFile();
            FileWriter dryBeamWriter = new FileWriter(cleanDryBeamFile);
            dryBeamWriter.write(output);
            dryBeamWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open dry beam data set.");
        } catch (IOException e) {
            System.out.println("Can not create dry beam data set file.");
        }
    }

    private static void cleanIrisDataSet(String pathToRawDataFolder, String pathToCleanDataFolder) {
        try {
            String pathsToRawIrisDataset = pathToRawDataFolder+"\\Iris\\iris.data";
            File irisFile = new File(pathsToRawIrisDataset);
            Scanner irisReader = new Scanner(irisFile);
            String output ="";
            long numberOfLines =0;
            while (irisReader.hasNextLine()){
                String line = irisReader.nextLine().trim();
                if (!line.isEmpty()){
                    numberOfLines++;
                    List<String> splitLine = Arrays.asList(line.split(","));
                    splitLine.set(splitLine.size()-1, irisClassesMap.get(splitLine.get(splitLine.size()-1)));
                    output += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                    printProgress(numberOfLines, 150, "iris");
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

    private static void printProgress(double current, double total, String dataSetName) {

        StringBuilder string = new StringBuilder(140);
        double percent = (current/total)* 100;
        string.append('\r')
              .append(formatter.format(percent))
              .append("% of ")
              .append(dataSetName)
              .append(" dataset cleaned.");

        System.out.print(string);
    }
}
