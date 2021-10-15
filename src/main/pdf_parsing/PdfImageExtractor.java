package main.pdf_parsing;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PdfImageExtractor {
    //image loading info
    private int numPages = 0;
    private int pagesProcessed = 0;

    /**
     * gets the LaTex text used to add an image to a page (for all pages),
     * with each index in the array corresponding to the page number from the input pdf.
     * @param documentToRead the pdf document that is having the images read from all of its pages
     * @return the LaTeX text used to add images to every pages (ordered by page index)
     */
    public String[] retrievePdfImageAddingTextForAllPages(PDDocument documentToRead, String outputFolderLocation)
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
            imageAddingTextForEachPage = getImagesFromPage(singlePage, pageNum, imageAddingTextForEachPage, outputFolderLocation);
        }
        statusShower.finishProcess();
        try {
            statusThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return imageAddingTextForEachPage.toArray(new String[0]);
    }

    private ArrayList<String> getImagesFromPage(PDPage page, int pageNumber, ArrayList<String> textToAddTheImages, String outputFolderLocation)
    {
        String userDir = System.getProperty("user.dir");
        String outputDir = userDir.concat("/"+outputFolderLocation);
        //create the output directory
        new File(outputDir).mkdirs();
        //

        PDResources pdResources = page.getResources();
        int i = 1;
        Iterable<org.apache.pdfbox.cos.COSName> cosNames =pdResources.getXObjectNames();
        for (COSName cosName : cosNames) {
            try {
                PDXObject o = pdResources.getXObject(cosName);
                if (o instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) o;
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
