package com.ocr.charles;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.GameChoice();
        String curDir = System.getProperty("user.dir");
        System.out.println ("Le répertoire courant est: "+curDir);
    }
}
