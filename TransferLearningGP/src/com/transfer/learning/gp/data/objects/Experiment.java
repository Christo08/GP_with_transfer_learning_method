package com.transfer.learning.gp.data.objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "book")
@XmlType(propOrder = {
        "author",
        "name",
        "publisher",
        "isbn"
})
public class Experiment {
    private long seed;
    private double startBondOfSlots;
    private double stopBondOfSlots;
    private double minAccuracy;
    private int maxDepthOfStartingChromosomes;
    private int populationSize;
    private int numberOfSlots;
    private int tournamentSize;
    private int reproductionSize;
    private int crossoverSize;
    private int mutationSize;
    private int numberOfGenerationsBeforeEvolveMap;


}
