import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import util.WriteTextToFileHandle;

import javax.imageio.ImageIO;

public class ParsePdf {
    //image loading info
    private int numPages = 0;
    private int pagesProcessed = 0;
    //

    public ParsePdf(String pdfLocation){
        System.out.println("parsing the pdf: "+pdfLocation);
    }

    /**
     * @param fileToRead = the filepath to the pdf, that is to have its contents read.
     */
    public ParsePdfOut getPdfText(String fileToRead, String newPageSeperator)
    {
        String[] imgAddingTextForEachPage = new String[0];
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

                //getting the text
                String extractedPageText = textStripper.getText(page);
                outText.append(extractedPageText);


                //adding the end of page marker
                outText.append(newPageSeperator);
                page.close();
            }
            imgAddingTextForEachPage = getAllPageImages(documentToRead);
            documentToRead.close();
        } catch (IOException e) {
            System.out.println("could not load the file to read: '"+fileToRead+"'");
            //file may be empty
            e.printStackTrace();
        }
        return new ParsePdfOut(outText.toString(), imgAddingTextForEachPage);
    }



    private String[] getAllPageImages(PDDocument documentToRead)
    {
        ArrayList<String> imageAddingTextForEachPage = new ArrayList<String>();
        //getting the images
        Iterator<PDPage> iteratorOfPages = documentToRead.getPages().iterator();
        int pageNum = 0;
        numPages = getLenOfIterator(iteratorOfPages);
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
        System.out.println("processing a page..........");
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

    private int getLenOfIterator(Iterator<?> iteratorToCheckLenOf)
    {
        int count = 0;
        while(iteratorToCheckLenOf.hasNext()) {
            iteratorToCheckLenOf.next();
            count++;
        }
        return count;
    }
}

