package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.*;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "Experiment")
@XmlAccessorType(XmlAccessType.FIELD)

public class Experiment {
    private int crossoverSize;
    private String dataSetName;
    private int maxDepthOfStartingChromosomes;
    private double minAccuracy;
    private int mutationSize;
    private int numberOfGenerationsBeforeEvolveMap;
    private int numberOfSlots;
    private int numberOfRuns;
    private int populationSize;
    private long seed;
    private double startBondOfSlots;
    private double stopBondOfSlots;
    private int tournamentSize;
    private int transferLearningMethod;
    private int reproductionSize;
    private List<Run> runs;
    private double padding;
    private double percentOfChromosomeToSaveInFullTreMethod;

    public Experiment() {
    }

    public Experiment(String dataSetName, long seed) {
        this.dataSetName = dataSetName;
        this.seed = seed;

        crossoverSize = ConfigController.getPercentOfCrossover();
        maxDepthOfStartingChromosomes = ConfigController.getMaxDepth();
        minAccuracy = ConfigController.getMinAccuracy();
        mutationSize = ConfigController.getPercentOfMutation();
        numberOfGenerationsBeforeEvolveMap = ConfigController.getNumberOfGenerationsBeforeEvolveMap();
        numberOfSlots = ConfigController.getNumberOfSlots();
        populationSize = ConfigController.getPopulationSize();
        startBondOfSlots = ConfigController.getStartBondOfSlots();
        stopBondOfSlots = ConfigController.getStopBondOfSlots();
        tournamentSize = ConfigController.getTournamentSize();
        reproductionSize = ConfigController.getPercentOfReproduction();
        numberOfRuns = ConfigController.getNumberOfRuns();
        padding = ConfigController.getPadding();
        percentOfChromosomeToSaveInFullTreMethod = ConfigController.getPercentOfChromosomeToSaveInFullTreMethod();

        runs = new LinkedList<>();
    }


    public int getCrossoverSize() {
        return crossoverSize;
    }

    public void setCrossoverSize(int crossoverSize) {
        this.crossoverSize = crossoverSize;
    }


    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }


    public int getMaxDepthOfStartingChromosomes() {
        return maxDepthOfStartingChromosomes;
    }

    public void setMaxDepthOfStartingChromosomes(int maxDepthOfStartingChromosomes) {
        this.maxDepthOfStartingChromosomes = maxDepthOfStartingChromosomes;
    }


    public double getMinAccuracy() {
        return minAccuracy;
    }

    public void setMinAccuracy(double minAccuracy) {
        this.minAccuracy = minAccuracy;
    }


    public int getMutationSize() {
        return mutationSize;
    }

    public void setMutationSize(int mutationSize) {
        this.mutationSize = mutationSize;
    }


    public int getNumberOfGenerationsBeforeEvolveMap() {
        return numberOfGenerationsBeforeEvolveMap;
    }

    public void setNumberOfGenerationsBeforeEvolveMap(int numberOfGenerationsBeforeEvolveMap) {
        this.numberOfGenerationsBeforeEvolveMap = numberOfGenerationsBeforeEvolveMap;
    }


    public int getNumberOfSlots() {
        return numberOfSlots;
    }

    public void setNumberOfSlots(int numberOfSlots) {
        this.numberOfSlots = numberOfSlots;
    }


    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(int numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }


    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }


    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }


    public double getStartBondOfSlots() {
        return startBondOfSlots;
    }

    public void setStartBondOfSlots(double startBondOfSlots) {
        this.startBondOfSlots = startBondOfSlots;
    }


    public double getStopBondOfSlots() {
        return stopBondOfSlots;
    }

    public void setStopBondOfSlots(double stopBondOfSlots) {
        this.stopBondOfSlots = stopBondOfSlots;
    }


    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }


    public int getReproductionSize() {
        return reproductionSize;
    }

    public void setReproductionSize(int reproductionSize) {
        this.reproductionSize = reproductionSize;
    }

    public List<Run> getRuns() {
        return runs;
    }

    public void setRuns(List<Run> runs) {
        this.runs = runs;
    }

    public void addRun(Run run) {
        this.runs.add(run);
    }

    public void setTransferLearningMethod(int transferLearningMethod) {
        this.transferLearningMethod = transferLearningMethod;
    }

    public int getTransferLearningMethod() {
        return transferLearningMethod;
    }

    public double getPadding() {
        return padding;
    }

    public void setPadding(double padding) {
        this.padding = padding;
    }

    public double getPercentOfChromosomeToSaveInFullTreMethod() {
        return percentOfChromosomeToSaveInFullTreMethod;
    }

    public void setPercentOfChromosomeToSaveInFullTreMethod(double percentOfChromosomeToSaveInFullTreMethod) {
        this.percentOfChromosomeToSaveInFullTreMethod = percentOfChromosomeToSaveInFullTreMethod;
    }
}
