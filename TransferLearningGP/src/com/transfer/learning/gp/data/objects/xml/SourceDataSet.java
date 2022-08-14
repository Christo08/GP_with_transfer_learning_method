package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SourceDataSet")
@XmlAccessorType(XmlAccessType.FIELD)
public class SourceDataSet {
    private double accuracy;

    private int numberOfGenerations;

    private long startTimeStamp;
    private long stopTimeStamp;
    private long duration;

    private boolean hasError;

    public SourceDataSet() {
    }

    public SourceDataSet(long startTimeStamp) {
        this.startTimeStamp = startTimeStamp;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setNumberOfGenerations(int numberOfGenerations) {
        this.numberOfGenerations = numberOfGenerations;
    }

    public void setStopTimeStamp(long stopTimeStamp) {
        this.stopTimeStamp = stopTimeStamp;
        this.duration = this.stopTimeStamp - this.startTimeStamp;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
