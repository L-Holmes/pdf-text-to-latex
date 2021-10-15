package main.pdf_parsing;

import main.pdf_parsing.ParsePdf;

/**
 * Creates an output, detailing how much progress has been made by the process (as a percentage)
 * specifically reports on a pdf parser loading images, and reports a percentage relating to the number of
 * pages that have been processed.
 */
public class ImageLoadingStatusShower implements Runnable{
    PdfImageExtractor pdfParserToReportOn;       //the parser, that is fetching images from a pdf, whose status is being reported on
    private boolean finishedAll = false;//true if the process being reported on has finished its execution

    /**
     * @param parentPdfParser = the parser, that is fetching images from a pdf, whose status is being reported on
     */
    public ImageLoadingStatusShower(PdfImageExtractor parentPdfParser)
    {
        this.pdfParserToReportOn = parentPdfParser;
    }

    /**
     * Forces the running status output to stop,
     * as the process it is reporting on is presumed to have finished
     */
    public void finishProcess()
    {
        finishedAll = true;
    }

    @Override
    public void run() {
        int prevReturnedPercentage = 0;
        while(prevReturnedPercentage < 100 && !finishedAll)
        {
            if(pdfParserToReportOn.getImageLoadingPercentage()!= prevReturnedPercentage){
                prevReturnedPercentage = pdfParserToReportOn.getImageLoadingPercentage();
                if(prevReturnedPercentage < 100) System.out.println("~"+prevReturnedPercentage+"% of images loaded");
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("100% of images loaded");
    }
}
