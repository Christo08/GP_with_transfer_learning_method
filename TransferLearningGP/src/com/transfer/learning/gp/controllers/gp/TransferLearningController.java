package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;
import com.transfer.learning.gp.data.objects.Function;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TransferLearningController {
    private GPController gpController;
    private String pathToFolder;
    private List<List<ChromosomeWrapper>> bestGenChromosomes;
    private NumberFormat formatter = new DecimalFormat("#0.0000");

    public TransferLearningController(GPController gpController) {
        this.gpController = gpController;
        this.pathToFolder = gpController.getPathToData()+"\\Data";
        this.bestGenChromosomes = new LinkedList<>();
    }

    public void addChromosomesToBestGenArray(List<ChromosomeWrapper> chromosomes){
        bestGenChromosomes.add(chromosomes);
    }

    public void exportBestGen(long startTime, int runNumber){
        int indexOfBestGen = 0;
        double bestAverage = 0.0;
        int counter=0;
        for (List<ChromosomeWrapper> bestGenChromosome:bestGenChromosomes) {
            double average = getAverageOfChromosomes(bestGenChromosome);
            if (bestAverage < average){
                bestAverage = average;
                indexOfBestGen = counter;
            }
            counter++;
        }
        String outputString ="";
        for (ChromosomeWrapper chromosome : bestGenChromosomes.get(indexOfBestGen)) {
            outputString += chromosome.chromosome +"\n";
        }
        try {
            String fileName = startTime+"_"+runNumber+".txt";
            String pathToBestGenDataFile = pathToFolder+"\\bestGen\\"+fileName;
            File fullTreeDataFile = new File(pathToBestGenDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter bestGenWriter = new FileWriter(fullTreeDataFile);
            bestGenWriter.write(outputString);
            bestGenWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importBestGen(long startTime, int runNumber) throws FileNotFoundException {
        String fileName = startTime+"_"+runNumber+".txt";
        String pathToBestGenDataFile = pathToFolder+"\\bestGen\\"+fileName;
        File fullTreeFile = new File(pathToBestGenDataFile);
        Scanner fullTreeReader = new Scanner(fullTreeFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (fullTreeReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(fullTreeReader.nextLine().trim())));
        }
        fullTreeReader.close();
        return output;
    }

    public void exportFullTree(long startTime, int runNumber){
        List<ChromosomeWrapper> populationSubset = gpController.getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInFullTreeMethod());
        String outputString ="";
        for (ChromosomeWrapper chromosome : populationSubset) {
            outputString += chromosome.chromosome +"\n";
        }
        try {
            String fileName = startTime+"_"+runNumber+".txt";
            String pathToFullTreeDataFile = pathToFolder+"\\fullTree\\"+fileName;
            File fullTreeDataFile = new File(pathToFullTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter fullTreeWriter = new FileWriter(fullTreeDataFile);
            fullTreeWriter.write(outputString);
            fullTreeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importFullTree(long startTime, int runNumber) throws FileNotFoundException {
        String fileName = startTime+"_"+runNumber+".txt";
        String pathToFullTreeDataFile = pathToFolder+"\\fullTree\\"+fileName;
        File fullTreeFile = new File(pathToFullTreeDataFile);
        Scanner fullTreeReader = new Scanner(fullTreeFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (fullTreeReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(fullTreeReader.nextLine().trim())));
        }
        fullTreeReader.close();
        return output;
    }

    public void exportSubTree(long startTime, int runNumber){
        List<ChromosomeWrapper> populationSubset = gpController.getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInSubTreeMethod());
        List<Chromosome> subTreePopulationSubset = new LinkedList<>();
        for (ChromosomeWrapper chromosome : populationSubset) {
            Chromosome subTree;
            do{
                subTree =chromosome.chromosome.getSubTree(chromosome.chromosome.getRandomSubTreeID());
            }while (subTree.getType() != 'c');
            subTreePopulationSubset.add(subTree);
        }
        String outputString ="";
        for (Chromosome chromosome : subTreePopulationSubset) {
            outputString += chromosome +"\n";
        }
        try {
            String fileName = startTime+"_"+runNumber+".txt";
            String pathToSubTreeDataFile = pathToFolder+"\\subTree\\"+fileName;
            File fullTreeDataFile = new File(pathToSubTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter subTreeWriter = new FileWriter(fullTreeDataFile);
            subTreeWriter.write(outputString);
            subTreeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<ChromosomeWrapper> importSubTree(long startTime, int runNumber) throws FileNotFoundException {
        String fileName = startTime+"_"+runNumber+".txt";
        String pathToSubTreeDataFile = pathToFolder+"\\subTree\\"+fileName;
        File bestGenFile = new File(pathToSubTreeDataFile);
        Scanner bestGenReader = new Scanner(bestGenFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (bestGenReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(bestGenReader.nextLine().trim())));
        }
        bestGenReader.close();
        return output;
    }

    public void exportGPCR(long startTime, int runNumber){
        String trees ="";
        List<ChromosomeWrapper> populationSubset = gpController.getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInFullTreeMethod());

        for (ChromosomeWrapper chromosome : populationSubset) {
            trees += chromosome.chromosome.toString()+"\n";
        }
        try {
            String fileName = startTime+"_"+runNumber+".txt";
            String pathToGPCRDataFile = pathToFolder+"\\GPCR\\"+fileName;
            File GPCRDataFile = new File(pathToGPCRDataFile);
            GPCRDataFile.createNewFile();
            FileWriter GPCRWriter = new FileWriter(GPCRDataFile);
            GPCRWriter.write(trees);
            GPCRWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Chromosome> importGPCR(long startTime, int runNumber) throws FileNotFoundException {
        String fileName = startTime+"_"+runNumber+".txt";
        String pathToGPCRDataFile = pathToFolder+"\\GPCR\\"+fileName;
        File GPCRTreeFile = new File(pathToGPCRDataFile);
        Scanner GPCRTreeReader = new Scanner(GPCRTreeFile);

        List<Chromosome>  output = new ArrayList<>();
        while (GPCRTreeReader.hasNextLine()){
            output.add(new Chromosome(GPCRTreeReader.nextLine().trim()));
        }
        GPCRTreeReader.close();
        return output;
    }

    public void exportPST(List<ChromosomeWrapper> population1, List<ChromosomeWrapper> population2, long startTime, int runNumber){
        List<Chromosome> commonOutputSubTrees = new ArrayList<>();
        int counter =0;
        for (ChromosomeWrapper chromosomeOfPopulation1 : population1) {
            for (ChromosomeWrapper chromosomeOfPopulation2 : population2) {
                List<Chromosome> commonSubTrees = PopulationController.getCommonSubTree(chromosomeOfPopulation1.chromosome,chromosomeOfPopulation2.chromosome);
                for (Chromosome commonSubTree : commonSubTrees) {
                    if (commonSubTree.getMaxDepth() >= ConfigController.getMaxDepth() &&
                        !commonOutputSubTrees.stream().anyMatch(commonOutputSubTree -> commonOutputSubTree.hashCode() == commonSubTree.hashCode())){
                        commonOutputSubTrees.add(commonSubTree);
                    }
                }
                counter++;
                printProgress(counter,(population1.size() * population2.size()));
            }
        }

        String chromosomeSubTrees ="";
        for (Chromosome commonSubTree : commonOutputSubTrees) {
            chromosomeSubTrees += commonSubTree.toString()+"\n";
        }
        try {
            String fileName = startTime+"_"+runNumber+".txt";
            String pathToPSTDataFile = pathToFolder+"\\PST\\"+fileName;
            File pstDataFile = new File(pathToPSTDataFile);
            pstDataFile.createNewFile();
            FileWriter PSTWriter = new FileWriter(pstDataFile);
            PSTWriter.write(chromosomeSubTrees);
            PSTWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Function> importPST(long startTime, int runNumber) throws FileNotFoundException {
        String fileName = startTime+"_"+runNumber+".txt";
        String pathToPSTDataFile = pathToFolder+"\\PST\\"+fileName;
        File PSTFile = new File(pathToPSTDataFile);
        Scanner PSTReader = new Scanner(PSTFile);

        List<Function> output = new ArrayList<>();
        int lineNumber =0;
        while (PSTReader.hasNextLine()){
            output.add(new Function(PSTReader.nextLine().trim(), "f"+lineNumber));
            lineNumber++;
        }
        PSTReader.close();
        return output;
    }

    private boolean containsChromosome(Chromosome newChromosome, List<Chromosome> chromosomes) {
        for (Chromosome value : chromosomes) {
            if (value.hashCode() == newChromosome.hashCode())
                return true;
        }
        return false;
    }

    private double getAverageOfChromosomes(List<ChromosomeWrapper> bestGenChromosome) {
        double total = 0;
        for (ChromosomeWrapper chromosome : bestGenChromosome) {
            total += chromosome.fitness;
        }
        return (total/bestGenChromosome.size()) *100;
    }

    protected void printProgress(double current, double total) {
        StringBuilder string = new StringBuilder(140);
        double percent = (current/total)* 100;
        string.append('\r')
                .append(formatter.format(percent))
                .append("% export");

        System.out.print(string);
    }

    public String getPathToFolder(int mod) {
        if (mod == 1)
            return pathToFolder+"\\fullTree";
        else if (mod == 2)
            return pathToFolder+"\\subTree";
        else if (mod == 3)
            return pathToFolder+"\\bestGen";
        else if (mod == 4)
            return pathToFolder+"\\GPCR";
        else
            return pathToFolder+"\\PST";
    }
}
