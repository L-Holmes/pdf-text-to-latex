import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Converts an input String that has been read from  a pdf into a latex-like format
 */
public class Latexify {
    private static Stack<String> nestedBulletPointStyleStack = new Stack<String>();
    private final static String[] CHARACTERS_TO_BACKSLASH = {"_", "&" , "%" , "$" , "#" , "{" , "}" , "~" , "^"};  //charcters which are interpreted as special/modifier characters in latex, and so must be prepended by a backslash when converted
    private static int currentPage = 0;
    private final static  String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";
    private final static String BEGIN_DOC_PLACEHOLDER = "fsahfvmaekl343nm!";

    //--optional arguments--
    private static boolean quizMode = false;
    private static short numStartLinesToRemoveForEachPage = 0;
    private static String removePatternOnLine = "";
    private static String removePatternOnPara = "";
    private final static String QUIZ_PAGE_INDICATION_TEXT = "(Q)"; //text added to the start of each quiz page, to indicate that this particular page is a quiz page(i.e. a page asking a question)
    private static short linesToRemoveCount = 0;

    //----Optional argument setting---
    /**
     * Resets all of the optional variables back to their default value
     */
    private static void resetOptionalArguments()
    {
        quizMode = false;
        numStartLinesToRemoveForEachPage = 0;
        removePatternOnLine = "";
        removePatternOnPara = "";
        currentPage = 0;
    }

    private static void initialiseOptionalArguments(LatexifyOptionalArguments args)
    {
        if (args != null){
            quizMode = args.getQuizMode();
            numStartLinesToRemoveForEachPage = args.getNumStartLinesToRemoveForEachPage();
            removePatternOnLine = args.getRemovePatternOnLine();
            removePatternOnPara = args.getRemovePatternOnPara();
        }
    }
    //--- ---

    public static String convertTextToLatex(String textToConvert, String[] textToAddImages, String newPageSeperator, LatexifyOptionalArguments args)
    {
        initialiseOptionalArguments(args);
        StringBuilder asLatex  = new StringBuilder();
        //add required start of latex document
        asLatex.append("\\documentclass[12pt]{article}\n\n");
        asLatex.append("\\usepackage[T1]{fontenc}");
        //for images
        asLatex.append("\\usepackage{graphicx}");
        asLatex.append("\\graphicspath{ {./images/} } ");
        //force images onto the page that they are placed onto
        asLatex.append("\\usepackage{float}");
        //begining the doc 
        asLatex.append(BEGIN_DOC_PLACEHOLDER);
        //add the body of the document (add the data parsed from the pdf)
        asLatex = addDocumentBody(asLatex, textToConvert, newPageSeperator, textToAddImages);
        //add required end of document
        asLatex.append("\\end{document}");

        //return latexified string
        String outputString = asLatex.toString();
        if(quizMode){
            outputString = applyQuizFormatting(outputString);
        }
        //add in the actual begin doc text
        outputString = outputString.replace(BEGIN_DOC_PLACEHOLDER, "\\begin{document}\n");
        //reset options for next call
        resetOptionalArguments();
        return outputString;
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator, String[] textToAddImages)
    {
        boolean prevLineWasPageBreak = false;
        String previousBulletStyle = "";


        //optional arguments
        StringBuilder paragraphBuffer = new StringBuilder();



        for (String line : textToAdd.split("\n")){
            //prepend a backslash to any special characters so that they are literally interpreted in the latex code
            for (String specialChar : CHARACTERS_TO_BACKSLASH){
                if (line.contains(specialChar)){
                    line = line.replace(specialChar, "\\".concat(specialChar));
                }
            }
            line.replace("<", " \\textless ");
            line.replace(">", " \\textgreater ");

            //performing paragraph related checks
            if (!removePatternOnPara.isBlank() && !removePatternOnLine.isBlank()){
                ParagraphSearchOut paragraphData = performParagraphChecks(line, paragraphBuffer, textBuilder, removePatternOnPara, removePatternOnLine);
                line = paragraphData.firstLineOfNextParagraph();
                paragraphBuffer = paragraphData.paragraph();
                textBuilder = paragraphData.constructedOutText();
            }

            //performing new page related checks
            adjustAnyNewpagesOutput newpageAdjustOut = adjustAnyNewpages(line, newPageSeperator, prevLineWasPageBreak, previousBulletStyle, textBuilder, linesToRemoveCount, textToAddImages);
            line = newpageAdjustOut.line();
            prevLineWasPageBreak = newpageAdjustOut.prevLineWasPageBreak();
            previousBulletStyle = newpageAdjustOut.previousBulletStyle();
            textBuilder = newpageAdjustOut.textBuilder();
            linesToRemoveCount = newpageAdjustOut.lineToRemoveCount();

            System.out.println("prev pvb:"+prevLineWasPageBreak);

            if (linesToRemoveCount >0){
                System.out.println("lines to remove count: "+linesToRemoveCount);
                //if the user has chosen to ignore the first 'x' lines of each page, here, they are ignored (i.e. not added to the output text)
                if (linesToRemoveCount == numStartLinesToRemoveForEachPage){
                    //add the first line
                    textBuilder.append(line);
                    textBuilder.append("\\\\ \n");
                }
                linesToRemoveCount--;
            }
            else{
                //perform regular line adjustments
                RegularDocLineOut regLineOutput;
                if (!removePatternOnPara.isBlank() && !removePatternOnLine.isBlank()){
                    System.out.println("1"+prevLineWasPageBreak);
                    regLineOutput = addDocLine(prevLineWasPageBreak, line, paragraphBuffer, previousBulletStyle);
                    paragraphBuffer = regLineOutput.textBuilder();
                }
                else{
                    System.out.println("2"+prevLineWasPageBreak);
                    regLineOutput = addDocLine(prevLineWasPageBreak, line, textBuilder, previousBulletStyle);
                    textBuilder = regLineOutput.textBuilder();
                }
                prevLineWasPageBreak = regLineOutput.previousLineWasPageBreak();
                previousBulletStyle = regLineOutput.prevBulletStyle();
            }

        }
        return textBuilder;
    }


    private static adjustAnyNewpagesOutput adjustAnyNewpages(String line, String newPageSeperator, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder textBuilder, short linesToRemoveCount, String[] textToAddImages)
    {
        if(!line.contains(newPageSeperator)) {
            System.out.println("\nreturning prev line "+ prevLineWasPageBreak);
            return new adjustAnyNewpagesOutput(line, prevLineWasPageBreak, previousBulletStyle, textBuilder, linesToRemoveCount);
        }

        currentPage++;

        //get the image text
        String  currentPageImageAddingText;
        try {
            currentPageImageAddingText= textToAddImages[currentPage-1];
        }
        catch(ArrayIndexOutOfBoundsException e){
            currentPageImageAddingText= "";
        }

        //add the newpage indications
        line = line.replace(newPageSeperator, TEXT_TO_ADD_NEW_PAGE_IN_LATEX);
        prevLineWasPageBreak = true;
        linesToRemoveCount = numStartLinesToRemoveForEachPage;

        //add the images to the and of the page
        String[] splitLine = line.split(TEXT_TO_ADD_NEW_PAGE_IN_LATEX);
        try {
            line = splitLine[0].concat(currentPageImageAddingText).concat(splitLine[1]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            //if the newpage is at the immediate start/end of the sentence
            line = currentPageImageAddingText.concat(splitLine[0]);
        }

        //end any bullet points
        if (previousBulletStyle != ""){
            textBuilder.append("\\end{itemize}\n");
            previousBulletStyle = "";
        }

        System.out.println("\nreturning prev line true");
        return new adjustAnyNewpagesOutput(line, true, previousBulletStyle, textBuilder, linesToRemoveCount);
    }

    private record adjustAnyNewpagesOutput(String line, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder textBuilder, short lineToRemoveCount){}

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
        if(prevLineWasPageBreak && !line.contains(TEXT_TO_ADD_NEW_PAGE_IN_LATEX) && !line.isBlank()){
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

    /**
     * Checks if a new paragraph has started.
     * If so, adds the currently contstructed paragraph to the output string, if it is valid
     * the next paragraph is then started.
     * @param line = the line that is being checked to see if it is the last line of the paragraph
     * @param paragraphBuffer = contains the current paragraph that is being constructed, line-by-line
     * @param textBuilder = contains the output text, that has been constructed thus far
     * @param patternToRemoveLine = the pattern of letters/string, that will invalidate a line, should the line contain it (i.e. the line will be deleted; e.g. if it contains: 'remove me', delete this line)
     * @param patternToRemoveSentence = the pattern of letters/string, that will invalidate a paragraph, should the paragraph contain it (i.e. the paragraph will be deleted; e.g. if it contains: 'remove me', delete this paragraph)
     * @return the updated information; the current line that is being investigated, the current paragraph that is being investigated, and the output text.
     */
    private static ParagraphSearchOut performParagraphChecks(String line, StringBuilder paragraphBuffer, StringBuilder textBuilder, String patternToRemoveLine, String patternToRemoveSentence)
    {
        //handling paragraphs

        //if line contains a newline, then a new paragraph has been reached
        if(!line.contains("\n")){
            return new ParagraphSearchOut(line, paragraphBuffer, textBuilder);
        }
        //split the line at the newline
        String[] splitLine = line.split("\n");
        if (splitLine.length!=2){
            splitLine = new String[]{"", ""};
        }
        String lastLineOfPrevParagraph = splitLine[0];
        line = splitLine[1];

        //Finish constructing the previous paragraph by adding the last line
        if(!lineContainsPattern(line, patternToRemoveLine)) {
            //perform checks
            paragraphBuffer.append(lastLineOfPrevParagraph);
        }

        //add the previous paragraph  to the output string, if it is valid
        String previousParagraph = paragraphBuffer.toString();
        if(!paragraphContainsPattern(previousParagraph, patternToRemoveSentence)){
            textBuilder.append(previousParagraph);
        }
        //reset the to start the next paragraph
        paragraphBuffer.setLength(0);//empty the buffer, for the start of the next paragraph

        return new ParagraphSearchOut(line, paragraphBuffer, textBuilder);
    }

    private record ParagraphSearchOut(String firstLineOfNextParagraph,StringBuilder paragraph, StringBuilder constructedOutText){}

    private static String applyQuizFormatting(String textToConvert)
    {
        String PAGE_TITLE_TEXT_INDICATOR_START = "\\section".concat("{");
        String PAGE_TITLE_TEXT_INDICATOR_END = "}";
        ArrayList<String> textAsQuiz = new ArrayList<String>();
        String[] splitAtDocBodyBeginning = textToConvert.split(BEGIN_DOC_PLACEHOLDER);
        String documentStart = splitAtDocBodyBeginning[0];
        String documentBody;
        try{
            documentBody = splitAtDocBodyBeginning[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            documentBody = "";
        }
        String separator = TEXT_TO_ADD_NEW_PAGE_IN_LATEX;
        String[] splitByNewPages = documentBody.split(Pattern.quote(separator));

        StringBuilder quizPage = new StringBuilder();
        String pageTitle = "...";
        for (String page : splitByNewPages)
        {
            //find the page title
            if(page.contains(PAGE_TITLE_TEXT_INDICATOR_START)){
                String textAfterAndIncludingTitle;
                String titleTextStartSeperator = PAGE_TITLE_TEXT_INDICATOR_START;
                try {
                    textAfterAndIncludingTitle = page.split(Pattern.quote(titleTextStartSeperator))[1];
                }
                catch(ArrayIndexOutOfBoundsException e){
                    textAfterAndIncludingTitle = page.split(Pattern.quote(titleTextStartSeperator))[0];
                }
                String titleTextEndSeperator = PAGE_TITLE_TEXT_INDICATOR_START;
                try {
                    pageTitle = textAfterAndIncludingTitle.split(Pattern.quote(titleTextEndSeperator))[0];
                }
                catch(ArrayIndexOutOfBoundsException e){
                    pageTitle = textAfterAndIncludingTitle.split(Pattern.quote(titleTextEndSeperator))[1];
                }
            }
            //add quiz page indication text
            quizPage.append(QUIZ_PAGE_INDICATION_TEXT+"\n");

            //add the title to the quiz page
            quizPage.append("Describe: "+pageTitle+"\n");

            //add the quiz page (question page) to the output
            textAsQuiz.add(quizPage.toString());
            quizPage.setLength(0);
            //add the regular [answer] page to the output
            textAsQuiz.add(page);
            //add the regular [answer] page to the output
        }
        //join all pages back together with Latex's new page indicator text
        return documentStart.concat(BEGIN_DOC_PLACEHOLDER).concat("\n").concat(String.join(TEXT_TO_ADD_NEW_PAGE_IN_LATEX, textAsQuiz));
    }
}
