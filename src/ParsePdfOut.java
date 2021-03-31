/**
 * The output for the pdf parser.
 * @param outText = the text that has been read from the pdf
 * @param imgAddingForEachPage = the LaTex text used to add an image, with each index in the array corresponding to the page number from the input pdf.
 */
public record ParsePdfOut(String outText, String[] imgAddingForEachPage) {
}
