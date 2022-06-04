package com.training.gp.controller;


import java.util.*;

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

    public static String getRandomSymbolFromMathematicalFunctionSet() {
        List<String> pKeys = new LinkedList<>(mathematicalFunctionSet.keySet());
        return pKeys.get(GPController.getRandom().nextInt(mathematicalFunctionSet.size()-1));
    }

    public static String getRandomSymbolFromLogicFunctionSet() {
        List<String> pKeys = new LinkedList<>(logicFunctionSet.keySet());
        return pKeys.get(GPController.getRandom().nextInt(logicFunctionSet.size()-1));
    }

    public static String getRandomSymbolFromTerminalSet() {
        return terminalSet.get(GPController.getRandom().nextInt(terminalSet.size()-1));
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
}
