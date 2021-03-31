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

        ParsePdfOut pdfParseOutput = pdfParser.getPdfText(pdfToReadFromPath, newPageSeperatorText);
        String contentsToWrite = pdfParseOutput.outText();
        String[] textToAddImgs = pdfParseOutput.imgAddingForEachPage();
        LatexifyOptionalArguments optionalArgs = new LatexifyOptionalArguments();
        optionalArgs.setNumStartLinesToRemoveForEachPage(numLinesToIgnore);
        optionalArgs.setQuizMode(true);
        String latexifiedContents = Latexify.convertTextToLatex(contentsToWrite, textToAddImgs, newPageSeperatorText, optionalArgs);
        WriteTextToFileHandle fileWriter = new WriteTextToFileHandle(fileToWriteToPath, latexifiedContents);
        fileWriter.writeDataToFile();
    }
}