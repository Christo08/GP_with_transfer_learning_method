package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.*;

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
        }else{
            crossoverSize = ConfigController.getPercentOfCrossoverTwo();
            mutationSize = ConfigController.getPercentOfMutationTwo();
            reproductionSize = ConfigController.getPercentOfReproductionTwo();
            crossbreedingSize = ConfigController.getPercentOfCrossbreeding();
        }
    }
}
