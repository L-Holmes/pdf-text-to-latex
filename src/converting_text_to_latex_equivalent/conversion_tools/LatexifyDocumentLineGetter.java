package converting_text_to_latex_equivalent.conversion_tools;

import converting_text_to_latex_equivalent.Latexify;

public class LatexifyDocumentLineGetter {
    //
    private final String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";//the text that LaTeX will interpret as a 'new page prompt'

    /**
     * This function converts the common aspects of the pdf into latex indlucing:
     * -regular text
     * -newpagse
     * -titles / section headings
     * -bullet points
     * @param prevLineWasPageBreak = true if the line that was previously processed contained a page-break; false otherwise
     * @param line
     * @param textBuilder = The structure that is used to store the text that has been Latexified so far
     * @return
     */
    public DocumentLineOut addDocLine(boolean prevLineWasPageBreak, String line, StringBuilder textBuilder, String previousBulletStyle)
    {
        boolean addNewline = true;
        //adding the title to a page
        if(prevLineWasPageBreak && !line.contains(TEXT_TO_ADD_NEW_PAGE_IN_LATEX) && !line.isBlank()){
            textBuilder.append("\\section{");
            textBuilder.append(line);
            textBuilder.append("}\n");
            prevLineWasPageBreak = false;
        }
        else{
            //converting bullet points into latex bullet points
            LatexifyBulletPointHandler bulletPointHandler = new LatexifyBulletPointHandler();
            if (line.contains("• ")){
                BulletPointOperationOut bulletOperationOut = bulletPointHandler.handleBulletPointOperation(line, "• ", previousBulletStyle);
                line = bulletOperationOut.modifiedLine();
                previousBulletStyle = bulletOperationOut.previousBulletStyle();
                addNewline = false;
            }
            else if(line.length() > 2){
                if (line.substring(0, 2).contains("– ")){
                    BulletPointOperationOut bulletOperationOut = bulletPointHandler.handleBulletPointOperation(line, "– ", previousBulletStyle);
                    line = bulletOperationOut.modifiedLine();
                    previousBulletStyle = bulletOperationOut.previousBulletStyle();
                    addNewline = false;
                }
            }
            else{
                if (previousBulletStyle != ""){
                    line = "\\end{itemize}\n".concat(line);
                }
                previousBulletStyle = "";
            }
            //adding a regular line to a page
            textBuilder.append(line);
            if(addNewline){
                textBuilder.append("\\\\");
            }
            textBuilder.append("\n");
        }
        return new DocumentLineOut(textBuilder, prevLineWasPageBreak, previousBulletStyle);
    }
}
