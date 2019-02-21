package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;

import java.io.IOException;
import java.util.*;


public class Mastermind extends Game {


    protected int mastermindAllowedNumber;
    protected LinkedList<int[]> propositionsList;
    protected LinkedList<int[]> bufferList;
    protected LinkedList<Integer> scoreList;
    int[][] scoreTable;
    int indexSubmitedProp;
    int[] submitedProposition = new int[0];
    protected int combinationNumber; /* Number of possible combinations*/
    protected int currentCombination;
    protected int quotient = 0;

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
     * @param playerInput Player input splitted in String[]
     * @throws PlayerInputError Exception display message when incorrect input
     */
    @Override
    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) {
            throw new PlayerInputError();
        }
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (Integer.parseInt(playerInput[i]) > (mastermindAllowedNumber - 1)) {
                throw new PlayerInputError();
            }
        }
    }

    @Override
    protected void errorSentence() {
        System.out.println("Erreur de saisie - Veuillez saisir une combinaison composée de " + combinationDigitNumber +
                " chiffres compris entre 0 et " + (mastermindAllowedNumber - 1) + ".");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        if (attemptNumber == 1) {
            currentCombination = 1;
            //propositionsList = new LinkedList<>();
            combinationNumber = (int) Math.pow(mastermindAllowedNumber, combinationDigitNumber);
            submitedProposition = aiChooseRandomCombination();
            //propositionsList.add(retour);
            //retour = propositionsList.get(0);
            generateScoresList(); //knuth
        }
        //IA normal----------------------------------------------------------------------------
        /*else if (attemptNumber == 2) {
            generatePropositionListAccordingScoreAttempt1(result);
            propositionsList.remove(0); // Fin génération liste des propositions
            retour = propositionsList.get(0);

        } else if (attemptNumber >= 3) {
            for (int i = 1; i < propositionsList.size(); i++) {
                String score = compareSolutionAndAttempt(propositionsList.get(0), propositionsList.get(i));
                if (!score.equals(result)) {
                    propositionsList.remove(i);
                    i = i - 1;
                }
            }
            propositionsList.remove(0);
            retour = propositionsList.get(0);
        }*/

        //knuth---------------------------------------------------------------------

        if (attemptNumber >= 2) {

            generatePropositionListAccordingPreviousScoreAttempt(result,submitedProposition);
            generateScoreTable();
            indexSubmitedProp = choseMinWeightProposition(scoreCalculation());
            /*for (int i = 0; i < combinationDigitNumber; i++) {
                displayAnswer.append(propositionsList.get(indexSubmitedProp)[i]);
            }
            System.out.println(displayAnswer);*/
            submitedProposition= propositionsList.get(indexSubmitedProp);
        }
        for (int i = 0; i < combinationDigitNumber; i++) {
            displayAnswer.append(submitedProposition[i]);
        }
        System.out.println(displayAnswer);
        return submitedProposition;
    }


    public void generateScoresList() {
        scoreList = new LinkedList<>();
        for (int i = 0; i <= combinationDigitNumber; i++) {
            Integer dizaine = 10 * i;
            if (i <= combinationDigitNumber - 2) {
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
        /*System.out.println(scoresList);
        System.out.println(scoresList.size());*/
    }

    public void generateScoreTable(){
        scoreTable = new int[propositionsList.size()][(scoreList.size() + 1)];
    }

    public int scoreCalculation() {
        int[] solution = new int[combinationDigitNumber];
        int[] answer = new int[combinationDigitNumber];
        int score = 0;
        int minWeight = 0;
        // choix de la proposition de référence
        for (int i = 0; i < propositionsList.size(); i++) {
            solution = propositionsList.get(i);
            // choix de la proposition candidate
            for (int j = 0; j < propositionsList.size(); j++) {
                answer = propositionsList.get(j);
                // Calcul du score
                score = Integer.parseInt(compareSolutionAndAttempt(solution, answer));
                // Ajout du score dans le tableau de score
                int k = 0;
                while (score != scoreList.get(k)) {
                    k++;
                }
                scoreTable[i][k]++;
            }
            // Recherche du poids Max de la proposition de référence
            int weight = 0;
            for (int j = 0; j <= scoreList.size(); j++) {
                if (weight < scoreTable[i][j]) {
                    weight = scoreTable[i][j];
                }
            }
            // Ajout du poids max dans le tableau de score
            scoreTable[i][scoreList.size()] = weight;
            // Recherche du poids min parmi toutes les propositions candidates
            if (minWeight == 0) {
                minWeight = scoreTable[i][scoreList.size()];
            } else if (minWeight > scoreTable[i][scoreList.size()]) {
                minWeight = scoreTable[i][scoreList.size()];
            }
        }
        return minWeight;
    }

    public int choseMinWeightProposition(int minWeight) {
        int i = 0;
        while (minWeight != scoreTable[i][scoreList.size()]) {
            i++;
        }
        return i;
    }

    public void generatePropositionListAccordingScoreAttempt1(String result , int[]solution) {
        // Génère la liste des propositions en fonction du score arbitrage n-1
        propositionsList=new LinkedList<>();
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
            String score = compareSolutionAndAttempt(solution, resultTable);
            if (score.equals(result)) {
                propositionsList.add(resultTable);
            }
            currentCombination = currentCombination + 1;
        }

    }

    public void generatePropositionListAccordingPreviousScoreAttempt(String result, int[] solution) {
        if(attemptNumber==2){
            generatePropositionListAccordingScoreAttempt1(result, solution);
            propositionsList.remove(0);
        }
        if (attemptNumber > 2) {
            bufferList = new LinkedList<>();
            for (int i = 0; i < propositionsList.size(); i++) {
                compareSolutionAndAttempt(propositionsList.get(indexSubmitedProp), propositionsList.get(i));
                if (result.equals(compareSolutionAndAttempt(propositionsList.get(indexSubmitedProp), propositionsList.get(i)))){
                    bufferList.add(propositionsList.get(i));
                }

            }propositionsList = bufferList;
        }
    }


    /**
     * @param solution current solution formatted in int[combinationDigitNumber]
     * @param answer   generated answer formatted in int[combinationDigitNumber]
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
     * @return
     */
    @Override
    protected int randomNumber() {
        Random random = new Random();
        return random.nextInt(mastermindAllowedNumber);
    }


}

