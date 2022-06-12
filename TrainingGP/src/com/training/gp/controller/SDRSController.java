package com.training.gp.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SDRSController {
    private Map<Double, String> range;
    private List<List<Double>> array;
    private List<String> uniqueAnswers;
    private PopulationController populationController;
    private DataController dataController;
    private GPController gpController;
    private double interval;
    private NumberFormat formatter = new DecimalFormat("#0.0000");
    private List<Double> keys;
    private ExecutorService threadExecutorService;

    public SDRSController(GPController gpController,DataController dataController, PopulationController populationController) {
        this.populationController = populationController;
        this.dataController = dataController;
        this.gpController = gpController;

        this.uniqueAnswers = dataController.getUniqueAnswers();

        range = new HashMap<>();
        keys = new ArrayList<>(range.keySet());
        threadExecutorService = Executors.newFixedThreadPool( ConfigController.getNumberOfBatch());

        interval = (ConfigController.getStopBondOfSlots()-ConfigController.getStartBondOfSlots())/ ConfigController.getNumberOfSlots();

        for (int counter =0; counter < ConfigController.getNumberOfSlots(); counter++) {
            double key = ConfigController.getStartBondOfSlots()+interval*counter;
            range.put(key,uniqueAnswers.get(counter%uniqueAnswers.size()));
        }
    }

    public void evolveMap(){
        array = new ArrayList<>();
        for (int counter = 0; counter< range.keySet().size(); counter++){
            List<Double> classes = new ArrayList<>();
            for (int counter1 =0; counter1 < uniqueAnswers.size(); counter1++){
                classes.add(0D);
            }
            array.add(classes);
        }

        AtomicInteger totalCounter =new AtomicInteger(0);
        AtomicInteger numberOfThreadsFinished = new AtomicInteger(0);
        for (int counter1 =0; counter1 < ConfigController.getNumberOfBatch(); counter1++){
            final int batchIndex = counter1;
            threadExecutorService.submit(()->{
                for (Map<String, Double> datum : dataController.getBatchOfData(batchIndex)) {
                    for (int counter2 =0; counter2< ConfigController.getPopulationSize(); counter2++){
                        double output = populationController.evaluateChromosomes(datum, counter2);
                        int slot = (int) Math.round(output);
                        if (slot <= ConfigController.getStartBondOfSlots())
                            slot = (int) ConfigController.getStartBondOfSlots();
                        else  if (slot >= ConfigController.getStopBondOfSlots())
                            slot = (int) (ConfigController.getStopBondOfSlots()-1);
                        int slotIndex =ConfigController.getNumberOfSlots()/2 + slot;
                        int classIndex = (int) Math.round(datum.get("ans"));
                        double value = array.get(slotIndex).get(classIndex) + gpController.fitnessOfChromosomes(counter2);
                        array.get(slotIndex).set(classIndex,value );
                        totalCounter.getAndIncrement();
                        printProgress(totalCounter.get(), (dataController.getDataSet().size() * ConfigController.getPopulationSize()));
                    }
                }
                numberOfThreadsFinished.getAndIncrement();
            });
        }

        while (numberOfThreadsFinished.get() <4){}

        for (Double dKey : keys) {
            double maxValue = array.get(keys.indexOf(dKey)).stream().max(Comparator.naturalOrder()).orElse(0D);
            int indexOfMaxValue = array.get(keys.indexOf(dKey)).indexOf(maxValue);
            if (maxValue > 0)
                range.replace(dKey, String.valueOf(indexOfMaxValue));
            else
                range.replace(dKey, "-1");
        }

        double prevKey = 0;
        double nextKey = 0;
        int counter =0;
        String newClass ="-1";
        for (Double dKey : keys) {
            while (true){
                prevKey = dKey - interval*counter;
                if (prevKey > ConfigController.getStartBondOfSlots())
                    newClass =range.get(prevKey);
                if (!newClass.equals("-1"))
                    break;
                nextKey = dKey + interval*counter;
                if (nextKey > ConfigController.getStopBondOfSlots())
                    newClass =range.get(nextKey);
                if (!newClass.equals("-1"))
                    break;
                counter++;
            }
            range.replace(dKey, newClass);
        }

        System.out.println(1);
    }
    
    public String getClass(double input){

        return range.get(calculateSlot(input));
    }

    private double calculateSlot(double input){
        double calculateSlot = Math.round(input);
        if (calculateSlot <= ConfigController.getStartBondOfSlots())
            calculateSlot = ConfigController.getStartBondOfSlots();
        else  if (calculateSlot >= ConfigController.getStopBondOfSlots())
            calculateSlot = ConfigController.getStopBondOfSlots()-1;
        return calculateSlot;
    }

    private void printProgress(double current, double total) {

        StringBuilder string = new StringBuilder(140);
        double percent = (current/total)* 100;
        string.append('\r')
                .append(formatter.format(percent))
                .append("% of evolve map");

        System.out.print(string);
    }
}
