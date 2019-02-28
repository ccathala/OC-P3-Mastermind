package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.*;


public class Mastermind extends Game {


    private int mastermindAllowedNumber;
    private LinkedList<int[]> propositionsList;
    private LinkedList<Integer> scoreList;
    private int[][] scoreTable;
    private int indexSubmitedProp;
    private int[] submitedProposition = new int[0];
    private int combinationNumber; /* Number of possible combinations*/
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
    public void initGame() {
        super.initGame();

        logger.info("Base : " + mastermindAllowedNumber);
        logger.info("--------------------Fin resume init----------------------");
    }

    /**
     * Display instructions at game begins
     * @param gameMode challenger, defender, duel
     */
    @Override
    protected void displayInstruction(String gameMode) {
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
                + (mastermindAllowedNumber - 1) + ".");
    }

    /**
     * Set throwing conditions for PlayerInputError exception for player combination input
     * @param playerInput Player input splitted in String[]
     * @throws PlayerInputError Exception display message when incorrect input
     */
    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        //Throw exception if incorrect length input
        if (playerInput.length != combinationDigitNumber) {
            throw new PlayerInputError();
        }
        //Throw exception if digit out of base set
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (Integer.parseInt(playerInput[i]) > (mastermindAllowedNumber - 1)) {
                throw new PlayerInputError();
            }
        }
    }

    /**
     * Display error sentence if incorrect player input
     */
    @Override
    protected void errorSentence() {
        System.out.println("Erreur de saisie - Veuillez saisir une combinaison composée de " + combinationDigitNumber +
                " chiffres compris entre 0 et " + (mastermindAllowedNumber - 1) + ".");
    }

    /**
     * Generate and display AI answer
     * @param result value from compareSolutionAndAttempt method for previous attempt
     * @return AI answer combination
     */
    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        //First turn
        if (attemptNumber == 1) {
            combinationNumber = (int) Math.pow(mastermindAllowedNumber, combinationDigitNumber); /*Set amount of propositions*/
            submitedProposition = aiChooseRandomCombination(); /*Choose random combination for turn 1*/
            generateScoresList();
        }
        if (attemptNumber >= 2) {
            generatePropositionListAccordingPreviousScoreAttempt(result,submitedProposition);
            generateScoreTable();
            indexSubmitedProp = choseMinWeightProposition(scoreCalculation()); /*Record score for each proposition,
             calculate minimal weight proposition and record index of the first minimal weight proposition*/
            submitedProposition= propositionsList.get(indexSubmitedProp);
        }
        //Display answer
        for (int i = 0; i < combinationDigitNumber; i++) {
            displayAnswer.append(submitedProposition[i]);
        }
        System.out.println(displayAnswer);
        return submitedProposition;
    }

    /**
     * Generate score list, list all possible result of solution and answer comparison
     * Result example: [1,2]:
     * Well placed numbers = 1
     * Numbers in combination but not well placed = 2
     */
    private void generateScoresList() {
        scoreList = new LinkedList<>();
        for (int i = 0; i <= combinationDigitNumber; i++) {
            Integer dizaine = 10 * i;
            if (i <= combinationDigitNumber - 2) { /* -2 cause two last score are always: (mastermindAllowedNumber-1)*10/mastermindAllowedNumber*10*/
                for (int j = 0; j <= combinationDigitNumber; j++) {
                    if (j < i) {
                        j = i;
                    }
                    scoreList.add(dizaine);
                    dizaine = dizaine + 1;
                }
            } else {
                scoreList.add(dizaine);
            }
        }
    }

    /**
     * Generate score table
     * Column number = score list size + 1
     * Row number = proposition list size
     */
    private void generateScoreTable(){
        scoreTable = new int[propositionsList.size()][(scoreList.size() + 1)];/* +1 column to record weight value*/
    }

    /**
     * Calculate and record comparison result of all possible solution/answer combination
     * Calculate maximal weight for each proposition
     * @return minimal weight
     */
    private int scoreCalculation() {
        int[] solution;
        int[] answer;
        int score;
        int minWeight = 0;
        for (int i = 0; i < propositionsList.size(); i++) {
            solution = propositionsList.get(i);/*Choose reference proposition*/
            for (int j = 0; j < propositionsList.size(); j++) {
                answer = propositionsList.get(j);/*Choose candidate proposition*/
                // Score calculation
                score = Integer.parseInt(compareSolutionAndAnswer(solution, answer));
                //Record score in score table
                int k = 0;
                while (score != scoreList.get(k)) {
                    k++;
                }
                scoreTable[i][k]++;
            }
            // Search maximal weight of reference proposition
            int weight = 0;
            for (int j = 0; j < scoreList.size(); j++) {
                if (weight < scoreTable[i][j]) {
                    weight = scoreTable[i][j];
                }
            }
            // Record reference proposition maximal weigth in score table
            scoreTable[i][scoreList.size()] = weight;
            // Search weight minimal value in score table
            if (minWeight == 0) {
                minWeight = scoreTable[i][scoreList.size()];
            } else if (minWeight > scoreTable[i][scoreList.size()]) {
                minWeight = scoreTable[i][scoreList.size()];
            }
        }
        return minWeight;
    }

    /**
     * Search in score table proposition with minimal weight value
     * @param minWeight minimal weight proposition
     * @return return index of a minimal weight proposition
     */
    private int choseMinWeightProposition(int minWeight) {
        int i = 0;
        while (minWeight != scoreTable[i][scoreList.size()]) {
            i++;
        }
        return i;/*i=index of next answer*/
    }

    /**
     * Generate proposition list according to result of attempt 1
     * @param result value from compareSolutionAndAnswer from attempt 1
     * @param Attempt1Answer attempt 1 submitted solution
     */
    private void generatePropositionListAccordingScoreAttempt1(String result , int[]Attempt1Answer) {
        // Generate proposition list according to score turn 1
        propositionsList=new LinkedList<>();
        //Convert value j in base value
        for (int j = 0; j < combinationNumber; j++) {
            int[] resultTable = new int[combinationDigitNumber];
            for (int i = combinationDigitNumber - 1; i > -1; i--) {
                int rest;
                if (i == combinationDigitNumber - 1) {
                    rest = j % mastermindAllowedNumber;
                    quotient = j / mastermindAllowedNumber;
                } else {
                    rest = quotient % mastermindAllowedNumber;
                    quotient = quotient / mastermindAllowedNumber;
                }
                resultTable[i] = rest;
            }
            //Compare converted value of j with anwser turn 1, if score=result add j as proposition in list
            String score = compareSolutionAndAnswer(Attempt1Answer, resultTable);
            if (score.equals(result)) {
                propositionsList.add(resultTable);
            }
        }
    }

    /**
     * Generate new proposition list according to comparaison result of previous turn
     * @param result result value from compareSolutionAndAnswer from attempt 1
     * @param previousAttemptAnswer previous attempt submitted solution
     */
    private void generatePropositionListAccordingPreviousScoreAttempt(String result, int[] previousAttemptAnswer) {
        if(attemptNumber==2){
            generatePropositionListAccordingScoreAttempt1(result, previousAttemptAnswer);
        }
        if (attemptNumber > 2) {
            LinkedList<int[]> bufferList = new LinkedList<>();
            for (int i = 0; i < propositionsList.size(); i++) {
                //Compare each proposition in list with answer(turn-1)if score=result add proposition in new list
                compareSolutionAndAnswer(propositionsList.get(indexSubmitedProp), propositionsList.get(i));
                if (result.equals(compareSolutionAndAnswer(propositionsList.get(indexSubmitedProp), propositionsList.get(i)))){
                    bufferList.add(propositionsList.get(i));
                }
            }propositionsList = bufferList;/*update proposition list*/
        }
    }


    /**
     * Compare solution and answer
     * @param solution current solution formatted in int[combinationDigitNumber]
     * @param answer   generated answer formatted in int[combinationDigitNumber]
     * @return result format score, example:"12"
     */
    @Override
    public String compareSolutionAndAnswer(int[] solution, int[] answer) {
        int wellPlacedNumber = 0;
        int notWellPlacedNumber;
        int min = 0;
        //Calculate well placed number value
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (solution[i] == answer[i]) {
                wellPlacedNumber = wellPlacedNumber + 1;
            }
        }
        //Calculate not well placed number value
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
        notWellPlacedNumber = min - wellPlacedNumber;
        return String.valueOf((wellPlacedNumber * 10) + notWellPlacedNumber);
    }

    /**
     * Display indications after player or AI proposition
     * @param result from compareSolutionAndAnswer
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
     * @param result from compareSolutionAndAttempt
     * @return true if game is won
     */
    @Override
    public boolean isGameWon(String result) {
        return Integer.parseInt(result) == combinationDigitNumber * 10; /*Set win condition*/
    }

    /**
     *Import parameters from config.properties
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
        combinationDigitNumber = Integer.parseInt(properties.getProperty("mastermindCombinationDigitNumber"));
        attemptSetting = Integer.parseInt(properties.getProperty("mastermindAllowedAttempts"));
        mastermindAllowedNumber = Integer.parseInt(properties.getProperty("mastermindAllowedNumber"));
        devModeFromConfig = Integer.parseInt(properties.getProperty("devMode"));
    }

    /**
     * Generate random number from 0 to base setting
     * @return random int from 0 to base setting
     */
    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(mastermindAllowedNumber);
    }

}

