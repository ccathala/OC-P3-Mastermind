package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public abstract class Game {


    protected enum Player {
        Human,
        Ai;
    }

    protected int[][] solutionReturn; /* int[2][x digit] / index 0 player solution  / index 1 AI solution  */
    protected int[][] answerReturn; /* int[2][x digit] / index 0 player answer  / index 1 AI answer  */
    protected int[][] rangeAiAnswer;
    private String[] result = new String[2]; /* Index 0 => player result/Index 1 Ai result */
    protected Player currentPlayer = Player.Human;
    int combinationDigitNumber;
    int attemptSetting;
    int allowedAttempts;
    int attemptNumber;
    boolean[] gameOver = new boolean[3];/*index 0 : is game over / index 1 : does player win*/


    /**
     * Display game heading
     */
    public void displayGameHeading() {

    }


    public void newGame(String choosenGame) {

        int mode = 0;

        while (mode != 4) {
            mode = DisplayAndChooseGameMode();
            gameOver[2] = false;/*index 0: game is over/ index 1: player wins / index 2: player quit mode*/
            while (!gameOver[2]) {
                initGame(choosenGame);
                boolean initModeDuel=false;
                boolean duelAttempt = false;
                while (!gameOver[0]) {
                    attemptNumber = attemptSetting - allowedAttempts;
                    if (mode == 3&& !initModeDuel) {
                        System.out.println("C'est l'heure du Duel! Trouvez la combinaison secrète en " + attemptSetting + " tentatives.");
                        System.out.println();
                        System.out.println("Joueur choisi une combinaison cachée à " + combinationDigitNumber + " chiffres.");
                        solutionReturn[0] = recordPlayerCombinationInput();/*Record hidden combination*/
                        solutionReturn[1] = aiChooseRandomCombination();/*Record hidden combination*/
                        currentPlayer = Player.valueOf(toss());
                        initModeDuel = true;
                    }
                    int[][] returnMode = modeSequence(mode, currentPlayer, result[currentPlayer.ordinal()]);
                    result[currentPlayer.ordinal()] = compareAttemptAndSolution(returnMode);
                    gameOver = analyseResults(result[currentPlayer.ordinal()], mode, currentPlayer.toString());
                    if (gameOver[0]) {
                        displayResults(mode, gameOver[1]);
                    }

                    if (mode == 1 || mode == 2 || (mode == 3 && duelAttempt)) {
                        allowedAttempts = allowedAttempts - 1;
                    }
                    duelAttempt = !duelAttempt;

                    if (mode == 3) {
                        if (currentPlayer.toString().equals("Human")) {
                            currentPlayer = Player.Ai;
                        } else {
                            currentPlayer = Player.Human;
                        }
                    }


                }
                gameOver[2] = displayNewGameOrMainMenuAndRecordInputPlayerFor();
            }

        }

    }

    public void initGame(String choosenGame) {
        int[] parameters = importParameterFromConfigProperties(choosenGame);
        combinationDigitNumber = parameters[0];
        attemptSetting = parameters[1];
        allowedAttempts = parameters[1] - 1;
        solutionReturn = new int[2][combinationDigitNumber];
        answerReturn = new int[2][combinationDigitNumber];
        gameOver[0] = false;
        gameOver[1] = false;
        rangeAiAnswer = new int[2][combinationDigitNumber];
        for(int i=0;i<combinationDigitNumber;i++){
            rangeAiAnswer[0][i]=0;
            rangeAiAnswer[1][i]=9;
        }
    }

    /* Display mode menu------------------------------------------------------------------------------------------*/

    /**
     * Define the throwing conditions for PlayerInputError exception when player have to choose a mode
     *
     * @param choosenMode
     * @throws PlayerInputError
     */
    public void playerChooseCorrectGameModeOption(int choosenMode) throws PlayerInputError {
        if (choosenMode < 1 || choosenMode > 4) throw new PlayerInputError();
    }

    /**
     * Record player input for the mode choice
     *
     * @param
     */
    public int DisplayAndChooseGameMode() {
        int mode = 0;
        Scanner sc = new Scanner(System.in);
        boolean correctInput;
        do {
            displayGameHeading();
            System.out.println();
            System.out.println("Choisi ton mode de jeu");
            System.out.println("1 - Mode Challenger");
            System.out.println("2 - Mode Defender");
            System.out.println("3 - Mode Duel");
            System.out.println("4 - Retour au menu principal");
            try {
                mode = sc.nextInt();
                playerChooseCorrectGameModeOption(mode);
                correctInput = true;
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("Vous devez saisir un chiffre parmi les choix proposés.");
                System.out.println();
                correctInput = false;
            } catch (PlayerInputError e) {
                System.out.println("Choisissez parmi les choix proposés.");
                System.out.println();
                correctInput = false;
            }
        } while (!correctInput);

        return mode;
    }

    /* End display mode menu------------------------------------------------------------------------------------------*/

    /**
     * Import parameters from file config.properties
     *
     * @param choosenGame choosen game input
     *                    importedValues[0] rules the number of digits of the secret combination
     *                    importedValues[1] rules the amount of allowed attempts
     * @return imported values
     */
    protected int[] importParameterFromConfigProperties(String choosenGame) {

        Properties properties = new Properties();
        int[] importedValues = new int[2];
        try {
            properties.load(Game.class.getClassLoader().getResourceAsStream("META-INF/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("echec");
        }
        if (choosenGame.equals("searchnumber")) {
            importedValues[0] = Integer.parseInt(properties.getProperty("researchNumberCombinationDigitNumber"));
            importedValues[1] = Integer.parseInt(properties.getProperty("researchNumberAllowedAttempts"));
        } else if (choosenGame.equals("mastermind")) {
            importedValues[0] = Integer.parseInt(properties.getProperty("mastermindCombinationDigitNumber"));
            importedValues[1] = Integer.parseInt(properties.getProperty("mastermindAllowedAttempts"));
        }
        return importedValues;
    }


    public int[][] modeSequence(int choosenMode, Player currentPlayer, String result) {
        if (choosenMode == 1 || (choosenMode == 3 && currentPlayer.ordinal() == 0)) {
            return challengerSequence(choosenMode);
        } else if (choosenMode == 2 || (choosenMode == 3 && currentPlayer.ordinal() == 1)) {
            return defenderSequence(result, choosenMode);
        }
        return new int[2][4];
    }


    /*Challenger Sequence--------------------------------------------------------------------------------*/

    public int[][] challengerSequence(int choosenMode) {
        if (attemptNumber == 1 && choosenMode != 3) {
            currentPlayer = Player.Human;
            System.out.println("Bienvenue Challenger !");
            System.out.println();
            System.out.println("Trouve la combinaison cachée à " + combinationDigitNumber + " chiffres en " + attemptSetting + " tentatives.");
            solutionReturn[1] = aiChooseRandomCombination();/*Record hidden combination*/
        }
        System.out.println("----------------------------------");
        System.out.print("Tour joueur n°" + attemptNumber + " : ");
        answerReturn[0] = recordPlayerCombinationInput();/*Record player attempt*/
        int[][] challengerReturn = new int[2][combinationDigitNumber];
        challengerReturn[0] = solutionReturn[1];
        challengerReturn[1] = answerReturn[0];
        return challengerReturn;
    }

    /**
     * AI choose a random combination composed by X digits between 0 and 9
     */
    protected int[] aiChooseRandomCombination() {
        int[] aiHiddenCombination = new int[combinationDigitNumber];
        Random random = new Random();
        for (int i = 0; i < combinationDigitNumber; i++) {
            aiHiddenCombination[i] = random.nextInt(10);
            System.out.print(aiHiddenCombination[i]);
        }
        System.out.println();
        return aiHiddenCombination;
    }

    /*End Challenger Sequence--------------------------------------------------------------------------------*/

    /*Defender Sequence--------------------------------------------------------------------------------*/

    public int[][] defenderSequence(String result, int choosenMode) {
        if (attemptNumber == 1 && choosenMode != 3) {
            currentPlayer = Player.Ai;
            System.out.println("Bienvenue Défenseur !");
            System.out.println();
            System.out.println("Choisi une combinaison cachée à " + combinationDigitNumber + " chiffres.");

            solutionReturn[0] = recordPlayerCombinationInput();/*Record hidden combination*/
        }
        System.out.println("----------------------------------");
        System.out.print("Tour ordinateur n°" + attemptNumber + " : ");
        answerReturn[1] = generateAndDisplayAiAnswer(result);/*Record AI attempt*/
        int[][] defenderReturn = new int[2][combinationDigitNumber];
        defenderReturn[0] = solutionReturn[0];
        defenderReturn[1] = answerReturn[1];
        return defenderReturn;
    }

    /*End Defender Sequence--------------------------------------------------------------------------------*/

    /*Record player input for both mode --------------------------------------------------------------------------------*/

    protected void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError {
        if (playerInput.length != combinationDigitNumber) throw new PlayerInputError();
    }

    /**
     * Record player input combination composed by X digits between 0 and 9
     * Increment by 1 the value of attempNumberPlayer
     */
    protected int[] recordPlayerCombinationInput() {
        boolean correctInput;
        int[] playerCombinationInput = new int[combinationDigitNumber];
        do {
            try {
                String[] playerCombinationInputString;
                Scanner sc = new Scanner(System.in);
                playerCombinationInputString = sc.nextLine().split("");
                playerCorrectCombinationInput(playerCombinationInputString);
                for (int i = 0; i < combinationDigitNumber; i++) {
                    playerCombinationInput[i] = Integer.parseInt(playerCombinationInputString[i]);
                }
                correctInput = true;
            } catch (PlayerInputError | NumberFormatException e) {
                System.out.println();
                System.out.println("Saisissez une combinaison à " + combinationDigitNumber + " chiffres");
                System.out.println();
                System.out.println("----------------------------------");
                System.out.print("Tentative joueur n°" + attemptNumber + " : ");
                correctInput = false;
            }
        } while (!correctInput);
        return playerCombinationInput;


    }

    /*End record player input for both mode --------------------------------------------------------------------------------*/

    public String compareAttemptAndSolution(int[][] solutionAndAttempt) {
        return "";
    }

    /*Duel Sequence --------------------------------------------------------------------------------*/

    /**
     * Do a toss to define the first player
     *
     * @return
     */
    protected String toss() {
        Random random = new Random();
        int toss = random.nextInt(2);
        String currentPlayer;
        String playerWhoBegins;
        if (toss == 0) {
            playerWhoBegins = "Joueur commence !";
            currentPlayer = "Human";
        } else {
            playerWhoBegins = "Ordinateur commence !";
            currentPlayer = "Ai";
        }
        System.out.println(playerWhoBegins);
        return currentPlayer;

    }


    public int[] generateAndDisplayAiAnswer(String result) {
        return new int[1];
    }


    /**
     * Analyze results and end the game if win condition is true
     *
     * @param choosenMode
     * @param currentPlayer
     * @return boolean gameOver = true if game is ended
     */
    protected boolean[] analyseResults(String result, int choosenMode, String currentPlayer) {
        StringBuilder winCondition = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) { /* Generate win condition string*/
            winCondition.append("=");
        }
        if ((result.equals(winCondition.toString()))) {
            gameOver[0] = true;
            if (choosenMode == 1) {
                gameOver[1] = true;
            } else if (choosenMode == 2) {
                gameOver[1] = false;
            } else if (choosenMode == 3) {
                if (currentPlayer.equals("Human")) {
                    gameOver[1] = true;
                } else {
                    gameOver[1] = false;
                }
            }
        } else if (allowedAttempts == 0) {
            gameOver[0] = true;
            if (choosenMode == 1) {
                gameOver[1] = false;
            } else if (choosenMode == 2) {
                gameOver[1] = true;
            } else if (choosenMode == 3) {
                if (currentPlayer.equals("Human")) {
                    gameOver[1] = false;
                } else {
                    gameOver[1] = true;
                }
            }
        }
        return gameOver;
    }

    /**
     * Display the win or lose sentence according to results
     *
     * @param choosenMode
     * @param playerWin
     */
    protected void displayResults(int choosenMode, boolean playerWin) {
        StringBuilder answer = new StringBuilder();
        if (playerWin) {

            System.out.print("Bravo ");
            if (choosenMode == 1) {
                System.out.print("tu as trouvé la combinaison, ");
            } else if (choosenMode == 2) {
                System.out.print("l'ordinateur n'a pas trouvé la combinaison, ");
            } else if (choosenMode == 3) {
                System.out.print("tu as trouvé la combinaison avant l'ordinateur, ");
            }

            System.out.println("tu remportes la partie !");

        } else {
            for (int i = 0; i < combinationDigitNumber; i++) {
                answer.append(solutionReturn[1][i]);
            }
            if (choosenMode == 1) {
                System.out.println("Tu as utilisé toutes tes tentatives, l'ordinateur remporte la partie !");
                System.out.println();
                System.out.println("La solution était " + answer + ".");

            } else if (choosenMode == 2) {
                System.out.println("L'ordinateur a trouvé la combinaison, il remporte la partie !");
            } else if (choosenMode == 3) {
                System.out.println("L'ordinateur a trouvé la combinaison avant toi, il remporte la partie !");
                System.out.println();
                System.out.println("La solution était " + answer + ".");
            }
        }
        System.out.println();
    }


    /**
     * Define the throwing conditions for PlayerInputError exception when player have to choose new game or quit game
     *
     * @param choice
     * @throws PlayerInputError
     */
    public void playerChooseCorrectNewGameOption(int choice) throws PlayerInputError {
        if (choice < 1 || choice > 2) throw new PlayerInputError();
    }

    /**
     * Dispaly new game menu and record input player to choose new game or quit game option
     *
     * @return
     */
    protected boolean displayNewGameOrMainMenuAndRecordInputPlayerFor() {
        Scanner sc = new Scanner(System.in);
        boolean newGame = false;
        boolean correctInput;
        int endGameChoice = 0;
        System.out.println("Partie Terminée !");
        System.out.println();
        do {

            System.out.println("1 - Rejouer une partie");
            System.out.println("2 - Retour au menu mode de jeu");
            try {
                endGameChoice = sc.nextInt();
                playerChooseCorrectNewGameOption(endGameChoice);
                correctInput = true;
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("Vous devez saisir un chiffre parmi les choix proposés.");
                System.out.println();
                correctInput = false;
            } catch (PlayerInputError e) {
                System.out.println("Choisissez parmi les choix proposés.");
                System.out.println();
                correctInput = false;
            }
        } while (!correctInput);

        if (endGameChoice == 2) {
            newGame = true;
        }
        return newGame;
    }


}
