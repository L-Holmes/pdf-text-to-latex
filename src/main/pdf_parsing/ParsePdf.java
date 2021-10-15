package main.pdf_parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import main.util.WriteTextToFileHandle;

import javax.imageio.ImageIO;

public class ParsePdf {

    //
    private PdfTextExtractor textExtractor = new PdfTextExtractor();
    private PdfImageExtractor imageExtractor = new PdfImageExtractor();


    public ParsePdf(){
    }

    /**
     * Retrieves the text from all of the pages within the pdf file
     * @param filePathOfFileBeingRead = the filepath to the pdf, that is to have its contents read.
     * @param newPageSeperator = symbol/text used to represent where the current page would end (and the next one would begin)
     */
    public String getPdfText(String filePathOfFileBeingRead, String newPageSeperator){
        //

        try {
            PDDocument documentToRead = getPdfDocumentFromFilePath(filePathOfFileBeingRead);
            return textExtractor.getPdfText(documentToRead, newPageSeperator);

        } catch (IOException e) {
            System.out.println("could not load the file to read: '"+filePathOfFileBeingRead+"'");
            e.printStackTrace();
            return null;
        }
    }

    private PDDocument getPdfDocumentFromFilePath(String fileToReadFilePath) throws IOException {
        File inputFile = new File(fileToReadFilePath);
        PDDocument documentToRead = PDDocument.load(inputFile);
        return documentToRead;
    }

    public String[] getPdfImageAddingText(String filePathOfFileBeingRead, String outputFolderLocation){

        try {
            PDDocument documentToRead = getPdfDocumentFromFilePath(filePathOfFileBeingRead);
            String[] textToAddImages = imageExtractor.retrievePdfImageAddingTextForAllPages(documentToRead, outputFolderLocation);
            return textToAddImages;

        } catch (IOException e) {
            System.out.println("could not load the file to read: '"+filePathOfFileBeingRead+"'");
            e.printStackTrace();
            return null;
        }
    }

}

