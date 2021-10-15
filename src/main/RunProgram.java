package main;

import main.converting_text_to_latex_equivalent.Latexify;
import main.converting_text_to_latex_equivalent.LatexifyOptionalArguments;
import main.pdf_parsing.ParsePdf;
import main.util.ConvertOutTextToLatex;
import main.util.WriteTextToFileHandle;

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
        pdfParser = new ParsePdf();
        readPdfAndWriteOutputToFile(pdfLocation,"/static/out/out.txt");
        new Thread(new ConvertOutTextToLatex()).start();
    }

    public RunProgram(String pdfToReadLocation, String pathToOutputTextFile)
    {
        System.out.println("\n...starting main...");
        //"static/year3_lecture_slides/SCC361/in/SCC361-Wk1-L1.pdf"
        //"static/year3_lecture_slides/SCC361/out/SCC361-Wk1-L1-out.txt"

        String outputLatexPath = replaceFilePathExtensionWithLatexFileExtension(pathToOutputTextFile);
        pdfParser = new ParsePdf();

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
        return asLatex;
    }

    public static String replaceFileWithImagesFolder(String inputFilePath)
    {
        //remove after the last dot
        if (inputFilePath == null || inputFilePath.length() <= 0 ) return null;
        int endIndex = inputFilePath.lastIndexOf("/");
        if (endIndex == -1) return null;
        String asLatex = inputFilePath.substring(0, endIndex);
        //add .tex
        asLatex = asLatex.concat("/images/");
        return asLatex;
    }

    /**
     * Reads the input pdf's contents (text and images),
     * and produces an output LaTeX document, which contains the read
     * text and images.
     * @param pdfToReadFromPath = the file path to the pdf that is being read from
     * @param textFileToWriteToPath = the file path to the output text file that will be produced
     */
    private void readPdfAndWriteOutputToFile(String pdfToReadFromPath, String textFileToWriteToPath)
    {
        short numLinesToIgnore = 6;
        String contentsToWrite = pdfParser.getPdfText(pdfToReadFromPath, newPageSeperatorText);

        String imagesOutputFolder = replaceFileWithImagesFolder(textFileToWriteToPath);
        String[] textToAddImgs = pdfParser.getPdfImageAddingText(pdfToReadFromPath, imagesOutputFolder);

        LatexifyOptionalArguments optionalArgs = new LatexifyOptionalArguments();

        optionalArgs.setNumStartLinesToRemoveForEachPage(numLinesToIgnore);
        optionalArgs.setQuizMode(true);
        String latexifiedContents = Latexify.convertTextToLatex(contentsToWrite, textToAddImgs, newPageSeperatorText, optionalArgs);

        WriteTextToFileHandle fileWriter = new WriteTextToFileHandle(textFileToWriteToPath, latexifiedContents);

        fileWriter.writeDataToFile();
    }
}