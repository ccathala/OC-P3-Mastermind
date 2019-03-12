package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

public class SearchNumber extends Game {

    private int[][] rangeAiAnswer;

    /**
     * Display game heading
     */
    @Override
    public void displayGameHeading() {
        System.out.println("----------[Recherche +/-]----------");
    }

    /**
     * Init new game parameter
     */
    @Override
    public void initGame(){
        super.initGame(); /*Init common parameters for both game*/
        //Init research number specific parameters
        rangeAiAnswer = new int[2][combinationDigitNumber];
        for (int i = 0; i < combinationDigitNumber; i++) {
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
        logger.info("---------------------------------------------------------");
    }

    /**
     * Display instructions at game begins
     * @param gameMode challenger, defender, duel
     */
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

    /**
     * Generate and display AI answer
     * @param result value from compareSolutionAndAttempt method for previous attempt
     * @return AI answer combination
     */
    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        int[] aiAnswerCombination = new int[combinationDigitNumber];
        //Set first answer to 5 for each digit
        if (attemptNumber == 1) {
            for (int i = 0; i < combinationDigitNumber; i++) {
                aiAnswerCombination[i] = 5;
                displayAnswer.append(aiAnswerCombination[i]);
            }
            System.out.println(displayAnswer);
            return aiAnswerCombination;
        }
        //Generate new answer according to previous result
        else {
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
                }else if(result.charAt(i) == '=') {
                    aiAnswerCombination[i]=answerReturn[1][i];
                }
                displayAnswer.append(aiAnswerCombination[i]);
            }
            logger.info("MAJ range Mini : " + rangeMin);
            logger.info("MAJ range Maxi : " + rangeMax);
            System.out.println(displayAnswer);
            return aiAnswerCombination;
        }
    }

    /**
     * Compare solution and answer for both mode
     * @param solution current solution formatted in int[combinationDigitNumber]
     * @param answer generated answer formatted in int[combinationDigitNumber]
     * @return comparison result format : "++=-"
     */
    @Override
    public String compareSolutionAndAnswer(int[] solution,int[] answer) {
        String[] comparatorTable = new String[combinationDigitNumber];
        StringBuilder comparator = new StringBuilder();
        //Compare index[i] solution with index[i] answer, record comparator value
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

    /**
     * Display indications after player or AI proposition
     * @param result from compareSolutionAndAnswer
     */
    @Override
    public void displayIndications(String result){
        //Indications template
        if (currentPlayer.ordinal() == 1) {
            System.out.print("    ");
        }
        System.out.print("      ->Réponse : ");
        //Display indication
        System.out.println(result);
        logger.info("Résultat : " + result);
    }

    /**
     * Import parameters from config.properties
     */
    @Override
    public void importParameterFromConfigProperties() {
        //Target config.properties file
        Properties properties = new Properties();
        try {
            properties.load(Objects.requireNonNull(Game.class.getClassLoader().getResourceAsStream("config.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Record parameters
        combinationDigitNumber = Integer.parseInt(properties.getProperty("researchNumberCombinationDigitNumber"));
        attemptSetting = Integer.parseInt(properties.getProperty("researchNumberAllowedAttempts"));
        devModeFromConfig = Integer.parseInt(properties.getProperty("devMode"));
    }

    /**
     * Return if game is won
     * @param result from compareSolutionAndAttempt format:"++=-"
     * @return true if game is won
     */
    @Override
    public boolean isGameWon(String result) {
        StringBuilder winCondition = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) { /* Generate win condition format:"===..." according to combination digit number*/
            winCondition.append("=");
        }
        return result.equals(winCondition.toString());
    }

    /**
     * Set throwing conditions for PlayerInputError exception for player combination input
     * @param playerInput Player input splitted in String[]
     * @throws PlayerInputError Exception display message when user input error
     */
    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber){
            throw new PlayerInputError();
        }
    }

    /**
     * Display error sentence if incorrect player input
     */
    @Override
    protected void errorSentence(){
        System.out.println("Erreur de saisie - Veuillez saisir une combinaison composée de " + combinationDigitNumber + " chiffres.");
    }

    /**
     * Generate random number from 0 to 9
     * @return random int
     */
    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(10);
    }
}
