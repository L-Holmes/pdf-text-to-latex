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

        latest issues:
        -The page breaks don't appear to be working
            * check page breaks.
            * check pages are ended correctly
            * maybe investigate the other one that was working and look for a difference
            * if necessary, try other page break techniques
        -When a bullet point goes onto multiple lines, the following lines aren't bullet pointed.
         But then, when the next bullet point is handled, it isn't made a bullet point
         -Sometimes the bullet points are not detected at all -- maybe previous bullet points were not ended correctly???
            * Check that the bullet points are being started and ended correctly (I suspect that they are not)

         -Only the first page appears to be quizified
            * fix this

         */

    }
}