package com.transfer.learning.gp.controllers.gp;


import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;

import java.util.*;
import java.util.stream.Collectors;

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

    private List<ChromosomeWrapper> chromosomes;

    public PopulationController() {
        this.chromosomes = new ArrayList<>();
        for (int counter =0;  counter < ConfigController.getPopulationSize(); counter++)
        {
            Chromosome newChromosome;
            do {
                newChromosome = new Chromosome(ConfigController.getMaxDepth(),(counter% 2 ==0));
            }while (containsChromosome(newChromosome));
            ChromosomeWrapper newChromosomeWrapper = new ChromosomeWrapper(newChromosome);
            chromosomes.add(newChromosomeWrapper);
        }
    }

    public static boolean terminalSetContains(String symbol) {
        return terminalSet.contains(symbol);
    }

    public static String validSymbol(String symbol) {
        if (terminalSet.contains(symbol) ||
            logicFunctionSet.containsKey(symbol) ||
            mathematicalFunctionSet.containsKey(symbol))
            return symbol;
        else
            return getRandomSymbolFromTerminalSet();
    }

    public static List<Chromosome> getSubTrees(Chromosome bestChromosome) {
        List<Chromosome> nodesOfChromosomes = bestChromosome.getNodes();
        List<Chromosome> output = new ArrayList<>();
        for (Chromosome nodesOfChromosome : nodesOfChromosomes) {
            for (int counter =1; counter <= ConfigController.getDepthOfPSTTree(); counter++){
                Chromosome newSubTree = nodesOfChromosome.getSubTreeOfDepth(counter);
                if (output.stream().anyMatch(subTree -> subTree.toString().equals(newSubTree.toString())))
                    output.add(newSubTree);
            }
        }
        return output;
    }

    private boolean containsChromosome(Chromosome newChromosome) {
        for (ChromosomeWrapper chromosomeWrapper : chromosomes) {
            if (chromosomeWrapper.chromosome.hashCode() == newChromosome.hashCode())
                return true;
        }
        return false;
    }

    public static double getValueOf(String symbol) {
        if (dataLine.containsKey(symbol)){
            return dataLine.get(symbol);
        }else {
            return Double.parseDouble(symbol);
        }
    }

    public Chromosome getChromosomes(int index){
        return chromosomes.get(index).chromosome;
    }

    public void addFitnessOfChromosomes(int index, double fitness){
        chromosomes.get(index).fitness = fitness;
    }

    public double getFitnessOfChromosomes(int index){
        return chromosomes.get(index).fitness;
    }

    public double evaluateChromosomes(Map<String, Double> dataLine, int chromosomesIndex) {
        this.dataLine = dataLine;
        return chromosomes.get(chromosomesIndex).chromosome.evaluate();
    }

    public Chromosome mutationChromosomes(int chromosomesIndex) {
        Chromosome newChromosomes = new Chromosome(chromosomes.get(chromosomesIndex).chromosome);
        int id = newChromosomes.getRandomSubTreeID();
        char type = newChromosomes.getTypeOfNode(id);
        if (id == 1){
            newChromosomes = new Chromosome(ConfigController.getMaxDepth(), type, false);
        }else{
            newChromosomes.replaceSubTree(id, new Chromosome(ConfigController.getMaxDepth(), type, false));
        }
        newChromosomes.renumberTheNodes(true);
        return newChromosomes;
    }

    public List<Chromosome> crossoverChromosomes(int chromosomesOnesIndex, int chromosomeTwosIndex){
        Chromosome newChromosomesOne = new Chromosome(chromosomes.get(chromosomesOnesIndex).chromosome);
        int subTreeIDOfChromosomesOne = newChromosomesOne.getRandomSubTreeID();
        char subTreeTypeOfChromosomesOne  = newChromosomesOne.getTypeOfNode(subTreeIDOfChromosomesOne);;

        Chromosome newChromosomesTwo = new Chromosome(chromosomes.get(chromosomeTwosIndex).chromosome);
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
            counter++;
        }

        Chromosome subTreeOne = newChromosomesOne.getSubTree(subTreeIDOfChromosomesOne);
        Chromosome subTreeTwo = newChromosomesTwo.getSubTree(subTreeIDOfChromosomesTwo);

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

    public List<Chromosome> reproductionChromosomes(List<Integer> chromosomesIndexes){
        List<Chromosome> outputs = new LinkedList<>();
        for (int index: chromosomesIndexes){
            outputs.add(new Chromosome(chromosomes.get(index).chromosome));
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
            return Double.toString((GPController.getRandom().nextDouble() * 20)-10);
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

    public static List<Chromosome> getCommonSubTree(Chromosome chromosomeOfPopulation1, Chromosome chromosomeOfPopulation2){

        List<Chromosome> nodesOfChromosomesX = chromosomeOfPopulation1.getNodes();
        List<Chromosome> nodesOfChromosomesY = chromosomeOfPopulation2.getNodes();

        Map<Chromosome,List<Chromosome>> commonXNodes = new HashMap<>();

        for (Chromosome nodeX:nodesOfChromosomesX) {
            List<Chromosome> nodesInCommon = nodesOfChromosomesY.stream()
                    .filter(nodeY -> nodeY.getSymbol().equals(nodeX.getSymbol()))
                    .collect(Collectors.toList());
            if (nodesInCommon.size() != 0){
                commonXNodes.put(nodeX,nodesInCommon);
            }
        }

        Map<Chromosome,List<Chromosome>> commonTrees = new HashMap<>();
        for (Chromosome nodeX : commonXNodes.keySet()) {
            List<Chromosome> subTrees = new ArrayList<>();
            for (Chromosome nodeY : commonXNodes.get(nodeX)) {
                Chromosome commonTree = nodeX.getCommonSubTree(nodeY);
                commonTree.renumberTheNodes(true);
                subTrees.add(commonTree);
            }
            commonTrees.put(nodeX,subTrees);
        }
        List<Chromosome> functionSet = new ArrayList<>();
        for (Chromosome node : commonTrees.keySet()) {
            for (Chromosome function : commonTrees.get(node)) {
                if (!functionSet.stream().anyMatch(saveFunction -> saveFunction.toString().equals(function.toString())))
                    functionSet.add(function);
            }
        }
        return functionSet;
    }

    public void setChromosomes(List<Chromosome> newChromosomes) {
        this.chromosomes = new ArrayList<>();
        for (int counter =0;  counter < newChromosomes.size(); counter++)
        {
            this.chromosomes.add(new ChromosomeWrapper(newChromosomes.get(counter)));
        }
    }

    public void sortedPopulation() {
        chromosomes = chromosomes.stream()
                                 .sorted((chromosomeWrapper1, chromosomeWrapper2) -> Double.compare(chromosomeWrapper2.fitness, chromosomeWrapper1.fitness))
                                 .collect(Collectors.toList());
    }

    public List<ChromosomeWrapper> getTopChromosomes(int numberOfChromosomes) {
        List<ChromosomeWrapper> output = new LinkedList<>();
        for (int counter =0; counter < numberOfChromosomes; counter++){
            output.add(chromosomes.get(counter));
        }
        return output;
    }

    public void replaceChromosomes(List<ChromosomeWrapper> importFullTree) {
        this.chromosomes = importFullTree;
        for (int counter =chromosomes.size();  counter < ConfigController.getPopulationSize(); counter++)
        {
            Chromosome newChromosome;
            do {
                newChromosome = new Chromosome(ConfigController.getMaxDepth(),(counter% 2 ==0));
            }while (containsChromosome(newChromosome));
            ChromosomeWrapper newChromosomeWrapper = new ChromosomeWrapper(newChromosome);
            chromosomes.add(newChromosomeWrapper);
        }
    }

    public void clearPopulation() {
        chromosomes.clear();

        for (int counter =0;  counter < ConfigController.getPopulationSize(); counter++)
        {
            Chromosome newChromosome;
            do {
                newChromosome = new Chromosome(ConfigController.getMaxDepth(),(counter% 2 ==0));
            }while (containsChromosome(newChromosome));
            ChromosomeWrapper newChromosomeWrapper = new ChromosomeWrapper(newChromosome);
            chromosomes.add(newChromosomeWrapper);
        }
    }
}
