package com.training.gp.controller;


import java.util.*;

public class PopulationController {
    //Function set
    private static Map<String,Integer> mathematicalFunctionSet = new HashMap<>(){{
        put("+",2);
        put("-",2);
        put("/",2);
        put("*",2);
        put("IF",3);
    }};
    private static Map<String,Integer> logicFunctionSet = new HashMap<>(){{
        put(">=",2);
        put("<=",2);
        put("==",2);
        put("<>",3);
    }};
    //Terminal set
    private static List<String> terminalSet = new LinkedList<>(Arrays.asList("rand"));

    private static Map<String, Double> dataLine;

    private List<Chromosomes> chromosomes;

    public PopulationController() {
        this.chromosomes = new LinkedList<>();
        for (int counter =0;  counter < ConfigController.getPopulationSize(); counter++)
        {
            chromosomes.add(new Chromosomes(ConfigController.getMaxDepth(),(counter% 2 ==0)));
        }
    }

    public static double getValueOf(String symbol) {
        if (dataLine.containsKey(symbol)){
            return dataLine.get(symbol);
        }else {
            return Double.parseDouble(symbol);
        }
    }

    public Chromosomes getChromosomes(int index){
        return chromosomes.get(index);
    }

    public double evaluateChromosomes(Map<String, Double> dataLine, int chromosomesNumber) {
        this.dataLine = dataLine;
        return chromosomes.get(chromosomesNumber).evaluate();
    }

    public Chromosomes mutationChromosomes(int chromosomesIndex) {
        Chromosomes newChromosomes = new Chromosomes(chromosomes.get(chromosomesIndex));
        int id = newChromosomes.getRandomSubTreeID();
        char type = newChromosomes.getTypeOfNode(id);
        if (id == 1){
            return new Chromosomes(ConfigController.getMaxDepth(), type, false);
        }else{
            newChromosomes.replaceSubTree(id, new Chromosomes(ConfigController.getMaxDepth(), type, false));
            newChromosomes.renumberTheNodes(true);
            return newChromosomes;
        }
    }

    public List<Chromosomes> crossoverChromosomes(int chromosomesOnesIndex, int chromosomeTwosIndex){
        Chromosomes newChromosomesOne = new Chromosomes(chromosomes.get(chromosomesOnesIndex));
        int subTreeIDOfChromosomesOne = newChromosomesOne.getRandomSubTreeID();
        char subTreeTypeOfChromosomesOne  = newChromosomesOne.getTypeOfNode(subTreeIDOfChromosomesOne);;

        Chromosomes newChromosomesTwo = new Chromosomes(chromosomes.get(chromosomeTwosIndex));
        int subTreeIDOfChromosomesTwo = newChromosomesTwo.getRandomSubTreeID();
        char subTreeTypeOfChromosomesTwo = newChromosomesTwo.getTypeOfNode(subTreeIDOfChromosomesTwo);

        int counter =0;

        while (subTreeTypeOfChromosomesTwo != subTreeTypeOfChromosomesOne) {
            if (counter % 2 == 0){
                subTreeIDOfChromosomesTwo = newChromosomesTwo.getRandomSubTreeID();
                subTreeTypeOfChromosomesTwo = newChromosomesTwo.getTypeOfNode(subTreeIDOfChromosomesTwo);
            }else{
                subTreeIDOfChromosomesOne = newChromosomesOne.getRandomSubTreeID();
                subTreeTypeOfChromosomesOne = newChromosomesOne.getTypeOfNode(subTreeIDOfChromosomesOne);
            }
        }

        Chromosomes subTreeOne = newChromosomesOne.getSubTree(subTreeIDOfChromosomesOne);
        Chromosomes subTreeTwo = newChromosomesTwo.getSubTree(subTreeIDOfChromosomesTwo);

        if (subTreeIDOfChromosomesOne == 1){
            newChromosomesOne = subTreeTwo;
        }else{
            newChromosomesOne.replaceSubTree(subTreeIDOfChromosomesOne, subTreeTwo);
        }
        if (subTreeIDOfChromosomesTwo == 1){
            newChromosomesTwo = subTreeOne;
        }else{
            newChromosomesTwo.replaceSubTree(subTreeIDOfChromosomesTwo, subTreeOne);
        }

        newChromosomesOne.renumberTheNodes(true);
        newChromosomesTwo.renumberTheNodes(true);

        return new LinkedList<>(Arrays.asList(newChromosomesOne,newChromosomesTwo));
    }

    public List<Chromosomes> reproductionChromosomes(List<Integer> chromosomesIndexes){
        List<Chromosomes> outputs = new LinkedList<>();
        for (int index: chromosomesIndexes){
            outputs.add(new Chromosomes(chromosomes.get(index)));
        }
        return outputs;
    }

    public static String getRandomSymbolFromMathematicalFunctionSet() {
        List<String> pKeys = new LinkedList<>(mathematicalFunctionSet.keySet());
        return pKeys.get(GPController.getRandom().nextInt(mathematicalFunctionSet.size()-1));
    }

    public static String getRandomSymbolFromLogicFunctionSet() {
        List<String> pKeys = new LinkedList<>(logicFunctionSet.keySet());
        return pKeys.get(GPController.getRandom().nextInt(logicFunctionSet.size()-1));
    }

    public static String getRandomSymbolFromTerminalSet() {
        String symbol =  terminalSet.get(GPController.getRandom().nextInt(terminalSet.size()-1));
        if (symbol.equals("rand")) {
            return Double.toString((GPController.getRandom().nextDouble() - 0.5) * 2);
        }
        return symbol;
    }

    public static void addAttributeToFunctionSet(String attributeName){
        terminalSet.add(attributeName);
    }

    public static List<Character> getChildrenTypes(String symbol){
        if (mathematicalFunctionSet.keySet().contains(symbol)) {
            if (mathematicalFunctionSet.get(symbol) == 2){
                return new ArrayList<>(Arrays.asList('d','d'));
            }else{
                return new ArrayList<>(Arrays.asList('b','d','d'));
            }
        }
        else if (logicFunctionSet.keySet().contains(symbol)) {
            if (logicFunctionSet.get(symbol) == 2){
                return new ArrayList<>(Arrays.asList('d','d'));
            }else{
                return new ArrayList<>(Arrays.asList('d','d','d'));
            }
        }
        else
            return null;

    }
}
