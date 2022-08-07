package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Run")
@XmlAccessorType(XmlAccessType.FIELD)
public class Run {
    private int runNumber;
    private long seed;
    //Target dataset
    private double accuracyOnTargetTestingDataset;
    private double accuracyOnTargetTrainingDataset;
    private int numberOfGenerationsOnTrainingDataset;
    private long startTimeStampOfTargetDataset;
    private long stopTimeStampOfTargetDataset;
    private boolean runTargetSuccessful;
    //Source one dataset
    private double accuracyOnSourceTestingDatasetOne;
    private double accuracyOnSourceTrainingDatasetOne;
    private int numberOfGenerationsOnSourceDatasetOne;
    private long startTimeStampOfSourceDatasetOne;
    private long stopTimeStampOfSourceDatasetOne;
    private boolean runSourceOneSuccessful;
    //Source two dataset
    private double accuracyOnSourceTestingDatasetTwo;
    private double accuracyOnSourceTrainingDatasetTwo;
    private int numberOfGenerationsOnSourceDatasetTwo;
    private long startTimeStampOfSourceDatasetTwo;
    private long stopTimeStampOfSourceDatasetTwo;
    private boolean runSourceTwoSuccessful;

    public Run() {
    }

    public double getAccuracyOnTargetTestingDataset() {
        return accuracyOnTargetTestingDataset;
    }

    public void setAccuracyOnTargetTestingDataset(double accuracyOnTargetTestingDataset) {
        this.accuracyOnTargetTestingDataset = accuracyOnTargetTestingDataset;
    }

    public double getAccuracyOnTargetTrainingDataset() {
        return accuracyOnTargetTrainingDataset;
    }

    public void setAccuracyOnTargetTrainingDataset(double accuracyOnTargetTrainingDataset) {
        this.accuracyOnTargetTrainingDataset = accuracyOnTargetTrainingDataset;
    }

    public double getAccuracyOnSourceTestingDatasetOne() {
        return accuracyOnSourceTestingDatasetOne;
    }

    public void setAccuracyOnSourceTestingDatasetOne(double accuracyOnSourceTestingDatasetOne) {
        this.accuracyOnSourceTestingDatasetOne = accuracyOnSourceTestingDatasetOne;
    }

    public double getAccuracyOnSourceTrainingDatasetOne() {
        return accuracyOnSourceTrainingDatasetOne;
    }

    public void setAccuracyOnSourceTrainingDatasetOne(double accuracyOnSourceTrainingDatasetOne) {
        this.accuracyOnSourceTrainingDatasetOne = accuracyOnSourceTrainingDatasetOne;
    }

    public int getNumberOfGenerationsOnSourceDatasetOne() {
        return numberOfGenerationsOnSourceDatasetOne;
    }

    public void setNumberOfGenerationsOnSourceDatasetOne(int numberOfGenerationsOnSourceDatasetOne) {
        this.numberOfGenerationsOnSourceDatasetOne = numberOfGenerationsOnSourceDatasetOne;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getStartTimeStampOfTargetDataset() {
        return startTimeStampOfTargetDataset;
    }

    public void setStartTimeStampOfTargetDataset(long startTimeStampOfTargetDataset) {
        this.startTimeStampOfTargetDataset = startTimeStampOfTargetDataset;
    }

    public long getStopTimeStampOfTargetDataset() {
        return stopTimeStampOfTargetDataset;
    }

    public void setStopTimeStampOfTargetDataset(long stopTimeStampOfTargetDataset) {
        this.stopTimeStampOfTargetDataset = stopTimeStampOfTargetDataset;
    }

    public long getStartTimeStampOfSourceDatasetOne() {
        return startTimeStampOfSourceDatasetOne;
    }

    public void setStartTimeStampOfSourceDatasetOne(long startTimeStampOfSourceDatasetOne) {
        this.startTimeStampOfSourceDatasetOne = startTimeStampOfSourceDatasetOne;
    }

    public long getStopTimeStampOfSourceDatasetOne() {
        return stopTimeStampOfSourceDatasetOne;
    }

    public void setStopTimeStampOfSourceDatasetOne(long stopTimeStampOfSourceDatasetOne) {
        this.stopTimeStampOfSourceDatasetOne = stopTimeStampOfSourceDatasetOne;
    }

    public boolean isRunSourceOneSuccessful() {
        return runSourceOneSuccessful;
    }

    public void setRunSourceOneSuccessful(boolean runSourceOneSuccessful) {
        this.runSourceOneSuccessful = runSourceOneSuccessful;
    }

    public boolean isRunTargetSuccessful() {
        return runTargetSuccessful;
    }

    public void setRunTargetSuccessful(boolean runTargetSuccessful) {
        this.runTargetSuccessful = runTargetSuccessful;
    }

    public int getNumberOfGenerationsOnTrainingDataset() {
        return numberOfGenerationsOnTrainingDataset;
    }

    public void setNumberOfGenerationsOnTrainingDataset(int numberOfGenerationsOnTrainingDataset) {
        this.numberOfGenerationsOnTrainingDataset = numberOfGenerationsOnTrainingDataset;
    }

    public double getAccuracyOnSourceTestingDatasetTwo() {
        return accuracyOnSourceTestingDatasetTwo;
    }

    public void setAccuracyOnSourceTestingDatasetTwo(double accuracyOnSourceTestingDatasetTwo) {
        this.accuracyOnSourceTestingDatasetTwo = accuracyOnSourceTestingDatasetTwo;
    }

    public double getAccuracyOnSourceTrainingDatasetTwo() {
        return accuracyOnSourceTrainingDatasetTwo;
    }

    public void setAccuracyOnSourceTrainingDatasetTwo(double accuracyOnSourceTrainingDatasetTwo) {
        this.accuracyOnSourceTrainingDatasetTwo = accuracyOnSourceTrainingDatasetTwo;
    }

    public int getNumberOfGenerationsOnSourceDatasetTwo() {
        return numberOfGenerationsOnSourceDatasetTwo;
    }

    public void setNumberOfGenerationsOnSourceDatasetTwo(int numberOfGenerationsOnSourceDatasetTwo) {
        this.numberOfGenerationsOnSourceDatasetTwo = numberOfGenerationsOnSourceDatasetTwo;
    }

    public long getStartTimeStampOfSourceDatasetTwo() {
        return startTimeStampOfSourceDatasetTwo;
    }

    public void setStartTimeStampOfSourceDatasetTwo(long startTimeStampOfSourceDatasetTwo) {
        this.startTimeStampOfSourceDatasetTwo = startTimeStampOfSourceDatasetTwo;
    }

    public long getStopTimeStampOfSourceDatasetTwo() {
        return stopTimeStampOfSourceDatasetTwo;
    }

    public void setStopTimeStampOfSourceDatasetTwo(long stopTimeStampOfSourceDatasetTwo) {
        this.stopTimeStampOfSourceDatasetTwo = stopTimeStampOfSourceDatasetTwo;
    }

    public boolean isRunSourceTwoSuccessful() {
        return runSourceTwoSuccessful;
    }

    public void setRunSourceTwoSuccessful(boolean runSourceTwoSuccessful) {
        this.runSourceTwoSuccessful = runSourceTwoSuccessful;
    }
}
