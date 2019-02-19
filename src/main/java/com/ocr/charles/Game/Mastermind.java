package com.ocr.charles.Game;


import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.*;


public class Mastermind extends Game {


    private LinkedList<int[]> propositionsList = new LinkedList<>();
    private int combinationNumber; /* Number of possible combinations*/
    private int currentCombination = 1;
    private int quotient = 0;


    @Override
    public void displayGameHeading() {
        System.out.println("----------[Mastermind]----------");
    }

    @Override
    public void initGame(){
        super.initGame();
        logger.info("Base : " + mastermindAllowedNumber);
        logger.info("--------------------Fin resume init----------------------");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        if (attemptNumber == 1) {
            combinationNumber =(int) Math.pow(mastermindAllowedNumber, combinationDigitNumber);
            int[] currentProposition = new int[combinationDigitNumber];
            propositionsList.add(currentProposition);
        } else if (attemptNumber == 2) {
            for (int j = 1; j < combinationNumber; j++) {
                int[] resultTable = new int[combinationDigitNumber];
                for (int i = combinationDigitNumber - 1; i > -1; i--) {
                    int rest;
                    if (i == combinationDigitNumber - 1) {
                        rest = currentCombination % mastermindAllowedNumber;
                        quotient = currentCombination / mastermindAllowedNumber;
                    } else {
                        rest = quotient % mastermindAllowedNumber;
                        quotient = quotient / mastermindAllowedNumber;
                    }
                    resultTable[i] = rest;
                }
                String score = compareSolutionAndAttempt(propositionsList.get(0), resultTable);
                if (score.equals(result)) {
                    propositionsList.add(resultTable);
                }
                currentCombination = currentCombination + 1;
            }
            propositionsList.remove(0);
        } else if (attemptNumber >= 3) {
            for (int i = 1; i < propositionsList.size(); i++) {
                String score = compareSolutionAndAttempt(propositionsList.get(0), propositionsList.get(i));
                if (!score.equals(result)) {
                    propositionsList.remove(i);
                    i = i - 1;
                }
            }
            propositionsList.remove(0);
        }
        for (int i = 0; i < combinationDigitNumber; i++) {
            displayAnswer.append(propositionsList.get(0)[i]);
        }
        System.out.println(displayAnswer);
        return propositionsList.get(0);
    }

    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) throw new PlayerInputError();
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (Integer.parseInt(playerInput[i]) > (mastermindAllowedNumber - 1)) throw new PlayerInputError();
        }
    }

    @Override
    public String compareSolutionAndAttempt(int[] solution, int[] answer) {
        int numberWellplaced = 0;
        int numberInCombination;
        int min = 0;

        for (int i = 0; i < combinationDigitNumber; i++) {
            if (solution[i] == answer[i]) {
                numberWellplaced = numberWellplaced + 1;
            }
        }
        for (int i = 0; i < mastermindAllowedNumber; i++) {
            LinkedList<Integer> occurencies = new LinkedList<>();
            occurencies.add(0, 0);
            occurencies.add(1, 0);
            for (int j = 0; j < combinationDigitNumber; j++) {
                if (solution[j] == i) {
                    occurencies.set(0, occurencies.get(0) + 1);
                }
                if (answer[j] == i) {
                    occurencies.set(1, occurencies.get(1) + 1);
                }
            }
            min = min + Collections.min(occurencies);
        }
        numberInCombination = min - numberWellplaced;
        return String.valueOf((numberWellplaced * 10) + numberInCombination);
    }

    @Override
    public void displayIndications(String result) {
        int numberWellplaced = Integer.parseInt(result) / 10;
        int numberInCombination = Integer.parseInt(result) - numberWellplaced * 10;
        System.out.print("->Réponse : ");
        if (numberWellplaced <= 1) {
            System.out.println(numberWellplaced + " chiffre bien placé.");
        } else {
            System.out.println(numberWellplaced + " chiffres bien placés.");
        }
        if (numberInCombination <= 1) {
            System.out.println("            " + numberInCombination + " chiffre présent, mal placé.");
        } else {
            System.out.println("            " + numberInCombination + " chiffres présents, mal placés.");
        }
        logger.info("Chiffres bien placés : " + numberWellplaced);
        logger.info("Chiffres présents, mal placés : " + numberInCombination);
    }

    @Override
    public boolean isGameWon(String result) {
        return Integer.parseInt(result) == combinationDigitNumber * 10;
    }


    @Override
    public void importParameterFromConfigProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Game.class.getClassLoader().getResourceAsStream("config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        combinationDigitNumber = Integer.parseInt(properties.getProperty("mastermindCombinationDigitNumber"));
        attemptSetting = Integer.parseInt(properties.getProperty("mastermindAllowedAttempts"));
        mastermindAllowedNumber = Integer.parseInt(properties.getProperty("mastermindAllowedNumber"));
        devModeFromConfig = Integer.parseInt(properties.getProperty("devMode"));
    }

    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(mastermindAllowedNumber);
    }










}

