package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Run")
@XmlAccessorType(XmlAccessType.FIELD)
public class Run {
    private double accuracyOnTestingDataset;
    private double accuracyOnTrainingDataset;
    private int numberOfGenerations;
    private int runNumber;
    private long startTimeStamp;
    private long stopTimeStamp;
    private boolean runSuccessful;

    public Run() {
    }

    public double getAccuracyOnTestingDataset() {
        return accuracyOnTestingDataset;
    }

    public void setAccuracyOnTestingDataset(double accuracyOnTestingDataset) {
        this.accuracyOnTestingDataset = accuracyOnTestingDataset;
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public double getAccuracyOnTrainingDataset() {
        return accuracyOnTrainingDataset;
    }

    public void setAccuracyOnTrainingDataset(double accuracyOnTrainingDataset) {
        this.accuracyOnTrainingDataset = accuracyOnTrainingDataset;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }

    public long getStartTimeStamp() {
        return startTimeStamp;
    }

    public void setStartTimeStamp(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public long getStopTimeStamp() {
        return stopTimeStamp;
    }

    public void setStopTimeStamp(long stopTimeStamp) {
        this.stopTimeStamp = stopTimeStamp;
    }

    public boolean isRunSuccessful() {
        return runSuccessful;
    }

    public void setRunSuccessful(boolean runSuccessful) {
        this.runSuccessful = runSuccessful;
    }
}
