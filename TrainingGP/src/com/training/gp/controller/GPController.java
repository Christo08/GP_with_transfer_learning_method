package com.training.gp.controller;

import java.io.FileNotFoundException;
import java.util.*;

public class GPController {
    private static Random random = new Random();

    private DataController dataController;
    private PopulationController populationController;
    private SDRSController sdrsController;
    private long startTime;
    private double oldbestChromosomesAccuracy;

    public GPController(String pathToData) throws FileNotFoundException {
        long seed = random.nextLong();
        random.setSeed(seed);
        System.out.println("Seed: "+ seed);

        this.dataController = new DataController(pathToData);
        this.populationController = new PopulationController();
        this.sdrsController = new SDRSController(this, dataController, populationController);
    }

    public Chromosomes evolveAnswers(){
        startTime = System.currentTimeMillis();
        Chromosomes bestChromosomes = populationController.getChromosomes(findBestChromosomes());
        double bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(bestChromosomes);
        int counterChange =0;
        for (int counter = 0; bestChromosomesAccuracy < ConfigController.getMinAccuracy();counter++){
            List<Chromosomes> newChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
            newChromosomes.addAll(populationController.reproductionChromosomes(selectParents(ConfigController.getReproductionSize())));

            List<Integer> mutationIndexes = selectParents(ConfigController.getMutationSize());
            for (int index: mutationIndexes){
                newChromosomes.add(populationController.mutationChromosomes(index));
            }

            List<Integer> crossoverIndexes = selectParents(ConfigController.getCrossoverSize());
            for (int counter1 = 0; counter1 < ConfigController.getCrossoverSize() - 1; counter1+=2){
                newChromosomes.addAll(populationController.crossoverChromosomes(crossoverIndexes.get(counter1),crossoverIndexes.get(counter1+1)));
            }

            if (counter != 0 && counter % ConfigController.getNumberOfGenerationsToEvolveMap() ==0){
                sdrsController.evolveMap();
            }
            populationController.setChromosomes(newChromosomes);

            oldbestChromosomesAccuracy = bestChromosomesAccuracy;
            bestChromosomes = populationController.getChromosomes(findBestChromosomes());
            bestChromosomesAccuracy =populationController.getFitnessOfChromosomes(bestChromosomes);

            System.out.println("Generations "+counter+" best chromosome's accuracy "+(bestChromosomesAccuracy *100)+"% Number of times the same: "+counterChange);
            if (bestChromosomesAccuracy != oldbestChromosomesAccuracy)
                counterChange =0;
            else
                counterChange++;
           // System.out.println(bestChromosomes);
        }

        long timeTake = System.currentTimeMillis() -startTime;
        int seconds = (int) (timeTake / 1000) % 60 ;
        timeTake = timeTake - seconds * 1000;
        int minutes = (int) ((timeTake / (60000)) % 60);
        timeTake = timeTake - minutes * 60000;
        int hours   = (int) ((timeTake / (3600000)) % 24);
        timeTake = timeTake - hours * 3600000;
        int days = Math.round(timeTake / (86400000));

        System.out.println("Runtime: "+days+" days "+hours+" hours "+minutes+" minutes "+seconds+" seconds.");

        return bestChromosomes;
    }

    private List<Integer> selectParents(int numberOfParentsNeeded) {
        List<Integer> indexes = new LinkedList<>();
        for (int counter =0; counter< numberOfParentsNeeded; counter++) {
            indexes.add(selectParent());
        }
        return indexes;
    }

    private int findBestChromosomes() {
        double maxFitness =0;
        double fitness =0;
        int chromosomesIndex=0;
        for (int counter = 0; counter < ConfigController.getPopulationSize(); counter++){
            fitness = fitnessOfChromosomes(counter);

            populationController.addFitnessOfChromosomes(counter, fitness);

            if (fitness > maxFitness){
                maxFitness =fitness;
                chromosomesIndex = counter;
            }
        }
        return chromosomesIndex;
    }

    private int selectParent(){
        List<Integer> parentsIndex = new ArrayList<>(ConfigController.getTournamentSize());
        int randomIndex;
        for (int counter = 0; counter < ConfigController.getTournamentSize(); counter++){
            do {
                randomIndex = random.nextInt(ConfigController.getPopulationSize());
            }while (parentsIndex.contains(randomIndex));
            parentsIndex.add(randomIndex);
        }
        int bestIndex =0;
        double bestFitness =0;
        double fitness =0;
        for (int index: parentsIndex){
            fitness = populationController.getFitnessOfChromosomes(index);
            if (fitness > bestFitness){
                bestFitness = fitness;
                bestIndex = index;
            }
        }
        return bestIndex;
    }

    public static Random getRandom() {
        return random;
    }

    public double fitnessOfChromosomes(int counter) {
        double numberOfCorrect =0;
        for (Map<String, Double> dataLine: dataController.getDataSet()) {
            String chromosomesOutput = sdrsController.getClass(populationController.evaluateChromosomes(dataLine,counter));
            if (chromosomesOutput.equals(dataLine.get("ans").toString()))
                numberOfCorrect++;
        }
        return numberOfCorrect/((double)dataController.getDataSet().size());
    }
}
