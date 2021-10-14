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
        I have a theory, if ACCESS MAIN POWER GRID is seen, the whole line is removed (I think), so this may be breaking some stuff.

        -it seems like a lot of the content is being removed after
         going through the latexify process.

         todo remove old print statements
         TODO: ADD THE PRINT STATEMENTS
         TODO: CHECK THAT NEW MAIN POWER GRID THING

         TODO: (1) create a test for latexify
               were I can enter custom text
               (2) copy the first 5 pages, of the original text output that was fetched
               (3) enter this text as the input to the latexify test, and add lots of print statements to
                   see what is going on.

        -page titles are not being picked up
         */

    }
}