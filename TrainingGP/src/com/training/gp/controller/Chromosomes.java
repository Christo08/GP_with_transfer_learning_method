package com.training.gp.controller;

import java.util.LinkedList;
import java.util.List;

public class Chromosomes {
    private LinkedList<Chromosomes> children;
    private char type;
    private String symbol;

    public Chromosomes(int depth, boolean isFullMethod) {
        type = 'd';
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
                children.add(new Chromosomes(depth, childType,isFullMethod));
            }
        }
    }

    public Chromosomes(int depth, char type, boolean isFullMethod) {
        this.type = type;
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
                children.add(new Chromosomes(depth, childType,isFullMethod));
            }
        }
    }

    @Override
    public String toString() {
        String stringOfChildren = "";
        for (Chromosomes child: children){
            stringOfChildren= stringOfChildren+" ("+child.toString()+")";
        }
        return symbol+stringOfChildren;
    }
}
