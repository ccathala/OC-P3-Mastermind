package com.ocr.charles.Game;

public class SearchNumber extends Game {

    @Override
    public void displayGameHeading(){
        System.out.println("----------[Recherche +/-]----------");
    }

    @Override
    public int[] generateAndDisplayAiAnswer(String result) {
        StringBuilder displayAnswer = new StringBuilder();
        int[]aiAnswerCombination = new int[combinationDigitNumber];
        if (attemptNumber == 1) {
            rangeAiAnswer = new int[2][combinationDigitNumber];


            for (int i = 0; i < combinationDigitNumber; i++) {
                rangeAiAnswer[0][i] = 0;
                rangeAiAnswer[1][i] = 9;
                aiAnswerCombination[i] = 5;
                displayAnswer.append(aiAnswerCombination[i]);
            }
            System.out.println(displayAnswer);
            return aiAnswerCombination;
        } else {
            for (int i = 0; i < combinationDigitNumber; i++) {
                if (result.charAt(i) == '+') {
                    rangeAiAnswer[0][i] = aiAnswerCombination[i] + 1;
                    aiAnswerCombination[i] = rangeAiAnswer[0][i] + ((rangeAiAnswer[1][i] - rangeAiAnswer[0][i]) / 2);
                } else if (result.charAt(i) == '-') {
                    rangeAiAnswer[1][i] = aiAnswerCombination[i] - 1;
                    aiAnswerCombination[i] = rangeAiAnswer[1][i] - ((rangeAiAnswer[1][i] - rangeAiAnswer[0][i]) / 2);
                }
                displayAnswer.append(aiAnswerCombination[i]);

            }
            System.out.println(displayAnswer);
            return aiAnswerCombination;


        }
    }

    public String compareAttemptAndSolution(int[][] solutionAndAttempt) {
        String[] comparatorTable = new String[combinationDigitNumber];
        StringBuilder comparator = new StringBuilder();
        for (int i = 0; i < combinationDigitNumber; i++) {
            if (solutionAndAttempt[1][i] == solutionAndAttempt[0][i]) {
                comparatorTable[i] = "=";
            } else if (solutionAndAttempt[1][i] < solutionAndAttempt[0][i]) {
                comparatorTable[i] = "+";
            } else {
                comparatorTable[i] = "-";
            }
            comparator.append(comparatorTable[i]);
        }
        System.out.print("           ->Réponse : ");
        System.out.println(comparator.toString());
        return comparator.toString();
    }
}
