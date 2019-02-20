package com.ocr.charles.Game;
import com.ocr.charles.Exceptions.PlayerInputError;
import com.ocr.charles.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;


public class Mastermind extends Game {

    final Logger logger = LogManager.getLogger(Menu.class);

    private int mastermindAllowedNumber;
    private LinkedList<int[]> propositionsList;
    private int combinationNumber; /* Number of possible combinations*/
    private int currentCombination;
    private int quotient = 0;

    /**
     * Display MastermindLevel heading
     */
    @Override
    public void displayGameHeading() {
        System.out.println("----------[Mastermind]----------");
    }

    /**
     * Initialize MastermindLevel parameters
     */
    @Override
    public void initGame(){
        super.initGame();
        logger.info("Base : " + mastermindAllowedNumber);
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
        System.out.println(" combinaison cachée à " + combinationDigitNumber + " chiffres compris entre 0 et "
                + (mastermindAllowedNumber-1) + ".");
    }

    /**
     * @param result value from compareSolutionAndAttempt method for previous attempt
     * @return answer formatted in int[combinationDigitNumber]
     */
    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        if (attemptNumber == 1) {
            currentCombination=1;
            propositionsList=new LinkedList<>();
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

        //System.out.println(Arrays.toString(propositionsList.get(0)));

        for (int i = 0; i < combinationDigitNumber; i++) {
            displayAnswer.append(propositionsList.get(0)[i]);
        }
        System.out.println(displayAnswer);
        return propositionsList.get(0);
    }

    /**
     *
     * @param playerInput Player input splitted in String[]
     * @throws PlayerInputError Exception display message when incorrect input
     */
    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) {
            throw new PlayerInputError();
        }
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (Integer.parseInt(playerInput[i]) > (mastermindAllowedNumber - 1)){
                throw new PlayerInputError();
            }
        }
    }

    @Override
    protected void errorSentence(){
        System.out.println("Erreur de saisie - Veuillez saisir une combinaison composée de " + combinationDigitNumber +
                " chiffres compris entre 0 et " + (mastermindAllowedNumber-1) + ".");
    }

    /**
     *
     * @param solution current solution formatted in int[combinationDigitNumber]
     * @param answer generated answer formatted in int[combinationDigitNumber]
     * @return
     */
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

    /**
     *
     * @param result from compareSolutionAndAttempt
     */
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

    /**
     *
     * @param result from compareSolutionAndAttempt
     * @return
     */
    @Override
    public boolean isGameWon(String result) {
        return Integer.parseInt(result) == combinationDigitNumber * 10;
    }

    /**
     *
     */
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

    /**
     *
     * @return
     */
    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(mastermindAllowedNumber);
    }










}

