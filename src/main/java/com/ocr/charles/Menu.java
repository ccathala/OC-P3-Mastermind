package com.ocr.charles;

import com.ocr.charles.Exceptions.PlayerInputError;
import com.ocr.charles.Game.Mastermind;
import com.ocr.charles.Game.SearchNumber;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    /**
     * Display game menu choice
     */
    public void displayGameMenu() {
        System.out.println("Bienvenue joueur, choisi ton jeu");
        System.out.println("1 - Recherche +/-");
        System.out.println("2 - Mastermind");
        System.out.println("3 - Quitter le jeu");
    }

    /**
     * Define the throwing conditions for PlayerInputError exception when player have to choose a game
     * @param choosenGame
     * @throws PlayerInputError
     */
    public void playerChooseCorrectGameOption(int choosenGame) throws PlayerInputError {
        if (choosenGame < 1 || choosenGame > 3) throw new PlayerInputError();
    }

    /**
     * Record player input for the game choice
     * @param args
     */
    public void GameChoice(String args) {
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
                    correctInput = false;
                } catch (PlayerInputError e) {
                    System.out.println("Choisissez parmi les choix proposés.");
                    System.out.println();
                    correctInput = false;
                }
            } while (!correctInput);
            if (game == 1) {
                SearchNumber search = new SearchNumber();
                search.newGame("searchnumber",args);
            } else if(game == 2){
                Mastermind mastermind = new Mastermind();
                mastermind.newGame("mastermind",args);
            }else if (game == 3) {
                quitGame = false;
            }
        }
    }
}
