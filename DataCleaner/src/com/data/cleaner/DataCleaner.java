package com.data.cleaner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class DataCleaner {

    private static Map<String, String> dryBeamClassesMap;
    private static Map<String, String> irisClassesMap;
    private static Map<String, String> avilaClassesMap;

    private static NumberFormat formatter;

    private static String pathToRawDataFolder;
    private static String pathToCleanDataFolder;

    private static Random random;

    private static double percentOfTrainingData =0.25;

    public static void main(String[] args) {
        pathToRawDataFolder = args[0];
        pathToCleanDataFolder = args[1];
        formatter = new DecimalFormat("#0.00");

        random = new Random();
        random.setSeed(10);

        initialiseMaps();

        cleanAvilaSet();
        System.out.println();

        cleanDryBeamDataSet();
        System.out.println();

        cleanIrisDataSet();
        System.out.println();

        cleanSeedsDataSet();
        System.out.println();

        cleanRedWineDataSet();
        System.out.println();

        cleanWhiteWineDataSet();
        System.out.println();
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

        avilaClassesMap = new HashMap<>();
        avilaClassesMap.put("A","1");
        avilaClassesMap.put("B","2");
        avilaClassesMap.put("C","3");
        avilaClassesMap.put("D","4");
        avilaClassesMap.put("E","5");
        avilaClassesMap.put("F","6");
        avilaClassesMap.put("G","7");
        avilaClassesMap.put("H","8");
        avilaClassesMap.put("I","9");
        avilaClassesMap.put("W","10");
        avilaClassesMap.put("X","11");
        avilaClassesMap.put("Y","12");

    }

    private static void cleanAvilaSet() {
        try {
            String pathsToRawAvilaDataset = pathToRawDataFolder+"\\Avila\\avila.txt";
            File avilaFile = new File(pathsToRawAvilaDataset);
            Scanner avilaReader = new Scanner(avilaFile);

            String outputTesting ="";
            String outputTraining ="";

            int numberOfTotalLines = 20867;
            List<Integer> lineNumberOfTestingData = getLineNumberOfTestingData((int) Math.round(numberOfTotalLines * percentOfTrainingData), numberOfTotalLines);

            int numberOfLines =0;

            while (avilaReader.hasNextLine()){
                String line = avilaReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        List<String> splitLine = Arrays.asList(line.split(","));
                        splitLine.set(splitLine.size()-1, avilaClassesMap.get(splitLine.get(splitLine.size()-1)));
                        if (lineNumberOfTestingData.contains(numberOfLines-1)){
                            outputTesting += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                        }else {
                            outputTraining += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                        }
                        printProgress(numberOfLines, numberOfTotalLines, "avila");
                    }
                }
                numberOfLines++;
            }
            avilaReader.close();

            String pathsToCleanAvilaTestingDataset = pathToCleanDataFolder+"\\Avila\\avila_ts.txt";
            File cleanAvilaTestingFile = new File(pathsToCleanAvilaTestingDataset);
            cleanAvilaTestingFile.createNewFile();
            FileWriter dryBeamTestingWriter = new FileWriter(cleanAvilaTestingFile);
            dryBeamTestingWriter.write(outputTesting);
            dryBeamTestingWriter.close();

            String pathsToCleanAvilaTrainingDataset = pathToCleanDataFolder+"\\Avila\\avila_tr.txt";
            File cleanAvilaTrainingFile = new File(pathsToCleanAvilaTrainingDataset);
            cleanAvilaTrainingFile.createNewFile();
            FileWriter dryBeamTrainingWriter = new FileWriter(cleanAvilaTrainingFile);
            dryBeamTrainingWriter.write(outputTraining);
            dryBeamTrainingWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open dry beam data set.");
        } catch (IOException e) {
            System.out.println("Can not create dry beam data set file.");
        }
    }

    private static void cleanDryBeamDataSet() {
        try {
            String pathsToRawDryBeamDataset = pathToRawDataFolder+"\\DryBeanDataset\\Dry_Bean_Dataset.csv";
            File dryBeamFile = new File(pathsToRawDryBeamDataset);
            Scanner dryBeamReader = new Scanner(dryBeamFile);

            String outputTesting ="";
            String outputTraining ="";

            int numberOfTotalLines = 13611;
            List<Integer> lineNumberOfTestingData = getLineNumberOfTestingData((int) Math.round(numberOfTotalLines * percentOfTrainingData), numberOfTotalLines);

            int numberOfLines =0;

            while (dryBeamReader.hasNextLine()){
                String line = dryBeamReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        List<String> splitLine = Arrays.asList(line.split(","));
                        splitLine.set(splitLine.size()-1, dryBeamClassesMap.get(splitLine.get(splitLine.size()-1)));
                        if (lineNumberOfTestingData.contains(numberOfLines-1)){
                            outputTesting += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                        }else {
                            outputTraining += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                        }
                        printProgress(numberOfLines, numberOfTotalLines, "dry beam");
                    }
                }
                numberOfLines++;
            }
            dryBeamReader.close();

            String pathsToCleanDryBeamTestingDataset = pathToCleanDataFolder+"\\DryBeanDataset\\dryBeam_ts.txt";
            File cleanDryBeamTestingFile = new File(pathsToCleanDryBeamTestingDataset);
            cleanDryBeamTestingFile.createNewFile();
            FileWriter dryBeamTestingWriter = new FileWriter(cleanDryBeamTestingFile);
            dryBeamTestingWriter.write(outputTesting);
            dryBeamTestingWriter.close();

            String pathsToCleanDryBeamTrainingDataset = pathToCleanDataFolder+"\\DryBeanDataset\\dryBeam_tr.txt";
            File cleanDryBeamTrainingFile = new File(pathsToCleanDryBeamTrainingDataset);
            cleanDryBeamTrainingFile.createNewFile();
            FileWriter dryBeamTrainingWriter = new FileWriter(cleanDryBeamTrainingFile);
            dryBeamTrainingWriter.write(outputTraining);
            dryBeamTrainingWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open dry beam data set.");
        } catch (IOException e) {
            System.out.println("Can not create dry beam data set file.");
        }
    }

    private static void cleanIrisDataSet() {
        try {
            String pathsToRawIrisDataset = pathToRawDataFolder+"\\Iris\\iris.data";
            File irisFile = new File(pathsToRawIrisDataset);
            Scanner irisReader = new Scanner(irisFile);

            String outputTesting ="";
            String outputTraining ="";

            int numberOfTotalLines = 150;
            List<Integer> lineNumberOfTestingData = getLineNumberOfTestingData((int) Math.round(numberOfTotalLines * percentOfTrainingData), numberOfTotalLines);

            int numberOfLines =0;

            while (irisReader.hasNextLine()){
                String line = irisReader.nextLine().trim();
                if (!line.isEmpty()){
                    numberOfLines++;
                    List<String> splitLine = Arrays.asList(line.split(","));
                    splitLine.set(splitLine.size()-1, irisClassesMap.get(splitLine.get(splitLine.size()-1)));
                    if (lineNumberOfTestingData.contains(numberOfLines-1)){
                        outputTesting += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                    }else {
                        outputTraining += splitLine.toString().replaceAll(" ","").replaceAll("\\[","").replaceAll("\\]","")+"\n";
                    }
                    printProgress(numberOfLines, numberOfTotalLines, "iris");
                }
            }
            irisReader.close();

            String pathsToCleanIrisTestingDataset = pathToCleanDataFolder+"\\Iris\\iris_ts.txt";
            File cleanIrisTestingFile = new File(pathsToCleanIrisTestingDataset);
            cleanIrisTestingFile.createNewFile();
            FileWriter irisTestingWriter = new FileWriter(cleanIrisTestingFile);
            irisTestingWriter.write(outputTesting);
            irisTestingWriter.close();

            String pathsToCleanIrisTrainingDataset = pathToCleanDataFolder+"\\Iris\\iris_tr.txt";
            File cleanIrisTrainingFile = new File(pathsToCleanIrisTrainingDataset);
            cleanIrisTrainingFile.createNewFile();
            FileWriter irisTrainingWriter = new FileWriter(cleanIrisTrainingFile);
            irisTrainingWriter.write(outputTraining);
            irisTrainingWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open iris data set.");
        } catch (IOException e) {
            System.out.println("Can not create iris data set file.");
        }
    }

    private static void cleanSeedsDataSet() {
        try {
            String pathsToRawSeedsDataset = pathToRawDataFolder+"\\Seeds\\seeds_dataset.txt";
            File seedsFile = new File(pathsToRawSeedsDataset);
            Scanner seedsReader = new Scanner(seedsFile);

            String outputTesting ="";
            String outputTraining ="";

            int numberOfTotalLines = 210;
            List<Integer> lineNumberOfTestingData = getLineNumberOfTestingData((int) Math.round(numberOfTotalLines * percentOfTrainingData), numberOfTotalLines);

            int numberOfLines =0;

            while (seedsReader.hasNextLine()){
                String line = seedsReader.nextLine().trim();
                if (!line.isEmpty()){
                    line=line.replaceAll("\t\t","\t");
                    numberOfLines++;
                    if (lineNumberOfTestingData.contains(numberOfLines-1)){
                        outputTesting += line.replaceAll("\t",",")+"\n";
                    }else {
                        outputTraining += line.replaceAll("\t",",")+"\n";
                    }
                    printProgress(numberOfLines, numberOfTotalLines, "seeds");
                }
            }
            seedsReader.close();

            String pathsToCleanSeedsTestingDataset = pathToCleanDataFolder+"\\Seeds\\seeds_ts.txt";
            File cleanSeedsTestingFile = new File(pathsToCleanSeedsTestingDataset);
            cleanSeedsTestingFile.createNewFile();
            FileWriter seedsTestingWriter = new FileWriter(cleanSeedsTestingFile);
            seedsTestingWriter.write(outputTesting);
            seedsTestingWriter.close();

            String pathsToCleanSeedsTrainingDataset = pathToCleanDataFolder+"\\Seeds\\seeds_tr.txt";
            File cleanSeedsTrainingFile = new File(pathsToCleanSeedsTrainingDataset);
            cleanSeedsTrainingFile.createNewFile();
            FileWriter seedsTrainingWriter = new FileWriter(cleanSeedsTrainingFile);
            seedsTrainingWriter.write(outputTraining);
            seedsTrainingWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("Can not open seeds data set.");
        } catch (IOException e) {
            System.out.println("Can not create seeds data set file.");
        }
    }

    private static void cleanRedWineDataSet() {
        try {
            String pathsToRawWineQualityRedDataset = pathToRawDataFolder+"\\Wine\\winequality-red.csv";
            File wineQualityRedFile = new File(pathsToRawWineQualityRedDataset);
            Scanner wineQualityRedReader = new Scanner(wineQualityRedFile);

            String output ="";

            int numberOfTotalLines = 1599;

            int numberOfLines =0;

            while (wineQualityRedReader.hasNextLine()){
                String line = wineQualityRedReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        output += line+"\n";
                        printProgress(numberOfLines, numberOfTotalLines, "wine quality red");
                    }
                }
                numberOfLines++;
            }
            wineQualityRedReader.close();

            String pathsToCleanWineQualityRedDataset = pathToCleanDataFolder+"\\WineRed\\wineQualityRed.txt";
            File cleanWineQualityRedFile = new File(pathsToCleanWineQualityRedDataset);
            cleanWineQualityRedFile.createNewFile();
            FileWriter wineQualityRedWriter = new FileWriter(cleanWineQualityRedFile);
            wineQualityRedWriter.write(output);
            wineQualityRedWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open wine quality red data set.");
        } catch (IOException e) {
            System.out.println("Can not create wine quality red data set file.");
        }
    }

    private static void cleanWhiteWineDataSet() {
        try {
            String pathsToRawWineQualityWhiteDataset = pathToRawDataFolder+"\\Wine\\winequality-white.csv";
            File wineQualityWhiteFile = new File(pathsToRawWineQualityWhiteDataset);
            Scanner wineQualityWhiteReader = new Scanner(wineQualityWhiteFile);

            String output ="";

            int numberOfTotalLines = 4898;

            int numberOfLines =0;

            while (wineQualityWhiteReader.hasNextLine()){
                String line = wineQualityWhiteReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        output += line+"\n";
                        printProgress(numberOfLines, numberOfTotalLines, "wine quality white");
                    }
                }
                numberOfLines++;
            }
            wineQualityWhiteReader.close();

            String pathsToCleanWineQualityWhiteDataset = pathToCleanDataFolder+"\\WineWhite\\wineQualityWhite.txt";
            File cleanWineQualityWhiteFile = new File(pathsToCleanWineQualityWhiteDataset);
            cleanWineQualityWhiteFile.createNewFile();
            FileWriter wineQualityWhiteWriter = new FileWriter(cleanWineQualityWhiteFile);
            wineQualityWhiteWriter.write(output);
            wineQualityWhiteWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("Can not open wine quality white data set.");
        } catch (IOException e) {
            System.out.println("Can not create wine quality white data set file.");
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

    private static List<Integer> getLineNumberOfTestingData(int numberOfTestingLines, int totalNumberOfLines) {
        List<Integer> lineNumbers = new LinkedList<>();
        int lineNumber;
        while (lineNumbers.size() < numberOfTestingLines){
            do {
                lineNumber = random.nextInt(totalNumberOfLines);
            }while (lineNumbers.contains(lineNumber));
            lineNumbers.add(lineNumber);
        }
        return lineNumbers;
    }
}
