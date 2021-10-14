package converting_text_to_latex_equivalent;

import converting_text_to_latex_equivalent.conversion_tools.*;

public class LatexifyDocumentBodyConvertor {
    //tools
    private LatexifyLinePatternChecker patternChecker = new LatexifyLinePatternChecker();
    private LatexifySpecialCharacterAdjustor characterAdjustor = new LatexifySpecialCharacterAdjustor();
    private LatexifyParagraphChecker paragraphChecker = new LatexifyParagraphChecker();
    private LatexifyRegularLineChecker regularLineChecker = new LatexifyRegularLineChecker();

    //-optional arguments-
    private String removePatternOnLine = "";//the sequence of characters that will be searched for on each line. The line will be removed if it contains that sequence
    private String removePatternOnPara = "";//the sequence of characters that will be searched for on each paragraph. The paragraph will be removed if it contains that sequence

    public LatexifyDocumentBodyConvertor(){}

    public LatexifyDocumentBodyConvertor(String removePatternOnLine, String removePatternOnPara){
        this.removePatternOnLine = removePatternOnLine;
        this.removePatternOnPara = removePatternOnPara;
    }

    /**
     * Used to convert the 'body' or 'document' (as LaTeX calls it) into a Latexified format
     * @param textBuilder = The structure that is used to store the text that has been Latexified so far (including the LaTex text before and after the 'document' body
     * @param textToAdd = the text which will be inserted into the 'document', after being Latexified
     * @param newPageSeperator = the temporary/placeholder text that is used to represent were a page-break prompt should be placed
     * @param textToAddImages = the text which contains LaTeX text, which is used to add images to the text (each entry corresponds to the page index from the input pdf, whose contents have been read)
     * @return the latexified document body
     */
    public StringBuilder getDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, String[] textToAddImages)
    {
        boolean prevLineWasPageBreak = false;
        String previousBulletStyle = "";

        StringBuilder paragraphBuffer = new StringBuilder();

        StringBuilder mainTextConstructor = getMainTextConstructor(textBuilder, paragraphBuffer);

        textBuilder = latexifyAllLines(textBuilder, textToAdd, newPageSeperator, textToAddImages, prevLineWasPageBreak, previousBulletStyle, mainTextConstructor, paragraphBuffer);
        return textBuilder;
    }

    private StringBuilder latexifyAllLines(StringBuilder textBuilder, String textToAdd, String newPageSeperator, String[] textToAddImages, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder mainTextConstructor, StringBuilder paragraphBuffer )
    {
        for (String line : textToAdd.split("\n")){
            byLineLatexificationOut thisLineLatexificationOut = performByLineLatexification(line, mainTextConstructor, textBuilder, paragraphBuffer, newPageSeperator, prevLineWasPageBreak, previousBulletStyle, textToAddImages);
            mainTextConstructor = thisLineLatexificationOut.mainTextConstructor();
            textBuilder = thisLineLatexificationOut.textBuilder();
            paragraphBuffer = thisLineLatexificationOut.paragraphBufferr();
            prevLineWasPageBreak = thisLineLatexificationOut.prevLineWasPageBreak();
            previousBulletStyle = thisLineLatexificationOut.previousBulletStyle();
        }
        return textBuilder;
    }

    private StringBuilder getMainTextConstructor(StringBuilder textBuilder, StringBuilder paragraphBuffer)
    {
        if(!removePatternOnPara.isBlank() && !removePatternOnLine.isBlank()){
            return paragraphBuffer;
        }
        else{
            return textBuilder;
        }
    }

    private byLineLatexificationOut performByLineLatexification(String line, StringBuilder mainTextConstructor, StringBuilder textBuilder, StringBuilder paragraphBuffer, String newPageSeperator, boolean prevLineWasPageBreak, String previousBulletStyle, String[] textToAddImages){
        if (patternChecker.lineContainsPattern(line, removePatternOnLine)){
            //TODO: do not add the line
        }

        //special character adjustments
        line = characterAdjustor.performSpecialCharacterAdjustments(line);

        //performing paragraph related checks
        if (!removePatternOnPara.isBlank() && !removePatternOnLine.isBlank()) {
            ParagraphSearchOut paragraphData = paragraphChecker.performParagraphChecks(line, paragraphBuffer, textBuilder, removePatternOnPara);
            line = paragraphData.firstLineOfNextParagraph();
            paragraphBuffer = paragraphData.paragraph();
            textBuilder = paragraphData.constructedOutText();
        }

        //perform regular line checks
        RegularLineCheckOut regLineOut = regularLineChecker.performRegularLineChecks(line, newPageSeperator, prevLineWasPageBreak, previousBulletStyle, mainTextConstructor, textToAddImages);
        mainTextConstructor = regLineOut.textBuffer();
        prevLineWasPageBreak = regLineOut.prevLineWasPageBreak();
        previousBulletStyle = regLineOut.prevBulletStyle();

        return new byLineLatexificationOut(mainTextConstructor, textBuilder, paragraphBuffer, prevLineWasPageBreak, previousBulletStyle);
    }

    private record byLineLatexificationOut(StringBuilder mainTextConstructor, StringBuilder textBuilder, StringBuilder paragraphBufferr, boolean prevLineWasPageBreak, String previousBulletStyle){}
}
