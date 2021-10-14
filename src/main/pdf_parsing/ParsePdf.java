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
    //image loading info
    private int numPages = 0;
    private int pagesProcessed = 0;
    //

    public ParsePdf(String pdfLocation){
    }

    /**
     * Retrieves the text from all of the pages within the pdf file
     * @param filePathOfFileBeingRead = the filepath to the pdf, that is to have its contents read.
     * @param newPageSeperator = symbol/text used to represent where the current page would end (and the next one would begin)
     */
    public String getPdfText(String filePathOfFileBeingRead, String newPageSeperator){
        //
        PdfTextExtractor textExtractor = new PdfTextExtractor();

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

    public String[] getPdfImageAddingText(String filePathOfFileBeingRead){
        try {
            PDDocument documentToRead = getPdfDocumentFromFilePath(filePathOfFileBeingRead);
            return retrievePdfImageAddingTextForAllPages(documentToRead);

        } catch (IOException e) {
            System.out.println("could not load the file to read: '"+filePathOfFileBeingRead+"'");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * gets the LaTex text used to add an image to a page (for all pages),
     * with each index in the array corresponding to the page number from the input pdf.
     * @param documentToRead the pdf document that is having the images read from all of its pages
     * @return the LaTeX text used to add images to every pages (ordered by page index)
     */
    private String[] retrievePdfImageAddingTextForAllPages(PDDocument documentToRead)
    {
        ArrayList<String> imageAddingTextForEachPage = new ArrayList<String>();
        //getting the images
        Iterator<PDPage> iteratorOfPages = documentToRead.getPages().iterator();
        int pageNum = 0;
        numPages = documentToRead.getNumberOfPages();
        pagesProcessed = 0;
        //start status shower
        ImageLoadingStatusShower statusShower =new ImageLoadingStatusShower(this);
        Thread statusThread = new Thread(statusShower);
        statusThread.start();

        System.out.println("Loading images ...");

        while (iteratorOfPages.hasNext()){
            pageNum++;
            pagesProcessed++;
            PDPage singlePage = iteratorOfPages.next();
            imageAddingTextForEachPage = getImagesFromPage(singlePage, pageNum, imageAddingTextForEachPage);
        }
        statusShower.finishProcess();
        try {
            statusThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return imageAddingTextForEachPage.toArray(new String[0]);
    }

    private ArrayList<String> getImagesFromPage(PDPage page, int pageNumber, ArrayList<String> textToAddTheImages)
    {
        PDResources pdResources = page.getResources();
        int i = 1;
        Iterable<org.apache.pdfbox.cos.COSName> cosNames =pdResources.getXObjectNames();
        for (COSName cosName : cosNames) {
            try {
                PDXObject o = pdResources.getXObject(cosName);
                if (o instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) o;
                    String userDir = System.getProperty("user.dir");
                    String outputDir = userDir.concat("/static/out/images/");
                    String nameOfImgFile = "page".concat(String.valueOf(pageNumber).concat("-image-").concat(String.valueOf(i)).concat(".png"));
                    String filename = outputDir.concat(nameOfImgFile);
                    ImageIO.write(image.getImage(), "png", new File(filename));
                    i++;
                    //add the text that adds the image into the latex document
                    StringBuilder textToAddTheImage = new StringBuilder();
                    textToAddTheImage.append("\\begin{figure}[H]\n");
                    textToAddTheImage.append("\\includegraphics[width=0.5\\linewidth]{"+nameOfImgFile+"}\n");
                    textToAddTheImage.append("\\end{figure}\n");
                    textToAddTheImages.add(textToAddTheImage.toString());
                    
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return textToAddTheImages;
    }

    public synchronized int getImageLoadingPercentage()
    {
        //System.out.println("returning images processed:"+pagesProcessed+" num pages" + numPages);
        return (int) (((float) pagesProcessed/(float)numPages)*100f);
    }

    private int getLenOfIterable(Iterable<?> iterableToCheckLenOf)
    {
        if (iterableToCheckLenOf instanceof Collection) {
            return ((Collection<?>) iterableToCheckLenOf).size();
        } else {
            int count = 0;
            Iterator iterator = iterableToCheckLenOf.iterator();
            while(iterator.hasNext()) {
                iterator.next();
                count++;
            }
            return count;
        }
    }


}

