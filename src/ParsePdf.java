import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import util.WriteTextToFileHandle;

public class ParsePdf {
    public ParsePdf(String pdfLocation){
        System.out.println("parsing the pdf: "+pdfLocation);
    }

    /**
     * @param fileToRead = the filepath to the pdf, that is to have its contents read.
     */
    public String getPdfText(String fileToRead)
    {
        StringBuilder outText = new StringBuilder();
        try {
            File inputFile = new File(fileToRead);
            PDDocument documentToRead = PDDocument.load(inputFile);
            PDFTextStripper textStripper = new PDFTextStripper();
            Splitter pageSplitter  = new Splitter();
            List<PDDocument> pagesOfPdf = pageSplitter.split(documentToRead);
            Iterator<PDDocument> pageIterator = pagesOfPdf.listIterator();
            while(pageIterator.hasNext()){
                PDDocument page = pageIterator.next();

                String extractedPageText = textStripper.getText(page);
                outText.append(extractedPageText);
                outText.append("<<<new page>>>");
                page.close();
            }
            documentToRead.close();
        } catch (IOException e) {
            System.out.println("could not load the file to read: '"+fileToRead+"'");
            //file may be empty
            e.printStackTrace();
        }
        return outText.toString();
    }

}
