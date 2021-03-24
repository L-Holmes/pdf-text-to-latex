class Main
{
    public static void main(String[] args)
    {
        System.out.println("\n...starting main...");
        String pdfLocation = "static/in.pdf";
        ParsePdf pdfParser = new ParsePdf(pdfLocation);
    }
}