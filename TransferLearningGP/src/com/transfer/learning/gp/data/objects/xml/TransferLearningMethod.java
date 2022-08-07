package com.transfer.learning.gp.data.objects.xml;

import com.transfer.learning.gp.controllers.ConfigController;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TransferLearningMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferLearningMethod {
    private String methodName;
    private String pathToData;
    private int populationSizeSave;

    public TransferLearningMethod() {
        this.methodName = "n.a.";
        this.pathToData = "n.a.";
        this.populationSizeSave = -1;
    }

    public TransferLearningMethod(String methodName, String pathToData) {
        this.methodName = methodName;
        this.pathToData = pathToData;
        this.populationSizeSave = ConfigController.getPopulationSize();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getPathToData() {
        return pathToData;
    }

    public void setPathToData(String pathToData) {
        this.pathToData = pathToData;
    }

    public int getPopulationSizeSave() {
        return populationSizeSave;
    }

    public void setPopulationSizeSave(int populationSizeSave) {
        this.populationSizeSave = populationSizeSave;
    }
}
