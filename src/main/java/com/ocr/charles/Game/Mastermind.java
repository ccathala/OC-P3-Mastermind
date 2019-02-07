package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.*;

import static java.lang.Integer.min;

public class Mastermind extends Game {

    @Override
    public void displayGameHeading() {
        System.out.println("----------[Mastermind]----------");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        return new int[0];
    }

    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) throw new PlayerInputError();
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (Integer.parseInt(playerInput[i]) > (mastermindAllowedNumber - 1)) throw new PlayerInputError();
        }
    }

    @Override
    public String[] compareAttemptAndSolution(int[][] solutionAndAttempt) {
        String[] comparatorTable = new String[combinationDigitNumber];
        String[] comparaisonReturn = new String[3];
        StringBuilder comparator = new StringBuilder();
        int numberWellplaced = 0;
        int numberInCombination = 0;
        int min = 0;
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (solutionAndAttempt[1][i] == solutionAndAttempt[0][i]) {
                comparatorTable[i] = "=";
            } else if (solutionAndAttempt[1][i] < solutionAndAttempt[0][i]) {
                comparatorTable[i] = "+";
            } else {
                comparatorTable[i] = "-";
            }
            comparator.append(comparatorTable[i]);
        }
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (solutionAndAttempt[0][i] == solutionAndAttempt[1][i]) {
                numberWellplaced = numberWellplaced + 1;
            }
        }
        for (int i = 0; i < mastermindAllowedNumber; i++) {
            LinkedList<Integer> occurencies = new LinkedList<>();
            occurencies.add(0, 0);
            occurencies.add(1, 0);
            for (int j = 0; j < combinationDigitNumber; j++) {
                if (solutionAndAttempt[0][j] == i) {
                    occurencies.set(0, occurencies.get(0) + 1);
                }
                if (solutionAndAttempt[1][j] == i) {
                    occurencies.set(1, occurencies.get(1) + 1);
                }
            }
            min = min + Collections.min(occurencies);
        }

       /* for (int i = 0; i < combinationDigitNumber; i++) {
            if (solutionAndAttempt[1][i] == solutionAndAttempt[0][i]) {
                comparatorTable[i] = "=";
            } else if (solutionAndAttempt[1][i] < solutionAndAttempt[0][i]) {
                comparatorTable[i] = "+";
            } else {
                comparatorTable[i] = "-";
            }
            comparator.append(comparatorTable[i]);
            for(int j=0;j<mastermindAllowedNumber;j++){
                LinkedList<Integer> occurencies = new LinkedList<>();
                occurencies.add(0,0);
                occurencies.add(1,0);
                if(solutionAndAttempt[0][i]==solutionAndAttempt[1][i] && i==j){
                    numberWellplaced = numberWellplaced+1;
                }
                if (solutionAndAttempt[0][i]==j){
                   occurencies.set(0,1);
                }
                if (solutionAndAttempt[1][i]==j){
                    occurencies.set(1,1);
                }
                min = min + Collections.min(occurencies);
            }
        }*/
        numberInCombination = min - numberWellplaced;
        comparaisonReturn[0] = comparator.toString();
        comparaisonReturn[1] = String.valueOf(numberWellplaced);
        comparaisonReturn[2] = String.valueOf(numberInCombination);
        System.out.print("->Réponse : ");
        if (numberWellplaced <= 1){
            System.out.println(numberWellplaced + " chiffre bien placé.");
        }else{
            System.out.println(numberWellplaced + " chiffres bien placés.");
        }
        if(numberInCombination<=1){
            System.out.println("            " + numberInCombination + " chiffre présent, mal placé.");
        }else{
            System.out.println("            " + numberInCombination + " chiffres présents, mal placés.");
        }
        logger.info("Chiffres bien placés : " + numberWellplaced );
        logger.info("Chiffres présents, mal placés : " + numberInCombination );
        logger.info("Résultat : " + comparator);
        return comparaisonReturn;
    }


    @Override
    protected int[] importParameterFromConfigProperties() {
        Properties properties = new Properties();
        int[] importedValues = new int[4];
        try {
            properties.load(Game.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        importedValues[0] = Integer.parseInt(properties.getProperty("mastermindCombinationDigitNumber"));
        importedValues[1] = Integer.parseInt(properties.getProperty("mastermindAllowedAttempts"));
        importedValues[3] = Integer.parseInt(properties.getProperty("mastermindAllowedNumber"));
        importedValues[2] = Integer.parseInt(properties.getProperty("devMode"));
        return importedValues;
    }

    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(mastermindAllowedNumber);
    }
}

