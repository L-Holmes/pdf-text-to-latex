package main.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Creates a copy of a text file, with the copy sharing the same name and having the .tex file extension
 */
public class ConvertOutTextToLatex implements Runnable {

    private static String inputTextFileLocation = "";
    private static String outputLaTexFileLocation = "";

    public static void setParameters(String newInputTextFileLocation, String newOutputLaTexFileLocation)
    {
        inputTextFileLocation = newInputTextFileLocation;
        outputLaTexFileLocation = newOutputLaTexFileLocation;
    }

    private static void resetParameters()
    {
        inputTextFileLocation = "";
        outputLaTexFileLocation = "";
    }

    @Override
    public void run() {
        if(inputTextFileLocation == "" ||  outputLaTexFileLocation == "") {
            createLatexCopy();
            System.out.println("created latex copy (1)");
        }
        else {
            System.out.println("created latex copy (2)");
            createLatexCopy(inputTextFileLocation, outputLaTexFileLocation);
        }
        //convertTheTextFileToLatex();
        System.out.println("resetting params...");
        resetParameters();
        System.out.println("successfully reset the params");
    }


    /**
     * creates a copy of the text file holding the parsed pdf output.
     * also changes the extension of the copied file to .tex
      */
    private void createLatexCopy()
    {
        Path copied = Paths.get(System.getProperty("user.dir")+"/static/out/out.tex");
        Path originalPath = Paths.get(System.getProperty("user.dir")+"/static/out/out.txt");
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a copy of the text file holding the parsed pdf output.
     * also changes the extension of the copied file to .tex
     */
    private void createLatexCopy(String inputTextFileLocation, String outputLaTexFileLocation)
    {
        System.out.println("creating latex copy (with parameters)");
        Path originalPath = Paths.get(System.getProperty("user.dir")+"/"+inputTextFileLocation);
        Path copied = Paths.get(System.getProperty("user.dir")+"/"+outputLaTexFileLocation);
        try {
            System.out.println("here, about to copy...");
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("copied successfully");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void convertTheTextFileToLatex()
    {
        //source: https://stackoverflow.com/questions/30622160/running-a-shell-script-from-java-code
        //accessed: 24/03/2021
        try {
            String target = new String(System.getProperty("user.dir")+"/zsh convert2texAndCompile.sh");
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(target);
            proc.waitFor();
            StringBuffer output = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
