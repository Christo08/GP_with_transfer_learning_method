package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Run")
@XmlAccessorType(XmlAccessType.FIELD)
public class Run {
    private int runNumber;

    private long seed;

    private long startTimeStamp;
    private long stopTimeStamp;
    private long totalDuration;

    //Source one dataset
    private SourceDataSet sourceDataSetOne;
    //Source two dataset
    private SourceDataSet sourceDataSetTwo;
    //Target dataset
    private TargetDataSet targetDataSet;

    public Run() {
    }

    public Run(int runNumber, long seed, long startTimeStamp) {
        this.runNumber = runNumber;
        this.seed = seed;
        this.startTimeStamp = startTimeStamp;
    }

    public void setStopTimeStamp(long stopTimeStamp) {
        this.stopTimeStamp = stopTimeStamp;
        this.totalDuration = this.stopTimeStamp - this.startTimeStamp;
    }

    public void setSourceDataSetOne(SourceDataSet sourceDataSetOne) {
        this.sourceDataSetOne = sourceDataSetOne;
    }

    public void setSourceDataSetTwo(SourceDataSet sourceDataSetTwo) {
        this.sourceDataSetTwo = sourceDataSetTwo;
    }

    public void setTargetDataSet(TargetDataSet targetDataSet) {
        this.targetDataSet = targetDataSet;
    }

    public double getTestingAccuracy() {
        return targetDataSet.getTestingAccuracy();
    }

    public double getTrainingAccuracy() {
        return targetDataSet.getTrainingAccuracy();
    }

    public double getGenerations() {
        return targetDataSet.getNumberOfGenerations();
    }

    public double getTotalDuration() {
        return totalDuration;
    }

    public double getDuration() {
        return targetDataSet.getDuration();
    }
}
