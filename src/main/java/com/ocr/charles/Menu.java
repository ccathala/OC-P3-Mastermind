package com.ocr.charles;

import com.ocr.charles.Exceptions.PlayerInputError;
import com.ocr.charles.Game.Game;
import com.ocr.charles.Game.Mastermind;
import com.ocr.charles.Game.SearchNumber;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    private static final Logger logger = LogManager.getLogger(Menu.class);

    /**
     * Display game menu choice
     */
    private void displayGameMenu() {
        System.out.println("Bienvenue joueur, choisi ton jeu");
        System.out.println("1 - Recherche +/-");
        System.out.println("2 - Mastermind");
        System.out.println("3 - Quitter l'application");
    }

    /**
     * Define the throwing conditions for PlayerInputError exception when player have to choose a game
     * @param chosenGame user input of choosen game
     * @throws PlayerInputError Exception throws message if input error
     */
    private void playerChooseCorrectGameOption(int chosenGame) throws PlayerInputError {
        if (chosenGame < 1 || chosenGame > 3) throw new PlayerInputError();
    }

    /**
     * Record player input for the game choice
     * @param args
     */
    protected void GameChoice(String args) {
        boolean quitGame = true;
        while (quitGame) {
            boolean correctInput;
            int game = 0;
            Scanner sc = new Scanner(System.in);
            do {
                displayGameMenu();
                try {
                    game = sc.nextInt();
                    playerChooseCorrectGameOption(game);
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
            Game current = null;
            if (game == 1) {
                logger.info("Jeu choisi : SearchNumber");
                current = new SearchNumber();

            } else if(game == 2){
                logger.info("Jeu choisi  : MastermindLevel");
                current = new Mastermind();

            }else if (game == 3) {

                logger.info("--------------FERMETURE DE L'APPLICATION-----------------");
                logger.info("---------------------------------------------------------");
                quitGame = false;
            }
            current.importParameterFromConfigProperties();
            current.newGame(args);
        }
    }


}
