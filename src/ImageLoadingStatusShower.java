public class ImageLoadingStatusShower implements Runnable{
    ParsePdf pdfParserToReportOn;
    private boolean finishedAll = false;

    public ImageLoadingStatusShower(ParsePdf parentPdfParser)
    {
        this.pdfParserToReportOn = parentPdfParser;
    }

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
        System.out.println("loaded all images");


    }
}
