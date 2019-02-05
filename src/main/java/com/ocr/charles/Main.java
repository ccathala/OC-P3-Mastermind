package com.ocr.charles;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        String argument="";

        Options options = new Options();
        options.addOption("dev",false,"enable developer mode");
        try{
            CommandLine line = parser.parse(options,args);
            if (line.hasOption("dev")){
                argument="dev";

            }
        }catch (ParseException e){
            System.out.println( "Unexpected exception:" + e.getMessage() );
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }


        Menu menu = new Menu();
        menu.GameChoice(argument);


        /*String curDir = System.getProperty("user.dir");
        System.out.println ("Le répertoire courant est: "+curDir);*/
    }
}
