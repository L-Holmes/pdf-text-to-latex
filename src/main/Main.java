package main;

import main.util.WriteTextToFileHandle;
/*
TODO:

-test for different pdfs
-test the paragraph and line search thing

 */
class Main
{
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


         -pictures not showing

         */

    }
}