package main;

import main.file_choosing.FileSelector;
import main.util.WriteTextToFileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

/*
TODO:

-test for different pdfs
-test the paragraph and line search thing

 */
class Main
{
    /*
    steps for notes:
    -enter the pdf location
    -run program
    -create pdf output
    -check pdf output against original for issues with this app
        - adjust code if necessary
    -compare latex file with output pdf, adjusting where necessary
    -save latex file as final version.
    -create pdf output.
     */
    public static void main(String[] args)
    {
        String inputPdfLocation = new FileSelector().selectPdfFile();
        String inputPdfFromProjectRootLocation = inputPdfLocation.substring(inputPdfLocation.indexOf(".")+2);//+2 removes the "./" at the start

        String inputWithDashOutAdded = inputPdfFromProjectRootLocation.substring(0, inputPdfFromProjectRootLocation.lastIndexOf(".")) + "-out"+ inputPdfFromProjectRootLocation.substring(inputPdfFromProjectRootLocation.lastIndexOf("."));
        String outputTextFileFromProjectRootLocation = replaceFilePathExtensionWithTextFileExtension(inputWithDashOutAdded);


        /*
        //String inputPdfLocation = "static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf";
        //String outputTextFileLocation = "static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt";

        String inputPdfLocation = "static/test/SCC361-Wk1-L1-TEST.pdf";
        String outputTextFileLocation = "static/test/SCC361-Wk1-L1-TEST-out.txt";
        */

        RunProgram runner = new RunProgram(inputPdfFromProjectRootLocation, outputTextFileFromProjectRootLocation);

        //compile latex into pdf
        Main m = new Main();
        try {
            m.executeScript();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*
        TODO:

         -seems to be 1 or 2 additional blank questions at the end

         steps now:
         -try to implement that easier way to choose a pdf.
         -try with the full pdf
         -try with all of the pdfs from this week

         entering pdf ideas:
         -could choose using a gui
         -get the file entered.
         -generate the output pdf location (exact same but with '-out.txt' on end instead of '.pdf'
         -pass the same into the executeScript method
         */

        /*
        TODO (OTHER)
        - make the process of entering a new file easier
            * currently have to change both files in the main code, and in the pdflatex shell script

        - maybe remove repeat pictures for each page?
            * could maybe check if an image exists before creating a new image png file

        - Possibly put pictures side by side?
        - possibly remove page numbers
            - check for standalone numbers
            - that are also = previous page number + 1.
            - then remove that line
         */
    }

    //https://stackoverflow.com/questions/49558546/run-shell-script-from-java-code
    //accessed: 16/10/21
    public void executeScript() throws IOException, InterruptedException {
        File wd = new File(System.getProperty("user.dir") + "/");

        String command = "./convert2texAndCompile.sh";
        String[] cmd = { command,  "static/test" , "SCC361-Wk1-L1-TEST-out.tex"};

        Process p = Runtime.getRuntime().exec(cmd, null, wd);

        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));


        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        line = "";
        while ((line = errorReader.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static String replaceFilePathExtensionWithTextFileExtension(String inputFilePath)
    {
        //remove after the last dot
        if (inputFilePath == null || inputFilePath.length() <= 0 ) return null;
        int endIndex = inputFilePath.lastIndexOf(".");
        if (endIndex == -1) return null;
        String asLatex = inputFilePath.substring(0, endIndex);
        //add .tex
        asLatex = asLatex.concat(".txt");
        return asLatex;
    }

}