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
        pagesProcessed = 0;
        Iterator<PDPage> iteratorOfPages = documentToRead.getPages().iterator();
        numPages = documentToRead.getNumberOfPages();

        imageAddingTextForEachPage = processAllPagesAndOutputStatusWhilstDoingSo(imageAddingTextForEachPage, iteratorOfPages, outputFolderLocation);

        return imageAddingTextForEachPage.toArray(new String[0]);
    }

    private ArrayList<String> processAllPagesAndOutputStatusWhilstDoingSo(ArrayList<String> imageAddingTextForEachPage, Iterator<PDPage> iteratorOfPages, String outputFolderLocation)
    {
        ImageLoadingStatusShower statusShower =new ImageLoadingStatusShower(this);

        //start status shower
        Thread statusThread = new Thread(statusShower);
        statusThread.start();
        imageAddingTextForEachPage = processAllPages(iteratorOfPages, imageAddingTextForEachPage, outputFolderLocation);
        statusShower.finishProcess();
        try {
            statusThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return imageAddingTextForEachPage;
    }

    private ArrayList<String> processAllPages(Iterator<PDPage> iteratorOfPages, ArrayList<String> imageAddingTextForEachPage, String outputFolderLocation)
    {
        int pageNum = 0;
        System.out.println("Loading images ...");
        while (iteratorOfPages.hasNext()){
            allPagesProcessingOutput pagesProcessingOutput = processAllImagesInPage(iteratorOfPages, imageAddingTextForEachPage, pageNum, outputFolderLocation);
            imageAddingTextForEachPage = pagesProcessingOutput.imageAddingTextForEachPage();
            pagesProcessed = pagesProcessingOutput.pagesProcessed();
            pageNum = pagesProcessingOutput.pageNum();
        }
        return imageAddingTextForEachPage;
    }

    private allPagesProcessingOutput processAllImagesInPage(Iterator<PDPage> iteratorOfPages, ArrayList<String> imageAddingTextForEachPage, int pageNum, String outputFolderLocation)
    {
        pageNum++;
        pagesProcessed++;
        PDPage singlePage = iteratorOfPages.next();
        imageAddingTextForEachPage = getImagesFromPage(singlePage, pageNum, imageAddingTextForEachPage, outputFolderLocation);
        return new allPagesProcessingOutput(imageAddingTextForEachPage, pagesProcessed, pageNum);
    }

    private record allPagesProcessingOutput(ArrayList<String> imageAddingTextForEachPage, int pagesProcessed, int pageNum){}

    private ArrayList<String> getImagesFromPage(PDPage page, int pageNumber, ArrayList<String> textToAddTheImages, String outputFolderLocation)
    {
        String userDir = System.getProperty("user.dir");
        String outputDir = userDir.concat("/"+outputFolderLocation);
        //create the output directory
        new File(outputDir).mkdirs();
        //
        StringBuilder textToAddImgsForThisPage = new StringBuilder();

        PDResources pdResources = page.getResources();
        int i = 1;
        Iterable<org.apache.pdfbox.cos.COSName> cosNames =pdResources.getXObjectNames();
        for (COSName cosName : cosNames) {
            try {
                imageExtractOut extractOut = extractImage(textToAddImgsForThisPage, outputDir, i, pdResources, cosName, pageNumber);
                textToAddImgsForThisPage = extractOut.textToAddTheImages();
                i = extractOut.i();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /////
        System.out.println("------------text to add images:!!!!!!!!!!!!!!!!!");
        for(String tex : textToAddTheImages){
            System.out.println("    -"+tex);
        }

        /*
        Instead of adding so that the index correlates to the page,
        each index appears to be assigned to a single image.

        check how these images are applied (and if it is by index)
        jump

        yeah, it is accessed by index, so each entry needs to have all the text for one page...
         */
        //////////
        //now add that string to text to add images
        textToAddTheImages.add(textToAddImgsForThisPage.toString());

        return textToAddTheImages;
    }

    private imageExtractOut extractImage(StringBuilder textToAddImgsForThisPage, String outputDir, int i, PDResources pdResources, COSName cosName, int pageNumber) throws IOException {
        PDXObject o = pdResources.getXObject(cosName);
        if(!(o instanceof  PDImageXObject)) return new imageExtractOut(textToAddImgsForThisPage, i);

        String nameOfImgFile = "page".concat(String.valueOf(pageNumber).concat("-image-").concat(String.valueOf(i)).concat(".png"));
        makeImageFileContainingExtractedImage(outputDir, o, nameOfImgFile);
        textToAddImgsForThisPage = addNewImageAddingTextToOverallImageAddingList(textToAddImgsForThisPage, nameOfImgFile);
        i++;

        return new imageExtractOut(textToAddImgsForThisPage, i);
    }

    private void makeImageFileContainingExtractedImage(String outputDir, PDXObject o, String nameOfImgFile) throws IOException {
        PDImageXObject image = (PDImageXObject) o;
        String filename = outputDir.concat(nameOfImgFile);
        ImageIO.write(image.getImage(), "png", new File(filename));
    }

    private StringBuilder addNewImageAddingTextToOverallImageAddingList(StringBuilder textToAddTheImages, String nameOfImgFile)
    {
        StringBuilder textToAddTheImage = addTextToAddImageOntoLatexDocument(nameOfImgFile);
        textToAddTheImages.append(textToAddTheImage.toString());
        return textToAddTheImages;
    }

    private StringBuilder addTextToAddImageOntoLatexDocument(String nameOfImgFile)
    {
        StringBuilder textToAddTheImage = new StringBuilder();
        textToAddTheImage.append("\\begin{figure}[H]\n");
        textToAddTheImage.append("\\includegraphics[width=0.5\\linewidth]{"+nameOfImgFile+"}\n");
        textToAddTheImage.append("\\end{figure}\n");
        return textToAddTheImage;
    }

    private record imageExtractOut(StringBuilder textToAddTheImages, int i){}

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
