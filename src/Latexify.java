public class Latexify {

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

        for (String line : textToAdd.split("\n")){
            if(line.contains(newPageSeperator)){
                line = line.replace(newPageSeperator, "\\newpage\n");
                prevLineWasPageBreak = true;
            }

            RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, textBuilder);
            prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
            textBuilder = regLineOutput.textBuilder();
        }
        return textBuilder;
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, String patternToRemoveSentence, String patternToRemoveLine)
    {
        boolean prevLineWasPageBreak = false;
        StringBuilder paragraphBuffer = new StringBuilder();

        for (String line : textToAdd.split("\n")){
            //perform paragraph related checks
            ParagraphSearchOut paragraphData = performParagraphChecks(line, paragraphBuffer, textBuilder, patternToRemoveSentence, patternToRemoveLine);
            line = paragraphData.firstLineOfNextParagraph();
            paragraphBuffer = paragraphData.paragraph();
            textBuilder = paragraphData.constructedOutText();

           if(!lineContainsPattern(line, patternToRemoveLine)) {
               if(line.contains(newPageSeperator)){
                   line = line.replace(newPageSeperator, "\\newpage\n");
                   prevLineWasPageBreak = true;
               }

               RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, paragraphBuffer);
               prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
               paragraphBuffer = regLineOutput.textBuilder();
           }
        }
        return textBuilder;
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, short numStartLinesToRemove)
    {
        boolean prevLineWasPageBreak = false;
        short linesToRemoveCount = 0;

        for (String line : textToAdd.split("\n")){
            if(line.contains(newPageSeperator)){
                line = line.replace(newPageSeperator, "\\newpage\n");
                prevLineWasPageBreak = true;
                linesToRemoveCount = numStartLinesToRemove;
            }
            if (linesToRemoveCount >0){
                if (linesToRemoveCount == numStartLinesToRemove){
                    textBuilder.append(line);
                    textBuilder.append("\\\\ \n");
                }
                linesToRemoveCount--;
            }
            else{
                RegularDocLineOut regLineOutput = addDocLine(prevLineWasPageBreak, line, textBuilder);
                prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
                textBuilder = regLineOutput.textBuilder();
            }
        }
        return textBuilder;
    }

    private static RegularDocLineOut addDocLine(boolean prevLineWasPageBreak, String line, StringBuilder textBuilder)
    {
        if(prevLineWasPageBreak && !line.contains("\\newpage") && !line.isBlank()){
            textBuilder.append("\\section{");
            textBuilder.append(line);
            textBuilder.append("}\n");
            prevLineWasPageBreak = false;
        }
        else{
            textBuilder.append(line);
            textBuilder.append("\\\\ \n");
        }
        return new RegularDocLineOut(prevLineWasPageBreak, textBuilder);
    }

    private record RegularDocLineOut(boolean previousLineWasPageBreak, StringBuilder textBuilder){}

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
