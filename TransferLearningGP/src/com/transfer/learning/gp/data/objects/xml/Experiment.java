package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Experiment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Experiment {

    private String dataSetName;

    private TransferLearningMethod transferLearningMethod;

    private List<Run> runs;

    private GPSettings gpSettings;

    private double avrgTestingAccuracy=0;

    private double avrgTrainingAccuracy=0;

    private double avrgGenerations=0;

    private double avrgTotalDuration=0;

    private double avrgDuration=0;

    public Experiment() {
        runs = new ArrayList<>();
    }

    public Experiment(String dataSetName) {
        this();
        this.dataSetName = dataSetName;
        this.gpSettings = new GPSettings();
    }

    public Experiment(String dataSetName, TransferLearningMethod transferLearningMethod) {
        this(dataSetName);
        this.transferLearningMethod = transferLearningMethod;
        if (transferLearningMethod.getMethodName().contains("GPCR"))
            gpSettings.setGPCRGeneticOperatorsConfigs(new GeneticOperatorsConfig(2));
    }

    public void addRun(Run newRun) {
        this.runs.add(newRun);
        recalculateAverages();
    }

    private void recalculateAverages(){
        avrgTestingAccuracy=0;
        avrgTrainingAccuracy=0;
        avrgGenerations=0;
        avrgTotalDuration=0;
        avrgDuration=0;
        for (Run run : runs) {
            avrgTestingAccuracy += run.getTestingAccuracy();
            avrgTrainingAccuracy += run.getTrainingAccuracy();
            avrgGenerations += run.getGenerations();
            avrgTotalDuration += run.getTotalDuration();
            avrgDuration += run.getDuration();
        }
        avrgTestingAccuracy = avrgTestingAccuracy/(double) runs.size();
        avrgTrainingAccuracy = avrgTrainingAccuracy/(double) runs.size();
        avrgGenerations = avrgGenerations/(double) runs.size();
        avrgTotalDuration = avrgTotalDuration/(double) runs.size();
        avrgDuration = avrgDuration/(double) runs.size();

    }

}
