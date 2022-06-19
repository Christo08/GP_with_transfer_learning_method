package com.transfer.learning.gp.controllers.gp;

import com.transfer.learning.gp.data.objects.Chromosome;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TransferLearningController {
    private GPController gpController;
    private String pathToFolder;

    public TransferLearningController(GPController gpController) {
        this.gpController = gpController;
        this.pathToFolder = gpController.getPathToExperiment()+"\\Data";
    }

    public void exportBestGen(){

    }

    public void importBestGen(){

    }

    public void exportFullTree(){
        List<Chromosome> populationSubset = gpController.getTopPercentageOfPopulation();
        String outputString ="";
        for (Chromosome chromosome : populationSubset) {
            outputString += chromosome +"\n";
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

    public List<Chromosome> importFullTree(){
        return new ArrayList<>();
    }

    public void exportSubTree(){
        List<Chromosome> populationSubset = gpController.getTopPercentageOfPopulation();
        List<Chromosome> subTreePopulationSubset = new LinkedList<>();
        for (Chromosome chromosome : populationSubset) {
            subTreePopulationSubset.add(chromosome.getSubTree(chromosome.getRandomSubTreeID()));
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

    public List<Chromosome> importSubTree(){
        return new ArrayList<>();
    }

    public void exportGPCR(){

    }

    public void importGPCR(){

    }

    public void exportPST(){

    }

    public void importPST(){

    }
}
