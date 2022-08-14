package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TargetDataSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class TargetDataSet {
    private double testingAccuracy;

    private double trainingAccuracy;

    private int numberOfGenerations;

    private long startTimeStamp;
    private long stopTimeStamp;
    private long duration;

    private boolean hasError;

    public TargetDataSet() {
    }

    public TargetDataSet(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void setTestingAccuracy(double testingAccuracy) {
        this.testingAccuracy = testingAccuracy;
    }

    public void setTrainingAccuracy(double trainingAccuracy) {
        this.trainingAccuracy = trainingAccuracy;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setStopTimeStamp(long stopTimeStamp) {
        this.stopTimeStamp = stopTimeStamp;
        this.duration = this.stopTimeStamp - this.startTimeStamp;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public double getTestingAccuracy() {
        return testingAccuracy;
    }

    public double getTrainingAccuracy() {
        return trainingAccuracy;
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public long getDuration() {
        return duration;
    }
}
