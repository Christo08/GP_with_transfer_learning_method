package com.transfer.learning.gp.data.objects;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
    private char returnType;
    private Map<String, Character> parameterTypes;
    private List<String> parameters;
    private int numberOfParameters;
    private Chromosome function;
    private String name;
    private final Pattern pattern = Pattern.compile("^\\d+");
    private Comparator<String> comparator = (object1, object2) -> {
        Matcher matcher = pattern.matcher(object1);
        Integer number1;
        if (!matcher.find()) {
            return object1.compareTo(object2);
        }
        else {
            Integer number2;
            number1 = Integer.parseInt(matcher.group());
            matcher = pattern.matcher(object2);
            if (!matcher.find()) {
                return object1.compareTo(object2);
            }
            else {
                number2 = Integer.parseInt(matcher.group());
                int comparison = number1.compareTo(number2);
                if (comparison != 0) {
                    return comparison;
                }
                else {
                    return object1.compareTo(object2);
                }
            }
        }
    };

    public Function(String functionString, String name) {
        this.name = name;
        this.returnType = functionString.charAt(1);

        parameterTypes = new HashMap<>();
        parameters = new ArrayList<>();

        numberOfParameters =0;
        for (char character : functionString.toCharArray()) {
            if (character == '#')
                numberOfParameters++;
        }

        for (int counter =0; counter< numberOfParameters; counter++){
            parameters.add("x"+counter);
            functionString = functionString.replaceFirst("#","x"+counter);
        }
        function =new Chromosome(functionString, parameters);

        for (String parameter : parameters) {
            parameterTypes.put(parameter, function.getTypeOfNode(parameter));
        }
    }

    public String getName() {
        return name;
    }

    public int getNumberOfParameters() {
        return numberOfParameters;
    }

    public double evaluateConsequent(Queue<Double> consequentInputs, Queue<Boolean> booleanInputs, Queue<Double> doubleInputs ){
        return function.evaluateConsequent(parameters, consequentInputs, booleanInputs, doubleInputs);
    }

    public double evaluateDouble(Queue<Double> doubleInputs ){
        return function.evaluateDouble(parameters, doubleInputs);
    }

    public boolean evaluateBoolean(Queue<Boolean> booleanInputs, Queue<Double> doubleInputs ){
        return function.evaluateBoolean(parameters, booleanInputs, doubleInputs);
    }

    public char getReturnType() {
        return returnType;
    }

    public List<Character> getParameterTypes() {
        List<Character> types = new ArrayList<>();
        List<String> keys = new ArrayList<>(parameterTypes.keySet());
        Collections.sort(keys,comparator);
        for (String key : keys) {
            types.add(parameterTypes.get(key));
        }
        return types;
    }

    public Function getSelf(){
        return this;
    }
}
