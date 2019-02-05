package com.ocr.charles.Game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(System.out);
    }


    @Test
    public void Given_SNGameWhen_displayGameHeadingRunThen_displayTheCorrectHeading(){
        SearchNumber search = new SearchNumber();
        search.displayGameHeading();
        String[] output = outContent.toString().replace("\r\n", "\n").split("\n");
        assertEquals("----------[Recherche +/-]----------", output[0]);
    }

    @Test
    public void Given_MMGameWhen_displayGameHeadingRunThen_displayTheCorrectHeading(){
        Mastermind search = new Mastermind();
        search.displayGameHeading();
        String[] output = outContent.toString().replace("\r\n", "\n").split("\n");
        assertEquals("----------[Mastermind]----------", output[0]);
    }

}