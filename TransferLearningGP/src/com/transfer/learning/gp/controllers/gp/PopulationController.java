package com.transfer.learning.gp.controllers.gp;


import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;
import com.transfer.learning.gp.data.objects.ChromosomeWrapper;
import com.transfer.learning.gp.data.objects.Function;

import java.util.*;
import java.util.stream.Collectors;

public class PopulationController {
    //Data
    private static Map<String, Double> dataLine;

    //Function and terminal sets
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
        put("true",0);
        put("false",0);
    }};
    private static final Map<String, Integer> consequentSet= new HashMap<>(){{
        put("IF",3);
    }};

    //Populations
    private List<ChromosomeWrapper> chromosomes;
    private List<Chromosome> alienChromosomes = new ArrayList<>();

    //Functions
    private static Map<String, Function> functions;
    private static boolean hasFunctions =false;

    public PopulationController() {
        this.chromosomes = new ArrayList<>();
    }

    public static double evaluateConsequentFunction(String symbol, ArrayList<Chromosome> children) {
        Queue<Boolean> booleanInputs = new LinkedList<>();
        Queue<Double> doubleInputs = new LinkedList<>();
        Queue<Double> consequentInputs = new LinkedList<>();
        for (Chromosome child : children) {
            if (child.getType() =='c')
                consequentInputs.add(child.evaluateConsequent());
            else if (child.getType() =='b')
                booleanInputs.add(child.evaluateBoolean());
            else if (child.getType() =='d')
                doubleInputs.add(child.evaluateDouble());
        }
        return functions.get(symbol).evaluateConsequent(consequentInputs, booleanInputs, doubleInputs);
    }

    public static boolean evaluateBooleanFunction(String symbol, ArrayList<Chromosome> children) {
        Queue<Boolean> booleanInputs = new LinkedList<>();
        Queue<Double> doubleInputs = new LinkedList<>();
        for (Chromosome child : children) {
            if (child.getType() =='b')
                booleanInputs.add(child.evaluateBoolean());
            else if (child.getType() =='d')
                doubleInputs.add(child.evaluateDouble());
        }
        return functions.get(symbol).evaluateBoolean(booleanInputs, doubleInputs);
    }

    public static double evaluateMathFunction(String symbol, ArrayList<Chromosome> children) {
        Queue<Double> doubleInputs = new LinkedList<>();
        for (Chromosome child : children) {
            doubleInputs.add(child.evaluateDouble());
        }
        return functions.get(symbol).evaluateDouble(doubleInputs);
    }

    public static boolean isFunction(String symbol) {
        return hasFunctions && functions.containsKey(symbol);
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

    public static String getValidSymbol(String symbol, Character type) {
        if (type == 'c'){
            if (consequentSet.containsKey(symbol))
                return symbol;
            else
                return getRandomConsequentTerminal();
        }else if (type == 'b'){
            if (booleanSet.containsKey(symbol))
                return symbol;
            else
                return getRandomBooleanTerminal();
        }else{
            if (mathSet.containsKey(symbol))
                return symbol;
            else
                return getRandomMathTerminal();
        }
    }

    public static String getRandomMathTerminal(){
        List<String> mathTerminal = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : mathSet.entrySet()) {
            if (pair.getValue() ==0){
                mathTerminal.add(pair.getKey());
            }
        }
        return mathTerminal.get(GPController.getRandom().nextInt(mathTerminal.size()));
    }

    public static String getRandomMathFunction(){
        List<String> mathFunction = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : mathSet.entrySet()) {
            if (pair.getValue() != 0){
                mathFunction.add(pair.getKey());
            }
        }
        return mathFunction.get(GPController.getRandom().nextInt(mathFunction.size()));
    }

    public static String getRandomConsequentTerminal(){
        List<String> consequentTerminal = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : consequentSet.entrySet()) {
            if (pair.getValue() ==0){
                consequentTerminal.add(pair.getKey());
            }
        }
        return consequentTerminal.get(GPController.getRandom().nextInt(consequentTerminal.size()));
    }

    public static String getRandomConsequentFunction(){
        List<String> booleanFunction = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : consequentSet.entrySet()) {
            if (pair.getValue() != 0){
                booleanFunction.add(pair.getKey());
            }
        }
        return booleanFunction.get(GPController.getRandom().nextInt(booleanFunction.size()));
    }

    public static String getRandomBooleanTerminal(){
        List<String> booleanTerminal = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : booleanSet.entrySet()) {
            if (pair.getValue() ==0){
                booleanTerminal.add(pair.getKey());
            }
        }
        return booleanTerminal.get(GPController.getRandom().nextInt(booleanTerminal.size()));
    }

    public static String getRandomBooleanFunction(){
        List<String> booleanFunction = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : booleanSet.entrySet()) {
            if (pair.getValue() != 0){
                booleanFunction.add(pair.getKey());
            }
        }
        return booleanFunction.get(GPController.getRandom().nextInt(booleanFunction.size()));
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

    public void addFitnessOfChromosomes(int index, double fitness){
        chromosomes.get(index).fitness = fitness;
    }

    public double getFitnessOfChromosomes(int index){
        return chromosomes.get(index).fitness;
    }

    public double evaluateChromosomes(Map<String, Double> dataLine, int chromosomesIndex) {
        this.dataLine = dataLine;
        return chromosomes.get(chromosomesIndex).chromosome.evaluateConsequent();
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

        return new ArrayList<>(Arrays.asList(newChromosomesOne,newChromosomesTwo));
    }

    public List<Chromosome> reproductionChromosomes(List<Integer> chromosomesIndexes){
        List<Chromosome> outputs = new ArrayList<>();
        for (int index: chromosomesIndexes){
            outputs.add(new Chromosome(chromosomes.get(index).chromosome));
        }
        return outputs;
    }

    public static void addAttributeToMathSet(String attributeName){
        mathSet.put(attributeName,0);
    }

    public static List<Character> getChildrenTypes(String symbol){
        if (hasFunctions && functions.containsKey(symbol)){
            return functions.get(symbol).getParameterTypes();
        }
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

    public List<ChromosomeWrapper> getTopChromosomes(int numberOfChromosomes) {
        List<ChromosomeWrapper> output = new ArrayList<>();
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

    public void createPopulation() {
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

    public void addAliens(List<Chromosome> alienChromosomes) {
        this.alienChromosomes.addAll(alienChromosomes);
    }

    public Chromosome crossbreedingChromosomes(int index) {
        Chromosome newChromosomes = new Chromosome(chromosomes.get(index).chromosome);
        int subTreeIDOfChromosomesOne = newChromosomes.getRandomSubTreeID();
        char subTreeTypeOfChromosomesOne  = newChromosomes.getTypeOfNode(subTreeIDOfChromosomesOne);

        int chromosomeTwosIndex =  GPController.getRandom().nextInt(alienChromosomes.size());
        Chromosome newChromosomesTwo = new Chromosome(alienChromosomes.get(chromosomeTwosIndex));
        int subTreeIDOfChromosomesTwo = newChromosomesTwo.getRandomSubTreeID();
        char subTreeTypeOfChromosomesTwo = newChromosomesTwo.getTypeOfNode(subTreeIDOfChromosomesTwo);

        int counter =0;

        while (subTreeTypeOfChromosomesTwo != subTreeTypeOfChromosomesOne) {
            if (counter % 2 == 0){
                subTreeIDOfChromosomesTwo = newChromosomesTwo.getRandomSubTreeID();
                subTreeTypeOfChromosomesTwo = newChromosomesTwo.getTypeOfNode(subTreeIDOfChromosomesTwo);
            }else{
                subTreeIDOfChromosomesOne = newChromosomes.getRandomSubTreeID();
                subTreeTypeOfChromosomesOne = newChromosomes.getTypeOfNode(subTreeIDOfChromosomesOne);
            }
            counter++;
        }

        Chromosome subTreeTwo = newChromosomesTwo.getSubTree(subTreeIDOfChromosomesTwo);

        if (subTreeIDOfChromosomesOne == 1){
            newChromosomes = subTreeTwo;
        }else{
            newChromosomes.replaceSubTree(subTreeIDOfChromosomesOne, subTreeTwo);
        }

        newChromosomes.renumberTheNodes(true);

        return newChromosomes;
    }

    public void addFunctions(List<Function> functions) {
        functions.stream()
                .filter(function -> function.getReturnType() == 'd')
                .forEach(function -> mathSet.put(function.getName(),function.getNumberOfParameters()));
        functions.stream()
                .filter(function -> function.getReturnType() == 'b')
                .forEach(function -> booleanSet.put(function.getName(),function.getNumberOfParameters()));
        functions.stream()
                .filter(function -> function.getReturnType() == 'c')
                .forEach(function -> consequentSet.put(function.getName(),function.getNumberOfParameters()));
        this.functions =functions.stream().collect(Collectors.toMap(Function::getName, Function::getSelf));
        hasFunctions = true;
    }

    public void resetSets() {
        List<String> mathKeys = new ArrayList<>(mathSet.keySet());
        mathKeys.stream()
                .filter(key -> (!isNumeric(key) && mathSet.get(key) == 0) || (hasFunctions && functions.containsKey(key)))
                .forEach(key -> mathSet.remove(key));

        List<String> consequentKeys = new ArrayList<>(consequentSet.keySet());
        consequentKeys.stream()
                      .filter(key -> (consequentSet.get(key) == 0) || (hasFunctions && functions.containsKey(key)))
                      .forEach(key -> consequentSet.remove(key));

        List<String> booleanKeys = new ArrayList<>(booleanSet.keySet());
        booleanKeys.stream()
                   .filter(key -> hasFunctions && functions.containsKey(key))
                   .forEach(key -> booleanSet.remove(key));
        hasFunctions = false;
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
