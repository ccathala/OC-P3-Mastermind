package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

public class SearchNumber extends Game {

    private int[][] rangeAiAnswer;

    @Override
    public void displayGameHeading() {
        System.out.println("----------[Recherche +/-]----------");
    }

    @Override
    public void initGame(){
        super.initGame();
        rangeAiAnswer = new int[2][combinationDigitNumber];
        for (int i = 0; i < combinationDigitNumber; i++) { /* init range for defender sequence */
            rangeAiAnswer[0][i] = 0;
            rangeAiAnswer[1][i] = 9;
        }
        StringBuilder rangeMin= new StringBuilder();
        StringBuilder rangeMax= new StringBuilder();
        for(int i=0;i<combinationDigitNumber;i++){
            rangeMin.append(rangeAiAnswer[0][i]).append(" ");
            rangeMax.append(rangeAiAnswer[1][i]).append(" ");
        }
        logger.info("Range min: " + rangeMin);
        logger.info("Range max: " + rangeMax);
        logger.info("--------------------Fin resume init----------------------");
    }

    @Override
    protected void displayInstruction(String gameMode){
        switch (gameMode) {
            case "challenger":
                System.out.print("Trouve la");
                break;
            case "defender":
                System.out.print("Choisi une");
                break;
            case "duel":
                System.out.print("Joueur, choisi une");
                break;
        }
        System.out.println(" combinaison cachée à " + combinationDigitNumber + " chiffres.");
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
        return comparator.toString();
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
        return result.equals(winCondition.toString());
    }

    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber){
            throw new PlayerInputError();
        }
    }

    @Override
    protected void errorSentence(){
        System.out.println("Erreur de saisie - Veuillez saisir une combinaison composée de " + combinationDigitNumber + " chiffres.");
    }



    @Override
    public void importParameterFromConfigProperties() {
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Game.class.getClassLoader().getResourceAsStream("config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        combinationDigitNumber = Integer.parseInt(properties.getProperty("researchNumberCombinationDigitNumber"));
        attemptSetting = Integer.parseInt(properties.getProperty("researchNumberAllowedAttempts"));
        devModeFromConfig = Integer.parseInt(properties.getProperty("devMode"));
    }

    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
