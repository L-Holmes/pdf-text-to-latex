import util.WriteTextToFileHandle;

public class RunProgram {

    ParsePdf pdfParser;
    public RunProgram()
    {
        //
        System.out.println("\n...starting main...");
        String pdfLocation = "static/in.pdf";
        pdfParser = new ParsePdf(pdfLocation);
        readPdfAndWriteOutputToFile(pdfLocation,"/static/out.txt");
        new Thread(new ConvertOutTextToLatex()).start();
    }


    private void readPdfAndWriteOutputToFile(String pdfToReadFromPath, String fileToWriteToPath)
    {
        String contentsToWrite = pdfParser.getPdfText(pdfToReadFromPath);
        String latexifiedContents = Latexify.convertTextToLatex(contentsToWrite, "<<<new page>>>");
        WriteTextToFileHandle fileWriter = new WriteTextToFileHandle(fileToWriteToPath, latexifiedContents);
        System.out.println("writing to system out...... -->");
        fileWriter.writeDataToFile();
    }
}