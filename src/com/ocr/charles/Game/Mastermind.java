package com.ocr.charles.Game;

public class Mastermind extends Game {
    @Override
    public void displayGameHeading(){
        System.out.println("----------[Mastermind]----------");
    }

    public int[] generateAndDisplayAiAnswer(int combinationDigitNumber, int attemptNumber, String result) {
        return new int[1];
    }
}
