import converting_text_to_latex_equivalent.Latexify;
import converting_text_to_latex_equivalent.LatexifyOptionalArguments;
import pdf_parsing.ParsePdf;
import util.ConvertOutTextToLatex;
import util.WriteTextToFileHandle;

/**
 * Used to the run the process, which are used to
 * take a pdf as an input, and create an approximate corresponding
 * latex file, which recreates the contents of that pdf
 */
public class RunProgram {
    private ParsePdf pdfParser;//the parser used to extract text and images from the pdf
    private String newPageSeperatorText = "ACCESS MAIN PROGRAM GRID";//text used to temporarily represent were new page prompts will be inserted

    /**
     *
     */
    public RunProgram()
    {
        System.out.println("\n...starting main...");
        String pdfLocation = "static/in.pdf";
        pdfParser = new ParsePdf(pdfLocation);
        readPdfAndWriteOutputToFile(pdfLocation,"/static/out/out.txt");
        new Thread(new ConvertOutTextToLatex()).start();
    }

    public RunProgram(String pdfToReadLocation, String pathToOutputTextFile)
    {
        System.out.println("\n...starting main...");
        //"static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf"
        //"static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt"

        String outputLatexPath = replaceFilePathExtensionWithLatexFileExtension(pathToOutputTextFile);
        pdfParser = new ParsePdf(pdfToReadLocation);

        readPdfAndWriteOutputToFile(pdfToReadLocation,pathToOutputTextFile);

        ConvertOutTextToLatex.setParameters(pathToOutputTextFile, outputLatexPath);

        new Thread(new ConvertOutTextToLatex()).start();
    }

    public static String replaceFilePathExtensionWithLatexFileExtension(String inputFilePath)
    {
       //remove after the last dot
        if (inputFilePath == null || inputFilePath.length() <= 0 ) return null;
        int endIndex = inputFilePath.lastIndexOf(".");
        if (endIndex == -1) return null;
        String asLatex = inputFilePath.substring(0, endIndex);
        //add .tex
        asLatex = asLatex.concat(".tex");
        System.out.println("as latex: "+ asLatex);
        return asLatex;
    }

    /**
     * Reads the input pdf's contents (text and images),
     * and produces an output LaTeX document, which contains the read
     * text and images.
     * @param pdfToReadFromPath = the file path to the pdf that is being read from
     * @param fileToWriteToPath = the file path to the output file that will be produced
     */
    private void readPdfAndWriteOutputToFile(String pdfToReadFromPath, String fileToWriteToPath)
    {
        short numLinesToIgnore = 6;
        String contentsToWrite = pdfParser.getPdfText(pdfToReadFromPath, newPageSeperatorText);

        /*
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%\n Check #1: Is the extracted text correct?");
        System.out.println("check #1 results: \n [");
        System.out.println(contentsToWrite);
        System.out.println("\n]\n");
         */

        String[] textToAddImgs = pdfParser.getPdfImageAddingText(pdfToReadFromPath);
        /*
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%\n Check #2: Is the extracted image adding text correct?");
        System.out.println("check #2 results: \n [");
        int pagenum = 0;
        for(String pageText : textToAddImgs){
            pagenum++;
            System.out.println("\npage "+pagenum+")\n"+contentsToWrite);
        }
        System.out.println("\n]\n");
         */

        LatexifyOptionalArguments optionalArgs = new LatexifyOptionalArguments();

        optionalArgs.setNumStartLinesToRemoveForEachPage(numLinesToIgnore);
        optionalArgs.setQuizMode(true);
        String latexifiedContents = Latexify.convertTextToLatex(contentsToWrite, textToAddImgs, newPageSeperatorText, optionalArgs);

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%\n Check #3: Is the converted LaTeX text correct?");
        System.out.println("check #3 results: \n [");
        System.out.println(latexifiedContents);
        System.out.println("\n]\n");

        WriteTextToFileHandle fileWriter = new WriteTextToFileHandle(fileToWriteToPath, latexifiedContents);

        fileWriter.writeDataToFile();
    }
}