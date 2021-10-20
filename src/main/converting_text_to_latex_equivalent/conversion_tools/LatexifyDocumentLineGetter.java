package main.converting_text_to_latex_equivalent.conversion_tools;

import main.converting_text_to_latex_equivalent.Latexify;
import main.util.Bullet_point_hub;

public class LatexifyDocumentLineGetter {
    LatexifyBulletPointHandler bulletPointHandler;
    //
    private final String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";//the text that LaTeX will interpret as a 'new page prompt'


    public LatexifyDocumentLineGetter(LatexifyBulletPointHandler bulletPointHandler){
        this.bulletPointHandler = bulletPointHandler;
    }

    /**
     * This function converts the common aspects of the pdf into latex indlucing:
     * -regular text
     * -newpages
     * -titles / section headings
     * -bullet points
     * @param prevLineWasPageBreak = true if the line that was previously processed contained a page-break; false otherwise
     * @param line
     * @param textBuilder = The structure that is used to store the text that has been Latexified so far
     * @return
     */
    public DocumentLineOut addDocLine(boolean prevLineWasPageBreak, String line, StringBuilder textBuilder, String previousBulletStyle)
    {
        if(isProbablyATitleHeadingLine(line, prevLineWasPageBreak)) return handleHeadingLine(line, textBuilder, previousBulletStyle);
        else return handleRegularLine(line, textBuilder, previousBulletStyle, prevLineWasPageBreak);
    }

    protected DocumentLineOut handleHeadingLine(String line, StringBuilder textBuilder, String previousBulletStyle)
    {
        textBuilder = makeLineALatexHeading(line, textBuilder);
        boolean prevLineWasPageBreak = false;
        return new DocumentLineOut(textBuilder, prevLineWasPageBreak, previousBulletStyle);
    }

    private DocumentLineOut handleRegularLine(String line, StringBuilder textBuilder, String previousBulletStyle, boolean prevLineWasPageBreak)
    {
        boolean addNewline = true;

        //converting bullet points into latex bullet points

        String bulletPointTypeUsed = getTheBulletPointTypeUsed(line);
        if(bulletPointTypeUsed != null){
            BulletPointOperationOut bulletOperationOut = bulletPointHandler.handleBulletPointOperation(line, bulletPointTypeUsed, previousBulletStyle);
            line = bulletOperationOut.modifiedLine();
            previousBulletStyle = bulletOperationOut.previousBulletStyle();
            addNewline = false;
        }
        else{
            //if the previous line had bullet points,they need to be ended.
            BulletPointOperationOut endBulletPointsOut = endPreviousBulletPoints(line, previousBulletStyle, bulletPointHandler);
            line = endBulletPointsOut.modifiedLine();
            previousBulletStyle = endBulletPointsOut.previousBulletStyle();

        }

        //adding a regular line to a page
        textBuilder = addRegularLine(textBuilder, line, addNewline);

        return new DocumentLineOut(textBuilder, prevLineWasPageBreak, previousBulletStyle);
    }

    private BulletPointOperationOut endPreviousBulletPoints(String line, String previousBulletStyle, LatexifyBulletPointHandler bulletPointHandler)
    {
        if (previousBulletStyle != "") {
            //since there is no bullet points now, ensure that the previous bullet points have been fully emptied

            String endingText = "";
            while (bulletPointHandler.hasNestedBulletsThatHaveNotBeenEnded()) {
                bulletPointHandler.removeTopNestedBulletPoint();
                endingText = endingText.concat("\\end{itemize}\n");
            }
            line = endingText.concat(line);

        }
        previousBulletStyle = "";
        return new BulletPointOperationOut(line, previousBulletStyle);
    }

    private boolean isProbablyATitleHeadingLine(String line, boolean prevLineWasPageBreak)
    {
        if(prevLineWasPageBreak && !line.contains(TEXT_TO_ADD_NEW_PAGE_IN_LATEX) && !line.isBlank()) return true;
        return false;
    }

    protected StringBuilder makeLineALatexHeading(String line, StringBuilder textBuilder)
    {
        textBuilder.append("\\section{");
        textBuilder.append(line);
        textBuilder.append("}\n");
        return textBuilder;
    }

    private String getTheBulletPointTypeUsed(String line) {
        String[] allBulletPointStyle = Bullet_point_hub.getAllBulletPoints();
        for(String bulletPointStyle : allBulletPointStyle){
            if(containsBulletPoint(line, bulletPointStyle)) return bulletPointStyle;
        }
        return null;
    }

    private boolean containsBulletPoint(String inputText, String bulletPointBeingSearchedFor){
        String inputTextWithoutStartWhitespace = inputText.replaceFirst("^\\s*", "");
        int numCharactersToSearchFromStart = Math.max(2, bulletPointBeingSearchedFor.length());
        if (inputTextWithoutStartWhitespace.length() > numCharactersToSearchFromStart) {
            //if the bullet point is at the start of the text
            if (inputTextWithoutStartWhitespace.substring(0, numCharactersToSearchFromStart).contains(bulletPointBeingSearchedFor)) return true;
        }
        return false;
    }

    private StringBuilder addRegularLine(StringBuilder textBuilder, String line, boolean addNewline)
    {
        textBuilder.append(line);
        if(addNewline){
            textBuilder.append("\\\\");
        }
        textBuilder.append("\n");
        return textBuilder;
    }
}
