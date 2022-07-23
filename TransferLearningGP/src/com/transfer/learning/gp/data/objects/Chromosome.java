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

    public Chromosome() {
        children = new LinkedList<>();
        symbol = "#";
    }

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

    public Chromosome(String chromosomeString) {
        chromosomeString = chromosomeString.substring(1,chromosomeString.length()-1);
        symbol = PopulationController.getValidSymbol(chromosomeString.substring(0, chromosomeString.indexOf(" (")));
        chromosomeString = chromosomeString.substring(chromosomeString.indexOf(" (")).trim();

        type = 'c';
        numberOfNodes = 1;
        nodeID = numberOfNodes;
        numberOfNodes++;

        int numberOfBrackets =0;
        int startOfChild =0;
        int endOfChild =0;
        List<String> childrenStrings = new ArrayList<>();
        for (char character: chromosomeString.toCharArray()) {
            if (character == '('){
                numberOfBrackets++;
            } else if (character == ')'){
                numberOfBrackets--;
            }
            endOfChild++;
            if (numberOfBrackets == 0){
                childrenStrings.add(chromosomeString.substring(startOfChild, endOfChild));
                startOfChild = endOfChild;
            }
        }
        childrenStrings.removeIf(childrenString -> childrenString.isEmpty() || childrenString.equals(" "));
        children = new LinkedList<>();
        if (childrenStrings.size() > 0){
            List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
            for (int counter =0; counter < childrenStrings.size();counter++) {
                children.add(new Chromosome(childrenStrings.get(counter),childTypes.get(counter)));
            }
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

    public Chromosome(int depth, boolean isFullMethod) {
        type = 'c';
        numberOfNodes = 1;
        nodeID = numberOfNodes;
        numberOfNodes++;
        if (depth == 1){
            symbol = PopulationController.getRandomAttribute();
        }else{
            if (isFullMethod) {
                if (depth - 2 == 0){
                    symbol = PopulationController.getRandomAttribute();
                }else {
                    symbol = "IF";
                }
            } else {
                symbol = PopulationController.getRandomSymbolFromConsequentSet();
            }
        }
        children = new LinkedList<>();
        List<Character> childTypes = PopulationController.getChildrenTypes(symbol);
        depth -=1;
        if (depth != 0 && childTypes != null){
            for (Character childType: childTypes){
                children.add(new Chromosome(depth, childType,isFullMethod));
            }
        }
        numberOfNodesInTree = numberOfNodes - nodeID;
        numberOfNodes =0;
    }

    public Chromosome(int depth, char type, boolean isFullMethod) {
        this.type = type;
        nodeID = numberOfNodes;
        numberOfNodes++;
        if (depth == 1){
            if (this.type == 'c'){
                symbol = PopulationController.getRandomAttribute();
            }else{
                symbol =PopulationController.getRandomSymbolFromMathematicalTerminalSet();
            }
        }else{
            do {
                if (isFullMethod) {
                    if (this.type == 'd') {
                        symbol = PopulationController.getRandomSymbolFromMathematicalFunctionSet();
                    } else if (this.type == 'b') {
                        symbol = PopulationController.getRandomSymbolFromBooleanFunctionSet();
                    } else if (depth - 2 ==0){
                        symbol = PopulationController.getRandomAttribute();
                    }else {
                        symbol = "IF";
                    }
                } else {
                    if (this.type == 'd'){
                        symbol = PopulationController.getRandomSymbolFromMathematicalSet();
                    } else if (this.type == 'b') {
                        symbol = PopulationController.getRandomSymbolFromBooleanSet();
                    } else{
                        symbol = PopulationController.getRandomSymbolFromConsequentSet();
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
        numberOfNodesInTree = numberOfNodes - nodeID;
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
        if (symbol.equals("IF")){
            if (children.get(0).evaluateBoolean()){
                return children.get(1).evaluate();
            }else{
                return children.get(2).evaluate();
            }
        }else{
            return Double.parseDouble(symbol);
        }
    }

    private boolean evaluateBoolean() {
        double arg1 = children.get(0).evaluateDouble();
        double arg2 = children.get(1).evaluateDouble();
        if (symbol.equals(">=")){
            return arg1 >= arg2;
        } else if (symbol.equals("<=")){
            return arg1 <= arg2;
        } else if (symbol.equals("==")){
            return arg1 == arg2;
        } else {
            return arg1 > arg2 && arg1 < children.get(2).evaluateDouble();
        }
    }

    private double evaluateDouble() {
        if (symbol == "+"){
            return children.get(0).evaluateDouble() + children.get(1).evaluateDouble();
        } else if (symbol == "-"){
            return children.get(0).evaluateDouble() - children.get(1).evaluateDouble();
        } else if (symbol == "*"){
            return children.get(0).evaluateDouble() * children.get(1).evaluateDouble();
        } else if (symbol == "/"){
            double arg2 = children.get(1).evaluateDouble();
            if (arg2 == 0)
                return 0;
            return children.get(0).evaluateDouble() / arg2;
        } else if(!isNumeric(symbol)){
            return PopulationController.getValueOf(symbol);
        } else {
            return Double.parseDouble(symbol);
        }
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
        return "{"+nodeID+" "+symbol+stringOfChildren+"}";
    }

    @Override
    public int hashCode() {
        int result = children.hashCode();
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + nodeID;
        return result;
    }

    public List<Chromosome> getNodes() {
        List<Chromosome> chromosomes = new LinkedList<>();
        chromosomes.add(this);
        for (Chromosome child : children) {
            chromosomes.addAll(child.getNodes());
        }
        return chromosomes;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setType(char type) {
        this.type = type;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Chromosome getChild(int counter) {
        return children.get(counter);
    }

    public void addChild(Chromosome commonSubTree) {
        children.add(commonSubTree);
    }

    public Chromosome getCommonSubTree(Chromosome nodeY) {
        Chromosome newTree;
        if (!this.symbol.equals(nodeY.symbol) || children.size() ==0){
            newTree = new Chromosome();
            if (!PopulationController.terminalSetContains(this.symbol) && this.symbol.equals(nodeY.getSymbol()))
                newTree.setSymbol(this.symbol);
        }else{
            newTree = new Chromosome();
            newTree.setSymbol(this.getSymbol());
            newTree.setType(this.getType());
            for (int counter =0; counter< children.size(); counter++) {
                newTree.addChild(children.get(counter).getCommonSubTree(nodeY.getChild(counter)));
            }
        }
        return newTree;
    }

    public int getMaxDepth() {
        int maxDepth =0;
        for (Chromosome child : children) {
            int tempDepth = child.getMaxDepth();
            if (maxDepth < tempDepth){
                maxDepth = tempDepth;
            }
        }
        return maxDepth+1;
    }

    public Chromosome getSubTreeOfDepth(int depth) {
        Chromosome newTree;
        if (children.size() ==0 || depth == 1) {
            newTree = new Chromosome();
            if (!PopulationController.terminalSetContains(this.symbol))
                newTree.setSymbol(this.symbol);
        }else{
            newTree = new Chromosome();
            newTree.setSymbol(this.getSymbol());
            newTree.setType(this.getType());
            for (int counter =0; counter< children.size(); counter++) {
                newTree.addChild(children.get(counter).getSubTreeOfDepth(depth-1));
            }
        }
        return newTree;
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
