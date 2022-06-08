package com.training.gp.controller;

import java.util.*;

public class SDRSController {
    private Map<Double, String> mapOfOutputToClass;
    private Map<Double, List<Double>> mapOfOutputToClassCounter;
    private List<String> uniqueAnswers;
    private PopulationController populationController;
    private DataController dataController;
    private GPController gpController;
    private double interval;

    public SDRSController(GPController gpController,DataController dataController, PopulationController populationController) {
        this.populationController = populationController;
        this.dataController = dataController;
        this.gpController = gpController;

        this.uniqueAnswers = dataController.getUniqueAnswers();
        mapOfOutputToClass = new HashMap<>();

        interval = (ConfigController.getStopBondOfSlots()-ConfigController.getStartBondOfSlots())/ ConfigController.getNumberOfSlots();

        for (int counter =0; counter < ConfigController.getNumberOfSlots(); counter++) {
            double key = ConfigController.getStartBondOfSlots()+interval*counter;
            mapOfOutputToClass.put(key,uniqueAnswers.get(counter%uniqueAnswers.size()));
        }
    }

    public void evolveMap(){
        mapOfOutputToClassCounter = new HashMap<>();
        List<Double> RValuesOfTheChromosomes = new ArrayList<>(ConfigController.getPopulationSize());
        for (int counter =0; counter < ConfigController.getPopulationSize(); counter++){
            RValuesOfTheChromosomes.add(counter,0.5 + gpController.fitnessOfChromosomes(counter));
        }

        for (int counter1 =0; counter1 < ConfigController.getNumberOfSlots(); counter1++) {
            List<Double> classCounter = new ArrayList<>(uniqueAnswers.size());
            for (int counter2 =0; counter2 < uniqueAnswers.size(); counter2++) {
                classCounter.add(counter2, (double) 0);
            }
            mapOfOutputToClassCounter.put(ConfigController.getStartBondOfSlots()+interval*counter1, classCounter);
        }

        for (Map<String, Double> dataLine: dataController.getData()){
            int indexOfClass = (int) Math.round(dataLine.get("ans")) - 1;
            for (int counter =0; counter < ConfigController.getPopulationSize(); counter++){
                double calculateSlot =calculateSlot(populationController.evaluateChromosomes(dataLine, counter));
                double value = mapOfOutputToClassCounter.get(calculateSlot).get(indexOfClass);
                value =  value+ RValuesOfTheChromosomes.get(counter);
                mapOfOutputToClassCounter.get(calculateSlot).set(indexOfClass,value);
            }
        }

        for (Double key : mapOfOutputToClassCounter.keySet()){
            double maxValue = mapOfOutputToClassCounter.get(key).stream().max(Comparator.comparingDouble(o -> o)).orElse(0D);
            if (maxValue > 0){
                mapOfOutputToClass.replace(key, String.valueOf((mapOfOutputToClassCounter.get(key).indexOf(maxValue)+1)));
            }else{
                mapOfOutputToClass.replace(key, "-1");

            }
        }

        int counter =0;
        double nextKey =0;
        for (Double key : mapOfOutputToClassCounter.keySet()){
            if (mapOfOutputToClass.get(key).equals("-1")){
                do{
                    counter++;
                    if ((key-counter)>ConfigController.getStartBondOfSlots() && !mapOfOutputToClass.get(key-counter).equals("0")){
                        nextKey =key-counter;
                    }else if ((key+counter)<ConfigController.getStopBondOfSlots() && !mapOfOutputToClass.get(key+counter).equals("0")){
                        nextKey =key+counter;
                    }
                }
                while (mapOfOutputToClass.get(nextKey).equals("-1"));
                mapOfOutputToClass.replace(key, mapOfOutputToClass.get(nextKey));
            }
        }
    }
    
    public String getClass(double input){

        return mapOfOutputToClass.get(calculateSlot(input));
    }

    private double calculateSlot(double input){
        double calculateSlot = Math.round(input);
        if (calculateSlot <= ConfigController.getStartBondOfSlots())
            calculateSlot = ConfigController.getStartBondOfSlots();
        else  if (calculateSlot >= ConfigController.getStopBondOfSlots())
            calculateSlot = ConfigController.getStopBondOfSlots()-1;
        return calculateSlot;
    }
}
