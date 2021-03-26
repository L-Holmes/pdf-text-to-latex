import java.util.Stack;

/**
 * Converts an input String that has been read from  a pdf into a latex-like format
 */
public class Latexify {
    private static Stack<String> nestedBulletPointStyleStack = new Stack<String>();
    private static String[] charactersToBackslash = {"<",">","_"};  //charcters which are interpreted as special/modifier characters in latex, and so must be prepended by a backslash when converted

    public static String convertTextToLatex(String textToConvert, String newPageSeperator)
    {
        StringBuilder asLatex  = new StringBuilder();
        //add required start of latex document
        asLatex.append("\\documentclass[12pt]{article}\n\n");
        asLatex.append("\\begin{document}\n");
        //add the body of the document (add the data parsed from the pdf)
        asLatex = addDocumentBody(asLatex, textToConvert, newPageSeperator);
        //add required end of document
        asLatex.append("\\end{document}");
        return asLatex.toString();
    }

    public static String convertTextToLatex(String textToConvert, String newPageSeperator, String removePatternOnLine, String removePatternOnPara)
    {
        StringBuilder asLatex  = new StringBuilder();
        //add required start of latex document
        asLatex.append("\\documentclass[12pt]{article}\n\n");
        asLatex.append("\\begin{document}\n");
        //add the body of the document (add the data parsed from the pdf)
        asLatex = addDocumentBody(asLatex, textToConvert, newPageSeperator, removePatternOnLine, removePatternOnPara);
        //add required end of document
        asLatex.append("\\end{document}");
        return asLatex.toString();
    }

    public static String convertTextToLatex(String textToConvert, String newPageSeperator, short numStartLinesToRemoveForEachPage)
    {
        StringBuilder asLatex  = new StringBuilder();
        //add required start of latex document
        asLatex.append("\\documentclass[12pt]{article}\n\n");
        asLatex.append("\\begin{document}\n");
        //add the body of the document (add the data parsed from the pdf)
        asLatex = addDocumentBody(asLatex, textToConvert, newPageSeperator, numStartLinesToRemoveForEachPage);
        //add required end of document
        asLatex.append("\\end{document}");
        return asLatex.toString();
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator)
    {
        boolean prevLineWasPageBreak = false;
        String previousBulletStyle = "";

        for (String line : textToAdd.split("\n")){

            if(line.contains(newPageSeperator)){
                line = line.replace(newPageSeperator, "\\newpage\n");
                prevLineWasPageBreak = true;
                if (previousBulletStyle != ""){
                    textBuilder.append("\\end{itemize}\n");
                    previousBulletStyle = "";
                }
            }
            //prepend a backslash to any special characters so that they are literally interpreted in the latex code
            for (String specialChar : charactersToBackslash){
                if (line.contains(specialChar)){
                    line = line.replace(specialChar, "\\".concat(specialChar));
                }
            }
            RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, textBuilder, previousBulletStyle);
            prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
            textBuilder = regLineOutput.textBuilder();
            previousBulletStyle = regLineOutput.prevBulletStyle();
        }
        return textBuilder;
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, String patternToRemoveSentence, String patternToRemoveLine)
    {
        boolean prevLineWasPageBreak = false;
        StringBuilder paragraphBuffer = new StringBuilder();
        String previousBulletStyle = "";

        for (String line : textToAdd.split("\n")){

            //perform paragraph related checks
            ParagraphSearchOut paragraphData = performParagraphChecks(line, paragraphBuffer, textBuilder, patternToRemoveSentence, patternToRemoveLine);
            line = paragraphData.firstLineOfNextParagraph();
            paragraphBuffer = paragraphData.paragraph();
            textBuilder = paragraphData.constructedOutText();
            //prepend a backslash to any special characters so that they are literally interpreted in the latex code
            for (String specialChar : charactersToBackslash){
                if (line.contains(specialChar)){
                    line = line.replace(specialChar, "\\".concat(specialChar));
                }
            }

           if(!lineContainsPattern(line, patternToRemoveLine)) {
               if(line.contains(newPageSeperator)){
                   line = line.replace(newPageSeperator, "\\newpage\n");
                   prevLineWasPageBreak = true;
                   if (previousBulletStyle != ""){
                       paragraphBuffer.append("\\end{itemize}\n");
                       previousBulletStyle = "";
                   }
               }

               RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, paragraphBuffer, previousBulletStyle);
               prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
               paragraphBuffer = regLineOutput.textBuilder();
               previousBulletStyle = regLineOutput.prevBulletStyle();
           }
        }
        return textBuilder;
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, short numStartLinesToRemove)
    {
        boolean prevLineWasPageBreak = false;
        short linesToRemoveCount = 0;
        String previousBulletStyle = "";

        for (String line : textToAdd.split("\n")){

            if(line.contains(newPageSeperator)){
                line = line.replace(newPageSeperator, "\\newpage\n");
                prevLineWasPageBreak = true;
                linesToRemoveCount = numStartLinesToRemove;
                //ensure that bullet points are reset before moving to the next page
                if (previousBulletStyle != ""){
                    for (int i = 0; i < nestedBulletPointStyleStack.size(); i++){
                        textBuilder.append("\\end{itemize}\n");
                    }
                    previousBulletStyle = "";
                }
                nestedBulletPointStyleStack.clear();
            }
            //prepend a backslash to any special characters so that they are literally interpreted in the latex code
            for (String specialChar : charactersToBackslash){
                if (line.contains(specialChar)){
                    line = line.replace(specialChar, "\\".concat(specialChar));
                }
            }
            if (linesToRemoveCount >0){
                if (linesToRemoveCount == numStartLinesToRemove){
                    textBuilder.append(line);
                    textBuilder.append("\\\\ \n");
                }
                linesToRemoveCount--;
            }
            else{
                RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, textBuilder, previousBulletStyle);
                prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
                textBuilder = regLineOutput.textBuilder();
                previousBulletStyle = regLineOutput.prevBulletStyle();
            }
        }
        return textBuilder;
    }

    /**
     * This function converts the common aspects of the pdf into latex indlucing:
     * -regular text
     * -newpagse
     * -titles / section headings
     * -bullet points
     * @param prevLineWasPageBreak
     * @param line
     * @param textBuilder
     * @return
     */
    private static RegularDocLineOut addDocLine(boolean prevLineWasPageBreak, String line, StringBuilder textBuilder, String previousBulletStyle)
    {
        boolean addNewline = true;
        //adding the title to a page
        if(prevLineWasPageBreak && !line.contains("\\newpage") && !line.isBlank()){
            textBuilder.append("\\section{");
            textBuilder.append(line);
            textBuilder.append("}\n");
            prevLineWasPageBreak = false;
        }
        else{
            //converting bullet points into latex bullet points
            if (line.contains("• ")){
                BulletPointOperationOut bulletOperationOut = handleBulletPointOperation(line, "• ", previousBulletStyle);
                line = bulletOperationOut.modifiedLine();
                previousBulletStyle = bulletOperationOut.previousBulletStyle();
                addNewline = false;
            }
            else if(line.length() > 2){
                if (line.substring(0, 2).contains("– ")){
                    BulletPointOperationOut bulletOperationOut = handleBulletPointOperation(line, "– ", previousBulletStyle);
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
        return new RegularDocLineOut(textBuilder, prevLineWasPageBreak, previousBulletStyle);
    }


    /**
     * handles the manipulation of the line string, when a bullet point is encountered.
     * @param line
     * @param bulletPointStyle
     * @param previousBulletStyle
     * @return
     */
    private static BulletPointOperationOut handleBulletPointOperation(String line, String bulletPointStyle, String previousBulletStyle)
    {
        line = "  \\item ".concat(line.replace(bulletPointStyle, ""));
        if (!previousBulletStyle.equals(bulletPointStyle)){
            //start a new set of bullet points
            if (nestedBulletPointStyleStack.isEmpty() || !nestedBulletPointStyleStack.contains(bulletPointStyle)){
                line = "\\begin{itemize}\n".concat(line);
                //if starting a new bullet point, and we are indenting into this new style, add to bullet point style stack
                nestedBulletPointStyleStack.push(bulletPointStyle);
            }
            else{
                //only end the bullet points, if unindenting the bullet points
                line = "\\end{itemize}\n".concat(line);
                nestedBulletPointStyleStack.pop();
            }
        }
        previousBulletStyle = bulletPointStyle;
        return new BulletPointOperationOut(line, previousBulletStyle);
    }

    private record BulletPointOperationOut(String modifiedLine, String previousBulletStyle){}

    private record RegularDocLineOut(StringBuilder textBuilder, boolean previousLineWasPageBreak, String prevBulletStyle){}

    /**
     * Used to identify patterns of letters/words within a paragraph.
     * if
     * e.g. to remove a page number / recurring text for each page in the document
     * @param paragraphToSearch = the line that is being searched for the pattern
     * @param patternToSearchFor = the pattern of characters that is being searched for
     */
    private static boolean paragraphContainsPattern(String paragraphToSearch, String patternToSearchFor)
    {
        return false;
    }

    /**
     * Used to remove patterns of letters/words on a single line.
     * if
     * e.g. to remove a page number / recurring text for each page in the document
     * @param lineToSearch = the line that is being searched for the pattern
     * @param patternToSearchFor = the pattern of characters that is being searched for
     */
    private static boolean lineContainsPattern(String lineToSearch, String patternToSearchFor)
    {
        return false;
    }

    private static ParagraphSearchOut performParagraphChecks(String line, StringBuilder paragraphBuffer, StringBuilder textBuilder, String patternToRemoveLine, String patternToRemoveSentence)
    {
        //handling paragraphs
        if(line.contains("\n")){
            //split the line at the newline
            String[] splitLine = line.split("\n");
            if (splitLine.length!=2){
                splitLine = new String[]{"", ""};
            }
            String prevLine = splitLine[0];
            line = splitLine[1];

            //add the last part of the previous paragraph
            if(!lineContainsPattern(line, patternToRemoveLine)) {
                paragraphBuffer.append(prevLine);
            }

            //add the previous paragraph if it was valid
            String previousParagraph = paragraphBuffer.toString();
            if(!paragraphContainsPattern(previousParagraph, patternToRemoveSentence)){
                textBuilder.append(previousParagraph);
            }
            paragraphBuffer.setLength(0);//empty the buffer, for the start of the next paragraph
        }
        ParagraphSearchOut outInfo = new ParagraphSearchOut(line, paragraphBuffer, textBuilder);
        return outInfo;
    }

    private record ParagraphSearchOut(String firstLineOfNextParagraph,StringBuilder paragraph, StringBuilder constructedOutText){}

}
