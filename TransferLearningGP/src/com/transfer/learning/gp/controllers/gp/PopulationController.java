package com.transfer.learning.gp.controllers.gp;


import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;

import java.util.*;
import java.util.stream.Collectors;

public class PopulationController {
    //Function set

    private static Map<String, Double> dataLine;
    private static final Map<String, Integer> mathSet= new HashMap<>(){{
        put("+",2);
        put("-",2);
        put("/",2);
        put("*",2);
        put("-5",0);
        put("-4",0);
        put("-3",0);
        put("-2",0);
        put("-1",0);
        put("0",0);
        put("1",0);
        put("2",0);
        put("3",0);
        put("4",0);
        put("5",0);
    }};
    private static final Map<String, Integer> booleanSet= new HashMap<>(){{
        put(">=",2);
        put("<=",2);
        put("==",2);
        put("<>",3);
    }};
    private static final Map<String, Integer> consequentSet= new HashMap<>(){{
        put("IF",3);
    }};

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
        if (consequentSet.containsKey(symbol)){
            return consequentSet.get(symbol) == 0;
        } else if (booleanSet.containsKey(symbol)){
            return booleanSet.get(symbol) ==0;
        } else if (mathSet.containsKey(symbol)){
            return mathSet.get(symbol) ==0;
        }
        return false;
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

    public static String getValidSymbol(String symbol) {
        if (mathSet.containsKey(symbol) ||
            booleanSet.containsKey(symbol) ||
            consequentSet.containsKey(symbol))
            return symbol;
        else
            return getRandomAttribute();
    }

    public static String getRandomAttribute() {
        List<String> attributes = new ArrayList<>(consequentSet.keySet());
        attributes.remove("IF");
        return attributes.get(GPController.getRandom().nextInt(attributes.size()));
    }

    public static String getRandomSymbolFromMathematicalSet() {
        List<String> mathSymbols = new ArrayList<>(mathSet.keySet());
        return mathSymbols.get(GPController.getRandom().nextInt(mathSymbols.size()));
    }

    public static String getRandomSymbolFromBooleanSet() {
        List<String> booleanSymbols = new ArrayList<>(booleanSet.keySet());
        return booleanSymbols.get(GPController.getRandom().nextInt(booleanSymbols.size()));
    }

    public static String getRandomSymbolFromConsequentSet() {
        List<String> consequences = new ArrayList<>(consequentSet.keySet());
        return consequences.get(GPController.getRandom().nextInt(consequences.size()));
    }

    public static String getRandomSymbolFromBooleanFunctionSet() {
        List<String> booleanSymbols = new ArrayList<>(booleanSet.keySet());
        for (String key : booleanSet.keySet()) {
            if (booleanSet.get(key) == 0)
                booleanSymbols.remove(key);
        }
        return booleanSymbols.get(GPController.getRandom().nextInt(booleanSymbols.size()));
    }

    public static String getRandomSymbolFromMathematicalFunctionSet() {
        List<String> mathSymbols = new ArrayList<>(mathSet.keySet());
        for (String key : mathSet.keySet()) {
            if (mathSet.get(key) == 0)
                mathSymbols.remove(key);
        }
        return mathSymbols.get(GPController.getRandom().nextInt(mathSymbols.size()));
    }

    public static String getRandomSymbolFromMathematicalTerminalSet() {
        List<String> mathSymbols = new ArrayList<>(mathSet.keySet());
        for (String key : mathSet.keySet()) {
            if (mathSet.get(key) > 0)
                mathSymbols.remove(key);
        }
        return mathSymbols.get(GPController.getRandom().nextInt(mathSymbols.size()));
    }

    public static void addClassToConsequentSet(String className) {
        consequentSet.put(className,0);
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

    public static void addAttributeToMathSet(String attributeName){
        mathSet.put(attributeName,0);
    }

    public static List<Character> getChildrenTypes(String symbol){
        if (symbol.equals("IF")){
            return new ArrayList<>(Arrays.asList('b','c','c'));
        }
        if (mathSet.containsKey(symbol) && mathSet.get(symbol) > 0){
            return new ArrayList<>(Arrays.asList('d','d'));
        }
        if (booleanSet.containsKey(symbol)){
            if (booleanSet.get(symbol) == 2)
                return new ArrayList<>(Arrays.asList('d','d'));
            else if (booleanSet.get(symbol) == 3)
                return new ArrayList<>(Arrays.asList('d','d','d'));
        }
        return new ArrayList<>();
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
