package converting_text_to_latex_equivalent.conversion_tools;

public class LatexifyRegularLineChecker {
    //optional arguments
    private short linesToRemoveCount = 0;//used to keep-track of how many of the following lines of text must be ignored/removed
    private short numStartLinesToRemoveForEachPage = 0;// the number of lines to remove/ignore, when processing the text for each page of the pdf text input

    /**
     * performs:
     * -general line adjustments
     * -new page related checks
     * -the removement of any lines from the start of each page (if requied)
     * @param line
     * @param newPageSeperator
     * @param prevLineWasPageBreak
     * @param previousBulletStyle
     * @param textBuffer
     * @param textToAddImages
     * @return
     */
     public RegularLineCheckOut performRegularLineChecks(String line, String newPageSeperator, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder textBuffer, String[] textToAddImages)
    {
        //performing new page related checks
        LatexifyNewPageAdjustor newPageAdjustor = new LatexifyNewPageAdjustor();
        NewPageAdjustorOut newpageAdjustOut = newPageAdjustor.adjustAnyNewpages(line, newPageSeperator, prevLineWasPageBreak, previousBulletStyle, textBuffer, linesToRemoveCount, textToAddImages);
        line = newpageAdjustOut.line();
        prevLineWasPageBreak = newpageAdjustOut.prevLineWasPageBreak();
        previousBulletStyle = newpageAdjustOut.previousBulletStyle();
        textBuffer = newpageAdjustOut.textBuilder();
        linesToRemoveCount = newpageAdjustOut.lineToRemoveCount();

        if (linesToRemoveCount >0){
            //if the user has chosen to ignore the first 'x' lines of each page, here, they are ignored (i.e. not added to the output text)
            if (linesToRemoveCount == numStartLinesToRemoveForEachPage){
                //add the first line
                textBuffer.append(line);
                textBuffer.append("\\\\ \n");
            }
            linesToRemoveCount--;
        }
        else{
            //perform regular line adjustments
            LatexifyDocumentLineGetter documentLineGetter = new LatexifyDocumentLineGetter();
            DocumentLineOut regLineOutput = documentLineGetter.addDocLine(prevLineWasPageBreak, line, textBuffer, previousBulletStyle);
            textBuffer = regLineOutput.textBuilder();
            prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
            previousBulletStyle = regLineOutput.prevBulletStyle();
        }
        return new RegularLineCheckOut(line, textBuffer, prevLineWasPageBreak, previousBulletStyle);
    }
}
