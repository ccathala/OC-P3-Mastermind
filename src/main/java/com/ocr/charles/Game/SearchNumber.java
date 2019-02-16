package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class SearchNumber extends Game {

    @Override
    public void displayGameHeading() {
        System.out.println("----------[Recherche +/-]----------");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        int[] aiAnswerCombination = new int[combinationDigitNumber];
        if (attemptNumber == 1) {
            for (int i = 0; i < combinationDigitNumber; i++) {
                aiAnswerCombination[i] = 5;
                displayAnswer.append(aiAnswerCombination[i]);
            }
            System.out.println(displayAnswer);
            return aiAnswerCombination;
        } else {
            StringBuilder rangeMin = new StringBuilder();
            StringBuilder rangeMax = new StringBuilder();
            for (int i = 0; i < combinationDigitNumber; i++) {

                if (result.charAt(i) == '+') {
                    rangeAiAnswer[0][i] = answerReturn[1][i] + 1;
                    rangeMin.append(rangeAiAnswer[0][i]).append(" ");
                    rangeMax.append(rangeAiAnswer[1][i]).append(" ");
                    aiAnswerCombination[i] = rangeAiAnswer[0][i] + ((rangeAiAnswer[1][i] - rangeAiAnswer[0][i]) / 2);
                } else if (result.charAt(i) == '-') {
                    rangeAiAnswer[1][i] = answerReturn[1][i] - 1;
                    rangeMin.append(rangeAiAnswer[0][i]).append(" ");
                    rangeMax.append(rangeAiAnswer[1][i]).append(" ");
                    aiAnswerCombination[i] = rangeAiAnswer[1][i] - ((rangeAiAnswer[1][i] - rangeAiAnswer[0][i]) / 2);
                }
                displayAnswer.append(aiAnswerCombination[i]);
            }
            logger.info("MAJ range Mini : " + rangeMin);
            logger.info("MAJ range Maxi : " + rangeMax);
            System.out.println(displayAnswer);
            return aiAnswerCombination;


        }
    }

    @Override
    public String compareSolutionAndAttempt(int[] solution,int[] answer) {
        String[] comparatorTable = new String[combinationDigitNumber];
        StringBuilder comparator = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (answer[i] == solution[i]) {
                comparatorTable[i] = "=";
            } else if (answer[i] < solution[i]) {
                comparatorTable[i] = "+";
            } else {
                comparatorTable[i] = "-";
            }
            comparator.append(comparatorTable[i]);
        }
        String comparaisonReturn = comparator.toString();
        return comparaisonReturn;
    }

    @Override
    public void displayIndications(String result){
        if (currentPlayer.ordinal() == 1) {
            System.out.print("    ");
        }
        System.out.print("      ->Réponse : ");
        System.out.println(result);
        logger.info("Résultat : " + result);
    }

    @Override
    public boolean isGameWon(String result) {
        StringBuilder winCondition = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) { /* Generate win condition string*/
            winCondition.append("=");
        }
        if (result.equals(winCondition.toString())){
           return true;
        }else{
            return false;
        }
    }

    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) throw new PlayerInputError();
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
        importedValues[0] = Integer.parseInt(properties.getProperty("researchNumberCombinationDigitNumber"));
        importedValues[1] = Integer.parseInt(properties.getProperty("researchNumberAllowedAttempts"));
        importedValues[2] = Integer.parseInt(properties.getProperty("devMode"));
        return importedValues;
    }

    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
