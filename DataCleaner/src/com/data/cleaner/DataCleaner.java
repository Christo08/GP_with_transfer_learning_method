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
    static List<Map<Character, String>> mushroomAttributesMap;

    static NumberFormat formatter;

    static String pathToRawDataFolder;
    static String pathToCleanDataFolder;

    public static void main(String[] args) {
        pathToRawDataFolder = args[0];
        pathToCleanDataFolder = args[1];
        formatter = new DecimalFormat("#0.00");

        initialiseMaps();

        cleanDryBeamDataSet();
        System.out.println();

        cleanIrisDataSet();
        System.out.println();

        cleanMushroomDataSet();
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

        mushroomAttributesMap = new LinkedList<>();

        Map<Character,String> mushroomCapShapeMap = new HashMap<>();
        mushroomCapShapeMap.put('b',"1");
        mushroomCapShapeMap.put('c',"2");
        mushroomCapShapeMap.put('x',"3");
        mushroomCapShapeMap.put('f',"4");
        mushroomCapShapeMap.put('k',"5");
        mushroomCapShapeMap.put('s',"6");
        mushroomAttributesMap.add(mushroomCapShapeMap);

        Map<Character,String> mushroomCapSurfaceMap = new HashMap<>();
        mushroomCapSurfaceMap.put('f',"1");
        mushroomCapSurfaceMap.put('g',"2");
        mushroomCapSurfaceMap.put('y',"3");
        mushroomCapSurfaceMap.put('s',"4");
        mushroomAttributesMap.add(mushroomCapSurfaceMap);

        Map<Character,String> mushroomCapColorMap = new HashMap<>();
        mushroomCapColorMap.put('n',"1");
        mushroomCapColorMap.put('b',"2");
        mushroomCapColorMap.put('c',"3");
        mushroomCapColorMap.put('g',"4");
        mushroomCapColorMap.put('r',"5");
        mushroomCapColorMap.put('p',"6");
        mushroomCapColorMap.put('u',"7");
        mushroomCapColorMap.put('e',"8");
        mushroomCapColorMap.put('w',"9");
        mushroomCapColorMap.put('y',"10");
        mushroomAttributesMap.add(mushroomCapColorMap);

        Map<Character,String> mushroomBruisesMap = new HashMap<>();
        mushroomBruisesMap.put('t',"1");
        mushroomBruisesMap.put('f',"2");
        mushroomAttributesMap.add(mushroomBruisesMap);

        Map<Character,String> mushroomOdorMap = new HashMap<>();
        mushroomOdorMap.put('a',"1");
        mushroomOdorMap.put('l',"2");
        mushroomOdorMap.put('c',"3");
        mushroomOdorMap.put('y',"4");
        mushroomOdorMap.put('f',"5");
        mushroomOdorMap.put('m',"6");
        mushroomOdorMap.put('n',"7");
        mushroomOdorMap.put('p',"8");
        mushroomOdorMap.put('s',"9");
        mushroomAttributesMap.add(mushroomOdorMap);

        Map<Character,String> mushroomGillAttachmentMap = new HashMap<>();
        mushroomGillAttachmentMap.put('a',"1");
        mushroomGillAttachmentMap.put('d',"2");
        mushroomGillAttachmentMap.put('f',"3");
        mushroomGillAttachmentMap.put('n',"4");
        mushroomAttributesMap.add(mushroomGillAttachmentMap);

        Map<Character,String> mushroomGillSpacingMap = new HashMap<>();
        mushroomGillSpacingMap.put('c',"1");
        mushroomGillSpacingMap.put('w',"2");
        mushroomGillSpacingMap.put('d',"3");
        mushroomAttributesMap.add(mushroomGillSpacingMap);

        Map<Character,String> mushroomGillSizeMap = new HashMap<>();
        mushroomGillSizeMap.put('b',"1");
        mushroomGillSizeMap.put('n',"2");
        mushroomAttributesMap.add(mushroomGillSizeMap);

        Map<Character,String> mushroomGillColorMap = new HashMap<>();
        mushroomGillColorMap.put('k',"1");
        mushroomGillColorMap.put('n',"2");
        mushroomGillColorMap.put('b',"3");
        mushroomGillColorMap.put('h',"4");
        mushroomGillColorMap.put('g',"5");
        mushroomGillColorMap.put('r',"6");
        mushroomGillColorMap.put('o',"7");
        mushroomGillColorMap.put('p',"8");
        mushroomGillColorMap.put('u',"9");
        mushroomGillColorMap.put('e',"10");
        mushroomGillColorMap.put('w',"11");
        mushroomGillColorMap.put('y',"12");
        mushroomAttributesMap.add(mushroomGillColorMap);

        Map<Character,String> mushroomStalkShapeMap = new HashMap<>();
        mushroomStalkShapeMap.put('e',"1");
        mushroomStalkShapeMap.put('t',"2");
        mushroomAttributesMap.add(mushroomStalkShapeMap);

        Map<Character,String> mushroomStalkRootMap = new HashMap<>();
        mushroomStalkRootMap.put('b',"1");
        mushroomStalkRootMap.put('c',"2");
        mushroomStalkRootMap.put('u',"3");
        mushroomStalkRootMap.put('e',"4");
        mushroomStalkRootMap.put('z',"5");
        mushroomStalkRootMap.put('r',"6");
        mushroomStalkRootMap.put('?',"-1");
        mushroomAttributesMap.add(mushroomStalkRootMap);

        Map<Character,String> mushroomStalkSurfaceAboveRingMap = new HashMap<>();
        mushroomStalkSurfaceAboveRingMap.put('f',"1");
        mushroomStalkSurfaceAboveRingMap.put('y',"2");
        mushroomStalkSurfaceAboveRingMap.put('k',"3");
        mushroomStalkSurfaceAboveRingMap.put('s',"4");
        mushroomAttributesMap.add(mushroomStalkSurfaceAboveRingMap);

        Map<Character,String> mushroomStalkSurfaceBelowRingMap = new HashMap<>();
        mushroomStalkSurfaceBelowRingMap.put('f',"1");
        mushroomStalkSurfaceBelowRingMap.put('y',"2");
        mushroomStalkSurfaceBelowRingMap.put('k',"3");
        mushroomStalkSurfaceBelowRingMap.put('s',"4");
        mushroomAttributesMap.add(mushroomStalkSurfaceBelowRingMap);

        Map<Character,String> mushroomStalkColorAboveRingMap = new HashMap<>();
        mushroomStalkColorAboveRingMap.put('n',"1");
        mushroomStalkColorAboveRingMap.put('b',"2");
        mushroomStalkColorAboveRingMap.put('c',"3");
        mushroomStalkColorAboveRingMap.put('g',"4");
        mushroomStalkColorAboveRingMap.put('o',"5");
        mushroomStalkColorAboveRingMap.put('p',"6");
        mushroomStalkColorAboveRingMap.put('e',"7");
        mushroomStalkColorAboveRingMap.put('w',"8");
        mushroomStalkColorAboveRingMap.put('y',"9");
        mushroomAttributesMap.add(mushroomStalkColorAboveRingMap);

        Map<Character,String> mushroomStalkColorBelowRingMap = new HashMap<>();
        mushroomStalkColorBelowRingMap.put('n',"1");
        mushroomStalkColorBelowRingMap.put('b',"2");
        mushroomStalkColorBelowRingMap.put('c',"3");
        mushroomStalkColorBelowRingMap.put('g',"4");
        mushroomStalkColorBelowRingMap.put('o',"5");
        mushroomStalkColorBelowRingMap.put('p',"6");
        mushroomStalkColorBelowRingMap.put('e',"7");
        mushroomStalkColorBelowRingMap.put('w',"8");
        mushroomStalkColorBelowRingMap.put('y',"9");
        mushroomAttributesMap.add(mushroomStalkColorBelowRingMap);

        Map<Character,String> mushroomVeilTypeMap = new HashMap<>();
        mushroomVeilTypeMap.put('p',"1");
        mushroomVeilTypeMap.put('u',"2");
        mushroomAttributesMap.add(mushroomVeilTypeMap);

        Map<Character,String> mushroomVeilColorMap = new HashMap<>();
        mushroomVeilColorMap.put('n',"1");
        mushroomVeilColorMap.put('o',"2");
        mushroomVeilColorMap.put('w',"3");
        mushroomVeilColorMap.put('y',"4");
        mushroomAttributesMap.add(mushroomVeilColorMap);

        Map<Character,String> mushroomRingNumberMap = new HashMap<>();
        mushroomRingNumberMap.put('n',"1");
        mushroomRingNumberMap.put('o',"2");
        mushroomRingNumberMap.put('t',"3");
        mushroomAttributesMap.add(mushroomRingNumberMap);

        Map<Character,String> mushroomRingTypeMap = new HashMap<>();
        mushroomRingTypeMap.put('c',"1");
        mushroomRingTypeMap.put('e',"2");
        mushroomRingTypeMap.put('f',"3");
        mushroomRingTypeMap.put('l',"4");
        mushroomRingTypeMap.put('n',"5");
        mushroomRingTypeMap.put('p',"6");
        mushroomRingTypeMap.put('s',"7");
        mushroomRingTypeMap.put('z',"8");
        mushroomAttributesMap.add(mushroomRingTypeMap);

        Map<Character,String> mushroomSporePrintColorMap = new HashMap<>();
        mushroomSporePrintColorMap.put('k',"1");
        mushroomSporePrintColorMap.put('n',"2");
        mushroomSporePrintColorMap.put('b',"3");
        mushroomSporePrintColorMap.put('h',"4");
        mushroomSporePrintColorMap.put('r',"5");
        mushroomSporePrintColorMap.put('o',"6");
        mushroomSporePrintColorMap.put('u',"7");
        mushroomSporePrintColorMap.put('w',"8");
        mushroomSporePrintColorMap.put('y',"9");
        mushroomAttributesMap.add(mushroomSporePrintColorMap);

        Map<Character,String> mushroomPopulationMap = new HashMap<>();
        mushroomPopulationMap.put('a',"1");
        mushroomPopulationMap.put('c',"2");
        mushroomPopulationMap.put('n',"3");
        mushroomPopulationMap.put('s',"4");
        mushroomPopulationMap.put('v',"5");
        mushroomPopulationMap.put('y',"6");
        mushroomAttributesMap.add(mushroomPopulationMap);

        Map<Character,String> mushroomHabitatMap = new HashMap<>();
        mushroomHabitatMap.put('g',"1");
        mushroomHabitatMap.put('l',"2");
        mushroomHabitatMap.put('m',"3");
        mushroomHabitatMap.put('p',"4");
        mushroomHabitatMap.put('u',"5");
        mushroomHabitatMap.put('w',"6");
        mushroomHabitatMap.put('d',"7");
        mushroomAttributesMap.add(mushroomHabitatMap);

        Map<Character,String> mushroomClassesMap = new HashMap<>();
        mushroomClassesMap.put('e',"1");
        mushroomClassesMap.put('p',"2");
        mushroomAttributesMap.add(mushroomClassesMap);

    }

    private static void cleanDryBeamDataSet() {
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

    private static void cleanIrisDataSet() {
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

    private static void cleanMushroomDataSet() {
        try {
            String pathsToRawMushroomDataset = pathToRawDataFolder+"\\Mushroom\\mushroom.data";
            File mushroomFile = new File(pathsToRawMushroomDataset);
            Scanner mushroomReader = new Scanner(mushroomFile);
            String output ="";
            double numberOfLines =0;
            while (mushroomReader.hasNextLine()){
                String line = mushroomReader.nextLine().trim();

                if (!line.isEmpty()){
                    List<String> splitLine = Arrays.asList(line.split(","));
                    List<Character> characters =new ArrayList<>();
                    for (int counter =1; counter < splitLine.size(); counter++)
                    {
                        characters.add(splitLine.get(counter).charAt(0));
                    }
                    characters.add(splitLine.get(0).charAt(0));

                    String newLine="";
                    for (int counter =0; counter < characters.size(); counter++)
                    {
                        newLine += mushroomAttributesMap.get(counter).get(characters.get(counter));
                        if (counter < characters.size()-1)
                        {
                            newLine+=",";
                        }
                    }
                    output += newLine+"\n";
                }
                numberOfLines++;
                printProgress(numberOfLines, 8124, "mushroom");
            }
            mushroomReader.close();

            String pathsToCleanMushroomDataset = pathToCleanDataFolder+"\\Mushroom\\mushroom.txt";
            File cleanMushroomFile = new File(pathsToCleanMushroomDataset);
            cleanMushroomFile.createNewFile();
            FileWriter mushroomWriter = new FileWriter(cleanMushroomFile);
            mushroomWriter.write(output);
            mushroomWriter.close();
        } catch (FileNotFoundException e) {
            System.out.println("Can not open dry beam data set.");
        } catch (IOException e) {
            System.out.println("Can not create dry beam data set file.");
        }
    }

    private static void cleanSeedsDataSet() {
        try {
            String pathsToRawSeedsDataset = pathToRawDataFolder+"\\Seeds\\seeds_dataset.txt";
            File seedsFile = new File(pathsToRawSeedsDataset);
            Scanner seedsReader = new Scanner(seedsFile);
            String output ="";
            long numberOfLines =0;
            while (seedsReader.hasNextLine()){
                String line = seedsReader.nextLine().trim();
                if (!line.isEmpty()){
                    numberOfLines++;
                    output +=line.replaceAll("\t",",")+"\n";
                    printProgress(numberOfLines, 210, "seeds");
                }
            }
            seedsReader.close();

            String pathsToCleanSeedsDataset = pathToCleanDataFolder+"\\Seeds\\seeds.txt";
            File cleanSeedsFile = new File(pathsToCleanSeedsDataset);
            cleanSeedsFile.createNewFile();
            FileWriter seedsWriter = new FileWriter(cleanSeedsFile);
            seedsWriter.write(output);
            seedsWriter.close();
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
            double numberOfLines =0;
            while (wineQualityRedReader.hasNextLine()){
                String line = wineQualityRedReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        output += line+"\n";
                        printProgress(numberOfLines, 1599, "wine quality red");
                    }
                }
                numberOfLines++;
            }
            wineQualityRedReader.close();

            String pathsToCleanWineQualityRedDataset = pathToCleanDataFolder+"\\Wine\\wineQualityRed.txt";
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
            double numberOfLines =0;
            while (wineQualityWhiteReader.hasNextLine()){
                String line = wineQualityWhiteReader.nextLine().trim();
                if (numberOfLines != 0)
                {
                    if (!line.isEmpty()){
                        output += line+"\n";
                        printProgress(numberOfLines, 4898, "wine quality white");
                    }
                }
                numberOfLines++;
            }
            wineQualityWhiteReader.close();

            String pathsToCleanWineQualityWhiteDataset = pathToCleanDataFolder+"\\Wine\\wineQualityWhite.txt";
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
}
