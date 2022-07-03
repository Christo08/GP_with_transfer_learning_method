package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;

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
        this.pathToFolder = gpController.getPathToExperiment()+"\\Data";
        this.bestGenChromosomes = new LinkedList<>();
    }

    public void addChromosomesToBestGenArray(List<ChromosomeWrapper> chromosomes){
        bestGenChromosomes.add(chromosomes);
    }

    public void exportBestGen(){
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
            String pathToFullTreeDataFile = pathToFolder+"\\bestGenData.txt";
            File fullTreeDataFile = new File(pathToFullTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter bestGenWriter = new FileWriter(fullTreeDataFile);
            bestGenWriter.write(outputString);
            bestGenWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importBestGen() throws FileNotFoundException {
        String pathsToRawFullTreeDataset = pathToFolder+"\\bestGenData.txt";
        File fullTreeFile = new File(pathsToRawFullTreeDataset);
        Scanner fullTreeReader = new Scanner(fullTreeFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (fullTreeReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(fullTreeReader.nextLine().trim())));
        }
        fullTreeReader.close();
        return output;
    }

    public void exportFullTree(){
        List<ChromosomeWrapper> populationSubset = gpController.getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInFullTreeMethod());
        String outputString ="";
        for (ChromosomeWrapper chromosome : populationSubset) {
            outputString += chromosome.chromosome +"\n";
        }
        try {
            String pathToFullTreeDataFile = pathToFolder+"\\fullTreeData.txt";
            File fullTreeDataFile = new File(pathToFullTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter fullTreeWriter = new FileWriter(fullTreeDataFile);
            fullTreeWriter.write(outputString);
            fullTreeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importFullTree() throws FileNotFoundException {
        String pathsToRawFullTreeDataset = pathToFolder+"\\fullTreeData.txt";
        File fullTreeFile = new File(pathsToRawFullTreeDataset);
        Scanner fullTreeReader = new Scanner(fullTreeFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (fullTreeReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(fullTreeReader.nextLine().trim())));
        }
        fullTreeReader.close();
        return output;
    }

    public void exportSubTree(){
        List<ChromosomeWrapper> populationSubset = gpController.getTopPercentageOfPopulation(ConfigController.getPercentOfChromosomeToSaveInSubTreeMethod());
        List<Chromosome> subTreePopulationSubset = new LinkedList<>();
        for (ChromosomeWrapper chromosome : populationSubset) {
            Chromosome subTree;
            do{
                subTree =chromosome.chromosome.getSubTree(chromosome.chromosome.getRandomSubTreeID());
            }while (containsChromosome(subTree,subTreePopulationSubset));
            subTreePopulationSubset.add(subTree);
        }
        String outputString ="";
        for (Chromosome chromosome : subTreePopulationSubset) {
            outputString += chromosome +"\n";
        }
        try {
            String pathToFullTreeDataFile = pathToFolder+"\\subTreeData.txt";
            File fullTreeDataFile = new File(pathToFullTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter subTreeWriter = new FileWriter(fullTreeDataFile);
            subTreeWriter.write(outputString);
            subTreeWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<ChromosomeWrapper> importSubTree() throws FileNotFoundException {
        String pathsToRawBestGenDataset = pathToFolder+"\\bestGenData.txt";
        File bestGenFile = new File(pathsToRawBestGenDataset);
        Scanner bestGenReader = new Scanner(bestGenFile);

        List<ChromosomeWrapper>  output = new ArrayList<>();
        while (bestGenReader.hasNextLine()){
            output.add(new ChromosomeWrapper(new Chromosome(bestGenReader.nextLine().trim())));
        }
        bestGenReader.close();
        return output;
    }

    public void exportGPCR(List<ChromosomeWrapper> population1, List<ChromosomeWrapper> population2){
        String commonSubTrees ="";
        List<Chromosome> commonSubTree = new ArrayList<>();
        int counter =0;
        for (ChromosomeWrapper chromosomeOfPopulation1 : population1) {
            for (ChromosomeWrapper chromosomeOfPopulation2 : population2) {
                List<Chromosome> functionSet = PopulationController.getCommonSubTree(chromosomeOfPopulation1.chromosome,chromosomeOfPopulation2.chromosome);
                commonSubTree.addAll(functionSet);
                counter++;
                printProgress(counter,(population1.size() * population2.size()));
            }
        }
        for (Chromosome chromosome : commonSubTree) {
            commonSubTrees += chromosome.toString()+"\n";
        }
        try {
            String pathToGPCRDataFile = pathToFolder+"\\GPCRData.txt";
            File GPCRDataFile = new File(pathToGPCRDataFile);
            GPCRDataFile.createNewFile();
            FileWriter GPCRWriter = new FileWriter(GPCRDataFile);
            GPCRWriter.write(commonSubTrees);
            GPCRWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importGPCR(){
        return new ArrayList<>();

    }

    public void exportPST(Chromosome bestChromosome){
        String chromosomeSubTrees ="";
        List<Chromosome> subTrees = PopulationController.getSubTrees(bestChromosome);
        subTrees.removeIf(subTree -> subTrees.stream()
                                               .anyMatch(subTree1 -> subTree1.toString().contains(subTree.toString())));
        subTrees.removeIf(chromosome -> chromosome.getMaxDepth() > ConfigController.getDepthOfPSTTree());
        for (Chromosome subTree : subTrees) {
            chromosomeSubTrees += subTree.toString()+"\n";
        }
        try {
            String pathToPSTDataFile = pathToFolder+"\\PSTData.txt";
            File pstDataFile = new File(pathToPSTDataFile);
            pstDataFile.createNewFile();
            FileWriter PSTWriter = new FileWriter(pstDataFile);
            PSTWriter.write(chromosomeSubTrees);
            PSTWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importPST(){
        return new ArrayList<>();
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

}
