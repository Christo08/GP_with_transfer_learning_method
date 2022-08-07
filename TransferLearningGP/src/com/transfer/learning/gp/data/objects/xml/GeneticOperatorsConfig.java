package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GeneticOperatorsConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneticOperatorsConfig {
    private int crossoverSize;
    private int mutationSize;
    private int reproductionSize;
    private int crossbreedingSize;

    public GeneticOperatorsConfig() {
    }

    public GeneticOperatorsConfig(int mod) {
        if (mod == 1){
            crossoverSize = ConfigController.getPercentOfCrossoverOne();
            mutationSize = ConfigController.getPercentOfMutationOne();
            reproductionSize = ConfigController.getPercentOfReproductionOne();
            crossbreedingSize =0;
        }else{
            crossoverSize = ConfigController.getPercentOfCrossoverTwo();
            mutationSize = ConfigController.getPercentOfMutationTwo();
            reproductionSize = ConfigController.getPercentOfReproductionTwo();
            crossbreedingSize = ConfigController.getPercentOfCrossbreeding();
        }
    }

    public int getCrossoverSize() {
        return crossoverSize;
    }

    public void setCrossoverSize(int crossoverSize) {
        this.crossoverSize = crossoverSize;
    }

    public int getMutationSize() {
        return mutationSize;
    }

    public void setMutationSize(int mutationSize) {
        this.mutationSize = mutationSize;
    }

    public int getReproductionSize() {
        return reproductionSize;
    }

    public void setReproductionSize(int reproductionSize) {
        this.reproductionSize = reproductionSize;
    }

    public int getCrossbreedingSize() {
        return crossbreedingSize;
    }

    public void setCrossbreedingSize(int crossbreedingSize) {
        this.crossbreedingSize = crossbreedingSize;
    }
}
