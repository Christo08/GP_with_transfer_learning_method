package com.transfer.learning.gp.controllers.data;

import com.transfer.learning.gp.controllers.ConfigController;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public abstract class DataController {
    private NumberFormat formatter = new DecimalFormat("#0.00");
    protected List<Map<String, Double>> dataSet;
    protected String dataSetName;

    public List<Map<String, Double>> getDataSet() {
        return dataSet;
    }

    public abstract void chanceMod();

    protected void printProgress(double current, double total, String dataSetName) {

        StringBuilder string = new StringBuilder(140);
        double percent = (current/total)* 100;
        string.append('\r')
                .append(formatter.format(percent))
                .append("% of ")
                .append(dataSetName)
                .append(" dataset loaded.");

        System.out.print(string);
    }

}
