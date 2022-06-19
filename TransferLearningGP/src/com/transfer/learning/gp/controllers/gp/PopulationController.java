package com.transfer.learning.gp.controllers.gp;


import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.data.objects.Chromosome;

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

    private Map<Integer, Chromosome> chromosomes;
    private Map<Integer, Double> chromosomesFitness;

    public PopulationController() {
        this.chromosomes = new HashMap<>();
        for (int counter =0;  counter < ConfigController.getPopulationSize(); counter++)
        {
            Chromosome newChromosome;
            do {
                newChromosome = new Chromosome(ConfigController.getMaxDepth(),(counter% 2 ==0));
            }while (containsChromosome(newChromosome));
            chromosomes.put(counter, newChromosome);
        }
        this.chromosomesFitness = new HashMap<>();
    }

    private boolean containsChromosome(Chromosome newChromosome) {
        for (Chromosome value : chromosomes.values()) {
            if (value.hashCode() == newChromosome.hashCode())
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
        return chromosomes.get(index);
    }

    public void addFitnessOfChromosomes(int index, double fitness){
        if (chromosomesFitness.size() < ConfigController.getPopulationSize()){
            chromosomesFitness.put(index,fitness);
        }else{
            chromosomesFitness.replace(index,fitness);
        }
    }

    public double getFitnessOfChromosomes(int index){
        return chromosomesFitness.get(index);
    }

    public double evaluateChromosomes(Map<String, Double> dataLine, int chromosomesIndex) {
        this.dataLine = dataLine;
        return chromosomes.get(chromosomesIndex).evaluate();
    }

    public Chromosome mutationChromosomes(int chromosomesIndex) {
        Chromosome newChromosomes = new Chromosome(chromosomes.get(chromosomesIndex));
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
        Chromosome newChromosomesOne = new Chromosome(chromosomes.get(chromosomesOnesIndex));
        int subTreeIDOfChromosomesOne = newChromosomesOne.getRandomSubTreeID();
        char subTreeTypeOfChromosomesOne  = newChromosomesOne.getTypeOfNode(subTreeIDOfChromosomesOne);;

        Chromosome newChromosomesTwo = new Chromosome(chromosomes.get(chromosomeTwosIndex));
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
            outputs.add(new Chromosome(chromosomes.get(index)));
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

    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = new HashMap<>();
        for (int counter =0;  counter < chromosomes.size(); counter++)
        {
            this.chromosomes.put(counter, chromosomes.get(counter));
        }
    }

    public void sortedPopulation() {
        Map<Integer, Double> resultFitness = new LinkedHashMap<>();
        Map<Integer, Chromosome> resultChromosomes = new LinkedHashMap<>();
        List<Map.Entry<Integer, Double>> entries =chromosomesFitness.entrySet().stream()
                                                                               .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                                                                               .collect(Collectors.toList());
        for (int counter=0; counter< entries.size();counter++){
            Map.Entry<Integer, Double> entry =entries.get(counter);
            resultFitness.put(counter, entry.getValue());
            resultChromosomes.put(counter, chromosomes.get(entry.getKey()));
        }

        chromosomes = resultChromosomes;
        chromosomesFitness = resultFitness;
    }

    public List<Chromosome> getTopChromosomes(int numberOfChromosomes) {
        List<Chromosome> output = new LinkedList<>();
        for (int counter =0; counter < numberOfChromosomes; counter++){
            output.add(chromosomes.get(counter));
        }
        return output;
    }
}
