package com.transfer.learning.gp.data.objects;

import com.transfer.learning.gp.controllers.ConfigController;
import com.transfer.learning.gp.controllers.gp.GPController;
import com.transfer.learning.gp.controllers.gp.PopulationController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Chromosome {
    private LinkedList<Chromosome> children;
    private char type;
    private String symbol;
    private int nodeID;
    private int numberOfNodesInTree;
    public static int numberOfNodes;

    public Chromosome(Chromosome chromosomes) {
        type = chromosomes.type;
        symbol = chromosomes.symbol;
        nodeID = chromosomes.nodeID;
        numberOfNodesInTree = chromosomes.numberOfNodesInTree;
        children = new LinkedList<>();
        for (Chromosome child: chromosomes.children) {
            children.add(new Chromosome(child));
        }
    }

    public Chromosome(int depth, boolean isFullMethod) {
        type = 'd';
        numberOfNodes = 1;
        nodeID = numberOfNodes;
        numberOfNodes++;
        if (depth == 1){
            symbol = PopulationController.getRandomSymbolFromTerminalSet();
        }else {
            do {
                if (isFullMethod) {
                    if (this.type == 'd') {
                        symbol = PopulationController.getRandomSymbolFromMathematicalFunctionSet();
                    } else {
                        symbol = PopulationController.getRandomSymbolFromLogicFunctionSet();
                    }
                } else {
                    if (this.type == 'd') {
                        if (GPController.getRandom().nextBoolean()) {
                            symbol = PopulationController.getRandomSymbolFromMathematicalFunctionSet();
                        } else {
                            symbol = PopulationController.getRandomSymbolFromTerminalSet();
                        }
                    } else {
                        symbol = PopulationController.getRandomSymbolFromLogicFunctionSet();
                    }

                }
            }
            while (symbol.equals("IF") && depth - 2 < 1);
        }
        children = new LinkedList<>();
        List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
        depth -=1;
        if (depth != 0 && childTypes != null){
            for (Character childType: childTypes){
                children.add(new Chromosome(depth, childType,isFullMethod));
            }
        }
        numberOfNodesInTree = numberOfNodes;
        numberOfNodes =0;
    }

    public Chromosome(int depth, char type, boolean isFullMethod) {
        this.type = type;
        nodeID = numberOfNodes;
        numberOfNodes++;
        numberOfNodesInTree = numberOfNodes;
        if (depth == 1){
            symbol = PopulationController.getRandomSymbolFromTerminalSet();
        }else {
            do {
                if (isFullMethod) {
                    if (this.type == 'd') {
                        symbol = PopulationController.getRandomSymbolFromMathematicalFunctionSet();
                    } else {
                        symbol = PopulationController.getRandomSymbolFromLogicFunctionSet();
                    }
                } else {
                    if (this.type == 'd') {
                        if (GPController.getRandom().nextBoolean()) {
                            symbol = PopulationController.getRandomSymbolFromMathematicalFunctionSet();
                        } else {
                            symbol = PopulationController.getRandomSymbolFromTerminalSet();
                        }
                    } else {
                        symbol = PopulationController.getRandomSymbolFromLogicFunctionSet();
                    }

                }
            }
            while (symbol.equals("IF") && depth - 2 < 1);
        }
        children = new LinkedList<>();
        List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
        depth -=1;
        if (depth != 0 && childTypes != null){
            for (Character childType: childTypes){
                children.add(new Chromosome(depth, childType,isFullMethod));
            }
        }
    }

    public Chromosome(String chromosome) {
        type = 'd';
        numberOfNodes = 1;
        nodeID = numberOfNodes;
        numberOfNodes++;

        chromosome = chromosome.substring(1,chromosome.length()-1);
        symbol = chromosome.substring(0, chromosome.indexOf(" ("));
        chromosome = chromosome.substring(chromosome.indexOf(" (")).trim();

        int numberOfBrackets =0;
        int startOfChild =0;
        int endOfChild =0;
        List<String> childrenStrings = new ArrayList<>();
        for (char character: chromosome.toCharArray()) {
            if (character == '('){
                numberOfBrackets++;
            } else if (character == ')'){
                numberOfBrackets--;
            }
            endOfChild++;
            if (numberOfBrackets == 0){
                childrenStrings.add(chromosome.substring(startOfChild, endOfChild));
                startOfChild = endOfChild;
            }
        }
        childrenStrings.removeIf(childrenString -> childrenString.isEmpty() || childrenString.equals(" "));
        children = new LinkedList<>();
        List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
        for (int counter =0; counter < childrenStrings.size();counter++) {
            children.add(new Chromosome(childrenStrings.get(counter),childTypes.get(counter)));
        }
        numberOfNodesInTree = numberOfNodes;
    }

    public Chromosome(String chromosome, char type) {
        this.type = type;
        nodeID = numberOfNodes;
        numberOfNodes++;

        chromosome = chromosome.substring(2,chromosome.length()-2);
        if (chromosome.contains("("))
            symbol = chromosome.substring(0, chromosome.indexOf(" ("));
        else {
            symbol = chromosome;
            numberOfNodesInTree = numberOfNodes;
            children = new LinkedList<>();
            return;
        }
        chromosome = chromosome.substring(chromosome.indexOf(" (")).trim();

        int numberOfBrackets =0;
        int startOfChild =0;
        int endOfChild =0;
        List<String> childrenStrings = new ArrayList<>();
        for (char character: chromosome.toCharArray()) {
            if (character == '('){
                numberOfBrackets++;
            } else if (character == ')'){
                numberOfBrackets--;
            }
            endOfChild++;
            if (numberOfBrackets == 0){
                childrenStrings.add(chromosome.substring(startOfChild, endOfChild));
                startOfChild = endOfChild;
            }
        }
        childrenStrings.removeIf(childrenString -> childrenString.isEmpty() || childrenString.equals(" "));
        children = new LinkedList<>();
        List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
        for (int counter =0; counter < childrenStrings.size();counter++) {
            children.add(new Chromosome(childrenStrings.get(counter),childTypes.get(counter)));
        }
        numberOfNodesInTree = numberOfNodes;
    }

    public char getType() {
        return type;
    }

    public char getTypeOfNode(int ID){
        return getSubTree(ID).getType();
    }

    public int getNodeID() {
        return nodeID;
    }

    public double evaluate() {
        double output;
        if (symbol.equals("IF")) {
            if (children.get(0).evaluateBoolean()) {
                output = children.get(1).evaluate();
            }else{
                output = children.get(2).evaluate();
            }
        }
        else if (symbol.equals("+")){
            output = children.get(0).evaluate() + children.get(1).evaluate();
        }
        else if (symbol.equals("-")){
            output = children.get(0).evaluate() - children.get(1).evaluate();
        }
        else if (symbol.equals("*")){
            output = children.get(0).evaluate() * children.get(1).evaluate();
        }
        else if (symbol.equals("/")){
            double childTwoValue = children.get(1).evaluate();
            if (childTwoValue == 0)
                output = 0;
            else
                output = children.get(0).evaluate() / childTwoValue;
        }
        else {
            output = PopulationController.getValueOf(symbol);
        }
        return output;
    }

    private boolean evaluateBoolean() {
        double outputOfChildOne = children.get(0).evaluate();
        double outputOfChildTwo = children.get(1).evaluate();
        if (symbol.contains(("<=")))
            return outputOfChildOne <= outputOfChildTwo;
        else if (symbol.contains((">=")))
            return outputOfChildOne >= outputOfChildTwo;
        else if (symbol.contains(("==")))
            return outputOfChildOne <= outputOfChildTwo;
        else
            return outputOfChildOne > outputOfChildTwo &&
                   outputOfChildOne < children.get(2).evaluate();
    }

    public int getRandomSubTreeID() {
        if (numberOfNodesInTree ==1)
            return 1;
        else{
            int id = GPController.getRandom().nextInt(numberOfNodesInTree-1)+1;
            while (id> ConfigController.getMaxDepthOfCrossover()){
                id = GPController.getRandom().nextInt(numberOfNodesInTree-1)+1;
            }
            return id;
        }
    }

    public Chromosome getSubTree(int ID) {
        if (nodeID == ID){
            return this;
        }
        for (Chromosome child: children) {
            Chromosome node = child.getSubTree(ID);
            if (node != null){
                return node;
            }
        }
        return null;
    }

    public boolean replaceSubTree(int id, Chromosome chromosomes) {
        if (nodeID == 1){
            numberOfNodes =0;
        }
        for (Chromosome child: children) {
            if (child.getNodeID() == id){
                children.set(children.indexOf(child),chromosomes);
                return true;
            }
        }
        for (Chromosome child: children) {
            if (child.replaceSubTree(id,chromosomes)){
                return true;
            }
        }
        return false;
    }

    public void renumberTheNodes(boolean fristLevel) {
        if (fristLevel)
            numberOfNodes = 0;
        numberOfNodes++;
        nodeID = numberOfNodes;
        for (Chromosome child: children){
            child.renumberTheNodes(false);
        }

        numberOfNodesInTree = numberOfNodes;
    }

    @Override
    public String toString() {
        String stringOfChildren = "";
        for (Chromosome child: children){
            stringOfChildren= stringOfChildren+" ("+child.toString()+")";
        }
        return "{"+symbol+stringOfChildren+"}";
    }

    @Override
    public int hashCode() {
        int result = children.hashCode();
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + nodeID;
        return result;
    }
}
