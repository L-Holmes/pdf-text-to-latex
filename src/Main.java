import util.WriteTextToFileHandle;
/*
TODO:

-test for different pdfs
-test the paragraph and line search thing

 */
class Main
{
    public static void main(String[] args)
    {
        //"static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf"
        //"static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt"
        String inputPdfLocation = "static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf";
        String outputTextFileLocation = "static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt" ;

        System.out.println("\n...starting main...");
        RunProgram runner = new RunProgram(inputPdfLocation, outputTextFileLocation);

        /*
        TODO:
        -add more print statements to work out why the pages are not being converted correctly.
         */
    }
}