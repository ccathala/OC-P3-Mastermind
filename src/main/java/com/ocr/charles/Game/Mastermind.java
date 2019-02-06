package com.ocr.charles.Game;

public class Mastermind extends Game {
    @Override
    public void displayGameHeading(){
        System.out.println("----------[Mastermind]----------");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        return new int[0];
    }


}
