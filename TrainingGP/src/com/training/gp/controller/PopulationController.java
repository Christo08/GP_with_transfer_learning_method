package com.training.gp.controller;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PopulationController {
    //Function set
    private static List<String> mathematicalFunctionSet = new LinkedList<>(Arrays.asList("+","-","/","*","IF",">"));
    private static List<String> logicFunctionSet = new LinkedList<>(Arrays.asList(">=","<=","==","<>"));
    //Terminal set
    private static List<String> terminalSet = new LinkedList<>(Arrays.asList("rand"));

    public static List<String> getMathematicalFunctionSet() {
        return mathematicalFunctionSet;
    }

    public static List<String> getLogicFunctionSet() {
        return logicFunctionSet;
    }

    public static List<String> getTerminalSet() {
        return terminalSet;
    }

    public static void addAttributeToFunctionSet(String attributeName){
        terminalSet.add(attributeName);
    }
}
