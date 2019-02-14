package com.ocr.charles.Game;

import com.ocr.charles.Exceptions.PlayerInputError;
import com.ocr.charles.Menu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

public abstract class Game {

    static final Logger logger = LogManager.getLogger(Menu.class);

    protected enum Player {
        Human,
        Ai;
    }

    protected int[][] solutionReturn; /* int[2][x digit] / index 0 player solution  / index 1 AI solution  */
    protected int[][] answerReturn; /* int[2][x digit] / index 0 player answer  / index 1 AI answer  */
    protected int[][] rangeAiAnswer;
    private String[][] result = new String[2][2]; /* Index 0 => player result/Index 1 Ai result */
    protected Player currentPlayer = Player.Human;
    protected int combinationDigitNumber;
    protected int mastermindAllowedNumber;
    private int attemptSetting;
    private int allowedAttempts;
    protected int attemptNumber;
    private boolean[] gameOver = new boolean[3];/*index 0 : is game over / index 1 : does player win*/
    private boolean devMode = false;


    /**
     * Display game heading
     * Overrided method, display heading for both Game
     */
    public void displayGameHeading() {

    }

    /**
     * Launch new game according to choosen game/mode
     *
     *
     * @param args
     */
    public void newGame(String args) {
        int mode = 0;
        if (args.equals("dev")) {
            devMode = true;
        }
        while (mode != 4) {
            mode = DisplayAndChooseGameMode();
            gameOver[2]=true;
            if(mode!=4){
                gameOver[2] = false;/*index 0: game is over/ index 1: player wins / index 2: player quit mode*/
            }
            while (!gameOver[2]) {
                logger.info("--------------------Nouvelle partie----------------------");
                initGame(); /* init game parameters */
                boolean duelInitialized = false;
                boolean duelAttempt = false; /* used for duel mode, increment attempt only if true */
                while (!gameOver[0]) {
                    attemptNumber = attemptSetting - allowedAttempts;
                    if (mode == 3 && !duelInitialized) {
                        duelInitialized = initDuelMode();
                        logger.info("Mode choisi : Duel");
                    }
                    int[][] returnMode = modeSequence(mode, currentPlayer, result[currentPlayer.ordinal()]);
                    logger.info("---------------------------------------------------------");
                    logger.info("Tour " + currentPlayer +" n°" + attemptNumber + " :");
                    logger.info("Solution : "+ retunModeSequenceForLogger(returnMode,0));
                    logger.info("Réponse : " + retunModeSequenceForLogger(returnMode,1));
                    result[currentPlayer.ordinal()] = compareAttemptAndSolution(returnMode);
                    gameOver = analyseResults(result[currentPlayer.ordinal()][0], mode, currentPlayer.toString());
                    if (gameOver[0]) {
                        displayResults(mode, gameOver[1]);
                    }
                    if (mode == 1 || mode == 2 || (mode == 3 && duelAttempt)) {
                        allowedAttempts = allowedAttempts - 1;
                        logger.info("Tentatives restantes : " + (allowedAttempts+1));
                    }
                    if (mode == 3) {
                        switchPlayer();
                        duelAttempt = !duelAttempt;
                        logger.info("Prochain joueur : " + currentPlayer);
                    }
                }
                gameOver[2] = displayNewGameOrMainMenuAndRecordInputPlayerFor();
            }
        }
    }

    /* Initialize game parameters------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /**
     * Init new game parameters
     *
     *
     */
    public void initGame() {
        int[] parameters = importParameterFromConfigProperties(); /*load parameters from config.properties*/
        combinationDigitNumber = parameters[0];
        mastermindAllowedNumber = parameters[3];
        attemptSetting = parameters[1];
        allowedAttempts = parameters[1] - 1;
        solutionReturn = new int[2][combinationDigitNumber];
        answerReturn = new int[2][combinationDigitNumber];
        gameOver[0] = false;
        gameOver[1] = false;
        rangeAiAnswer = new int[2][combinationDigitNumber];
        for (int i = 0; i < combinationDigitNumber; i++) { /* init range for defender sequence */
            rangeAiAnswer[0][i] = 0;
            rangeAiAnswer[1][i] = 9;
        }
        if (parameters[2] == 1) {
            devMode = true;
        }
        if (devMode){
            System.out.println("Mode développeur activé.");
            System.out.println();
        }
        logger.info("-----------------------Resume init-----------------------");
        logger.info("Nombre de digits : "+ combinationDigitNumber);
        logger.info("Tentatives autorisées : " + attemptSetting);
        logger.info("Tentatives autorisées -1 : " + allowedAttempts);
        logger.info("Partie terminée : " + gameOver[0]);
        logger.info("Joueur gagne : " + gameOver[1]);
        StringBuilder rangeMin= new StringBuilder();
        StringBuilder rangeMax= new StringBuilder();
        for(int i=0;i<combinationDigitNumber;i++){
            rangeMin.append(rangeAiAnswer[0][i]).append(" ");
            rangeMax.append(rangeAiAnswer[1][i]).append(" ");
        }
        logger.info("Range min: " + rangeMin);
        logger.info("Range max: " + rangeMax);
        logger.info("Développeur mode : " + devMode);
        logger.info("--------------------Fin resume init----------------------");
    }



    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /* Display mode menu---------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

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
     * Display mode menu and record player input for the mode choice
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
                logger.info("Erreur saisie utilisateur");
                correctInput = false;
            } catch (PlayerInputError e) {
                System.out.println("Choisissez parmi les choix proposés.");
                System.out.println();
                logger.info("Erreur saisie utilisateur");
                correctInput = false;
            }
        } while (!correctInput);
        if (mode==4){
            logger.info("Retour menu choix du jeu");
        }

        return mode;
    }

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /**
     * Import parameters from file config.properties
     *
     *
     *                    importedValues[0] rules the number of digits of the secret combination
     *                    importedValues[1] rules the amount of allowed attempts
     * @return imported values
     */
    protected abstract int[] importParameterFromConfigProperties();


    public int[][] modeSequence(int choosenMode, Player currentPlayer, String [] result) {
        if (choosenMode == 1 || (choosenMode == 3 && currentPlayer.ordinal() == 0)) {
            return challengerSequence(choosenMode);
        } else if (choosenMode == 2 || (choosenMode == 3 && currentPlayer.ordinal() == 1)) {
            return defenderSequence(result, choosenMode);
        }
        return new int[2][4];
    }

    /*Challenger Sequence--------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    public int[][] challengerSequence(int choosenMode) {
        if (attemptNumber == 1 && choosenMode != 3) {
            currentPlayer = Player.Human;
            System.out.println("Bienvenue Challenger !");
            System.out.println();
            System.out.println("Trouve la combinaison cachée à " + combinationDigitNumber + " chiffres en " + attemptSetting + " tentatives.");
            solutionReturn[1] = aiChooseRandomCombination();/*Record hidden combination*/
            logger.info("Mode choisi : Challenger");
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
        StringBuilder aiHiddenCombinationString = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) {
            aiHiddenCombination[i] = randomNumber();
            aiHiddenCombinationString.append(aiHiddenCombination[i]);
        }
        if (devMode) {
            System.out.print("Combinaison secrète : " + aiHiddenCombinationString);
        }
        System.out.println();
        return aiHiddenCombination;
    }

    protected abstract int randomNumber();

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /*Defender Sequence----------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    public int[][] defenderSequence(String [] result, int choosenMode) {
        if (attemptNumber == 1 && choosenMode != 3) {
            currentPlayer = Player.Ai;
            System.out.println("Bienvenue Défenseur !");
            System.out.println();
            System.out.println("Choisi une combinaison cachée à " + combinationDigitNumber + " chiffres.");
            solutionReturn[0] = recordPlayerCombinationInput();/*Record hidden combination*/
            logger.info("Mode choisi : Defender");
        }
        System.out.println("----------------------------------");
        System.out.print("Tour ordinateur n°" + attemptNumber + " : ");
        answerReturn[1] = generateAndDisplayAiAnswer(result);/*Record AI attempt*/
        int[][] defenderReturn = new int[2][combinationDigitNumber];
        defenderReturn[0] = solutionReturn[0];
        defenderReturn[1] = answerReturn[1];
        return defenderReturn;
    }

    public abstract int[] generateAndDisplayAiAnswer(String[] result);

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /*Record player input for both mode -----------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    protected abstract void playerCorrectCombinationInput(String[] playerInput) throws PlayerInputError;

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

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /*Duel Sequence -------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /**
     * Initialize duel mode by display welcome sentence, instructions and recording hidden combination for bot players
     *
     * @return
     */
    protected boolean initDuelMode() {
        System.out.println("C'est l'heure du Duel! Trouvez la combinaison secrète en " + attemptSetting + " tentatives.");
        System.out.println();
        System.out.println("Joueur choisi une combinaison cachée à " + combinationDigitNumber + " chiffres.");
        solutionReturn[0] = recordPlayerCombinationInput();/*Record player hidden combination*/
        solutionReturn[1] = aiChooseRandomCombination();/*Record AI hidden combination*/
        currentPlayer = Player.valueOf(toss()); /* Choose randomly who begins Player or AI, display who begins*/
        return true;
    }

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

    /**
     * Switch player turn for duel mode
     */
    protected void switchPlayer() {
        if (currentPlayer.toString().equals("Human")) {
            currentPlayer = Player.Ai;
        } else {
            currentPlayer = Player.Human;
        }
    }

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /* Compare, analyze, display result------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    public abstract String[] compareAttemptAndSolution(int[][] solutionAndAttempt);

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
        logger.info("La partie est terminée : "+gameOver[0]);
        logger.info("Le joueur remporte la partie : "+gameOver[1]);
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

    /*---------------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/

    /*New game menu--------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------------*/


    /**
     * Define the throwing conditions for PlayerInputError exception when player have to choose new game or quit game
     *
     * @param choice
     * @throws PlayerInputError
     */
    public void playerChooseCorrectNewGameOption(int choice) throws PlayerInputError {
        if (choice < 1 || choice > 3) throw new PlayerInputError();
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
            System.out.println("3 - Quitter l'application");
            try {
                endGameChoice = sc.nextInt();
                playerChooseCorrectNewGameOption(endGameChoice);
                correctInput = true;
            } catch (InputMismatchException e) {
                sc.next();
                System.out.println("Vous devez saisir un chiffre parmi les choix proposés.");
                System.out.println();
                logger.info("Erreur saisie utilisateur");
                correctInput = false;
            } catch (PlayerInputError e) {
                System.out.println("Choisissez parmi les choix proposés.");
                System.out.println();
                logger.info("Erreur saisie utilisateur");
                correctInput = false;
            }
        } while (!correctInput);
        if(endGameChoice==1){
            logger.info("---------------------------------------------------------");
            logger.info("Joueur rejoue une partie.");
        }
        else if(endGameChoice == 2) {
            newGame = true;
            logger.info("---------------------------------------------------------");
            logger.info("Retour menu choix du mode.");
        }else if(endGameChoice==3){
            logger.info("--------------FERMETURE DE L'APPLICATION-----------------");
            logger.info("---------------------------------------------------------");
            System.exit(1);
        }
        return newGame;
    }
    /*---------------------------------------------------------------------------------------------------------------*/
    /*---------------------------------------------------------------------------------------------------------------*/

        public String retunModeSequenceForLogger(int[][]modeSequence, int solutionOrAnswerIndex){
            String[] returModeValues = new String[2];
            StringBuilder solution = new StringBuilder();
            StringBuilder answer = new StringBuilder();
            for(int i =0;i<combinationDigitNumber;i++){
                solution.append(modeSequence[0][i]).append(" ");
                answer.append(modeSequence[1][i]).append(" ");
            }
            returModeValues[0]= String.valueOf(solution);
            returModeValues[1]= String.valueOf(answer);
            return returModeValues[solutionOrAnswerIndex];
        }

}
