package com.transfer.learning.gp.data.objects.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TransferLearningMethod")
@XmlAccessorType(XmlAccessType.FIELD)
public class TransferLearningMethod {
    private String methodName;
    private String pathToData;
    private int populationSizeSave;

    public TransferLearningMethod() {
    }

    public TransferLearningMethod(String methodName, String pathToData) {
        this.methodName = methodName;
        this.pathToData = pathToData;
    }

    public String getMethodName() {
        return methodName;
    }
}
