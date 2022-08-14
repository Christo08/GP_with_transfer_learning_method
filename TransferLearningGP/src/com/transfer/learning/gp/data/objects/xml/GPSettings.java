package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GPSettings")
@XmlAccessorType(XmlAccessType.FIELD)
public class GPSettings {
    private int maxDepthOfStartingChromosomes;

    private double padding;
    private double maxAccuracy;
    private int numberOfRuns;

    private int populationSize;
    private int tournamentSize;

    private GeneticOperatorsConfig defaultGeneticOperatorsConfigs;
    private GeneticOperatorsConfig GPCRGeneticOperatorsConfigs;

    public GPSettings() {
        maxDepthOfStartingChromosomes = ConfigController.getMaxDepth();
        maxAccuracy = ConfigController.getMaxAccuracy();
        populationSize = ConfigController.getPopulationSize();
        tournamentSize = ConfigController.getTournamentSize();
        numberOfRuns = ConfigController.getNumberOfRuns();
        padding = ConfigController.getPadding();
        defaultGeneticOperatorsConfigs = new GeneticOperatorsConfig(1);
    }

    public void setGPCRGeneticOperatorsConfigs(GeneticOperatorsConfig geneticOperatorsConfigsWithTransferLearning) {
        this.GPCRGeneticOperatorsConfigs = geneticOperatorsConfigsWithTransferLearning;
    }
}
