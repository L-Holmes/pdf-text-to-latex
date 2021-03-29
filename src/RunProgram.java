import util.WriteTextToFileHandle;

public class RunProgram {

    ParsePdf pdfParser;
    public RunProgram()
    {
        System.out.println("\n...starting main...");
        String pdfLocation = "static/in.pdf";
        pdfParser = new ParsePdf(pdfLocation);
        readPdfAndWriteOutputToFile(pdfLocation,"/static/out/out.txt");
        new Thread(new ConvertOutTextToLatex()).start();
    }

    private void readPdfAndWriteOutputToFile(String pdfToReadFromPath, String fileToWriteToPath)
    {
        String newPageSeperatorText = "ACCESS MAIN PROGRAM GRID";
        ParsePdfOut pdfParseOutput = pdfParser.getPdfText(pdfToReadFromPath, newPageSeperatorText);
        String contentsToWrite = pdfParseOutput.outText();
        String[] textToAddImgs = pdfParseOutput.imgAddingForEachPage();
        short numLinesToIgnore = 6;
        LatexifyOptionalArguments optionalArgs = new LatexifyOptionalArguments();
        optionalArgs.setNumStartLinesToRemoveForEachPage(numLinesToIgnore);
        optionalArgs.setQuizMode(true);
        String latexifiedContents = Latexify.convertTextToLatex(contentsToWrite, textToAddImgs, newPageSeperatorText, optionalArgs);
        WriteTextToFileHandle fileWriter = new WriteTextToFileHandle(fileToWriteToPath, latexifiedContents);
        System.out.println("writing to system out...... -->");
        fileWriter.writeDataToFile();
    }
}