package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TransferLearningController {
    private GPController gpController;
    private String pathToFolder;
    private List<List<ChromosomeWrapper>> bestGenChromosomes;

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
            FileWriter seedsTestingWriter = new FileWriter(fullTreeDataFile);
            seedsTestingWriter.write(outputString);
            seedsTestingWriter.close();
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
            FileWriter seedsTestingWriter = new FileWriter(fullTreeDataFile);
            seedsTestingWriter.write(outputString);
            seedsTestingWriter.close();
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
            FileWriter seedsTestingWriter = new FileWriter(fullTreeDataFile);
            seedsTestingWriter.write(outputString);
            seedsTestingWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<ChromosomeWrapper> importSubTree() throws FileNotFoundException {
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

    public void exportGPCR(List<Chromosome> population1, List<Chromosome> population2){
        String commonSubTrees ="";
        for (Chromosome chromosomeOfPopulation1 : population1) {
            for (Chromosome chromosomeOfPopulation2 : population2) {
                String commonSubTree = getCommonSubTree(chromosomeOfPopulation1.toString(),chromosomeOfPopulation2.toString());
                if (!commonSubTree.isEmpty())
                    commonSubTrees += commonSubTree+'\n';
            }
        }
        try {
            String pathToFullTreeDataFile = pathToFolder+"\\GPCRData.txt";
            File fullTreeDataFile = new File(pathToFullTreeDataFile);
            fullTreeDataFile.createNewFile();
            FileWriter seedsTestingWriter = new FileWriter(fullTreeDataFile);
            seedsTestingWriter.write(commonSubTrees);
            seedsTestingWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ChromosomeWrapper> importGPCR(){
        return new ArrayList<>();

    }

    public void exportPST(List<Chromosome> population1, List<Chromosome> population2){

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

    private String getCommonSubTree(String chromosomeOfPopulation1, String chromosomeOfPopulation2){
        int m = chromosomeOfPopulation1.length();
        int n = chromosomeOfPopulation2.length();

        int max = 0;
        int maxIIndex =0;
        int maxJIndex =0;

        int[][] dp = new int[m][n];

        for(int i=0; i<m; i++){
            for(int j=0; j<n; j++){
                if(chromosomeOfPopulation1.charAt(i) == chromosomeOfPopulation2.charAt(j)){
                    if(i==0 || j==0){
                        dp[i][j]=1;
                    }else{
                        dp[i][j] = dp[i-1][j-1]+1;
                    }

                    if(max < dp[i][j]) {
                        max = dp[i][j];
                        maxIIndex = i;
                        maxJIndex = j;
                    }
                }

            }
        }

        List<Character> outputChars = new ArrayList<>();
        int number = dp[maxIIndex][maxJIndex];
        while(number != 0){
            outputChars.add(chromosomeOfPopulation1.charAt(maxIIndex));
            maxIIndex--;
            maxJIndex--;
            number = dp[maxIIndex][maxJIndex];
        }
        String output ="";
        for (int counter = outputChars.size()-1; counter>=0; counter--) {
            output += outputChars.get(counter);
        }
        return output;
    }
}
