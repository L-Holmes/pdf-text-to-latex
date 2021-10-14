package main.pdf_parsing;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PdfTextExtractor {
    /**
     * Retrieves the text from all of the pages within the pdf file
     * @param documentToRead = the pdf document, that is to have its contents read.
     * @param newPageSeperator = symbol/text used to represent where the current page would end (and the next one would begin)
     */
    public String getPdfText(PDDocument documentToRead, String newPageSeperator)
    {
        String[] imgAddingTextForEachPage = new String[0]; //text used specifically to add images to each (and every) page
        StringBuilder outText = new StringBuilder(); //text from all of the pdf pages
        try {
            //get the tools used to extract the text


            outText = getTextFromAllPages(outText, documentToRead, newPageSeperator);
            documentToRead.close();
        } catch (IOException e) {
            //file may be empty
            e.printStackTrace();
        }
        return outText.toString();
    }

    private StringBuilder getTextFromAllPages(StringBuilder allPagesText, PDDocument documentToRead, String newPageSeperator) throws IOException {
        Iterator<PDDocument> pageIterator = getPdfPageIterator(documentToRead);
        PDFTextStripper textStripper = new PDFTextStripper();

        //get the text from the page
        while(pageIterator.hasNext()){
            allPagesText = addPageTextToTextFetchedSoFar(allPagesText, pageIterator, textStripper, newPageSeperator);
        }
        return allPagesText;
    }

    private Iterator<PDDocument> getPdfPageIterator( PDDocument documentToRead) throws IOException {
        Splitter pageSplitter  = new Splitter();
        List<PDDocument> pagesOfPdf = pageSplitter.split(documentToRead);
        Iterator<PDDocument> pageIterator = pagesOfPdf.listIterator();
        return pageIterator;
    }



    private StringBuilder addPageTextToTextFetchedSoFar(StringBuilder textFetchedSoFar, Iterator<PDDocument> pageIterator, PDFTextStripper textStripper, String newPageSeperator) throws IOException {
        //
        String extractedPageText = getTextFromPdfPage(pageIterator, textStripper);
        textFetchedSoFar.append(extractedPageText);

        //adding the end of page marker
        textFetchedSoFar.append(newPageSeperator);

        return textFetchedSoFar;
    }

    private String getTextFromPdfPage(Iterator<PDDocument> pageIterator,PDFTextStripper textStripper) throws IOException {
        //get the page that we are up to
        PDDocument page = pageIterator.next();

        //getting the text
        String extractedPageText = textStripper.getText(page);

        page.close();
        return extractedPageText;
    }




}
