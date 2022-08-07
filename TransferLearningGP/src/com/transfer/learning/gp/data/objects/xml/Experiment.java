package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "Experiment")
@XmlAccessorType(XmlAccessType.FIELD)

public class Experiment {

    private String dataSetName;
    private int maxDepthOfStartingChromosomes;

    private double padding;
    private double minAccuracy;
    private int numberOfRuns;

    private int populationSize;
    private int tournamentSize;
    private TransferLearningMethod transferLearningMethod;

    private List<Run> runs;
    private List<GeneticOperatorsConfig> geneticOperatorsConfigs;

    public Experiment() {
    }

    public Experiment(String dataSetName) {
        this.dataSetName = dataSetName;

        maxDepthOfStartingChromosomes = ConfigController.getMaxDepth();
        minAccuracy = ConfigController.getMinAccuracy();
        populationSize = ConfigController.getPopulationSize();
        tournamentSize = ConfigController.getTournamentSize();
        numberOfRuns = ConfigController.getNumberOfRuns();
        padding = ConfigController.getPadding();

        runs = new ArrayList<>();
        geneticOperatorsConfigs = new ArrayList<>();

        geneticOperatorsConfigs.add(new GeneticOperatorsConfig(1));
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

    public int getTournamentSize() {
        return tournamentSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
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

    public double getPadding() {
        return padding;
    }

    public void setPadding(double padding) {
        this.padding = padding;
    }

    public TransferLearningMethod getTransferLearningMethod() {
        return transferLearningMethod;
    }

    public void setTransferLearningMethod(TransferLearningMethod transferLearningMethod) {
        this.transferLearningMethod = transferLearningMethod;
    }

    public List<GeneticOperatorsConfig> getGeneticOperatorsConfigs() {
        return geneticOperatorsConfigs;
    }

    public void setGeneticOperatorsConfigs(List<GeneticOperatorsConfig> geneticOperatorsConfigs) {
        this.geneticOperatorsConfigs = geneticOperatorsConfigs;
    }

    public void addGeneticOperatorsConfig(GeneticOperatorsConfig geneticOperatorsConfig) {
        this.geneticOperatorsConfigs.add(geneticOperatorsConfig);
    }
}
