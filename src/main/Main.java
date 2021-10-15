package main;

import main.util.WriteTextToFileHandle;
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
        //String inputPdfLocation = "static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf";
        //String outputTextFileLocation = "static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt";
        String inputPdfLocation = "static/test/SCC361-Wk1-L1-TEST.pdf";
        String outputTextFileLocation = "static/test/SCC361-Wk1-L1-TEST-out.txt";

        RunProgram runner = new RunProgram(inputPdfLocation, outputTextFileLocation);

        /*
        TODO:
        latest issues:

         -seems to be 1 or 2 additional blank questions at the end
         */

        /*
        TODO (OTHER)
        - make the process of entering a new file easier
            * currently have to change both files in the main code, and in the pdflatex shell script
         */
    }
}