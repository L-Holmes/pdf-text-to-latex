import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        while (iteratorOfPages.hasNext()){
            pageNum++;
            PDPage singlePage = iteratorOfPages.next();
            imageAddingTextForEachPage = getImagesFromPage(singlePage, pageNum, imageAddingTextForEachPage);
        }
        return imageAddingTextForEachPage.toArray(new String[0]);
    }

    private ArrayList<String> getImagesFromPage(PDPage page, int pageNumber, ArrayList<String> textToAddTheImages)
    {
        PDResources pdResources = page.getResources();
        int i = 1;
        for (COSName cosName : pdResources.getXObjectNames()) {
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
                    textToAddTheImage.append("\\begin{figure}\n");
                    textToAddTheImage.append("\\includegraphics[width=\\linewidth]{"+nameOfImgFile+"}\n");
                    textToAddTheImage.append("\\end{figure}\n");
                    textToAddTheImages.add(textToAddTheImage.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return textToAddTheImages;
    }
}

