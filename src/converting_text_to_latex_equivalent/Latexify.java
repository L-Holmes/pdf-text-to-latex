package converting_text_to_latex_equivalent;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Converts an input String that has been read from  a pdf into a latex-like format
 */
public class Latexify {
    private final static  String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";//the text that LaTeX will interpret as a 'new page prompt'
    private final static String BEGIN_DOC_PLACEHOLDER = "fsahfvmaekl343nm!";//a placeholder/temp for the text that this class will use/interpret as 'starting the main document body'

    //--optional arguments--
    private static boolean quizMode = false;//true if 'quiz mode' formatting is to be applied to the text; false otherwise
    private static short numStartLinesToRemoveForEachPage = 0;
    private static short linesToRemoveCount = 0;//used to keep-track of how many of the following lines of text must be ignored/removed
    private static String removePatternOnLine = "";//the sequence of characters that will be searched for on each line. The line will be removed if it contains that sequence
    private static String removePatternOnPara = "";//the sequence of characters that will be searched for on each paragraph. The paragraph will be removed if it contains that sequence
    private final static String QUIZ_PAGE_INDICATION_TEXT = "(Q)"; //text added to the start of each quiz page, to indicate that this particular page is a quiz page(i.e. a page asking a question)

    //----Optional argument setting---
    /**
     * Resets all of the optional variables back to their default value
     */
    private static void resetOptionalArguments()
    {
        quizMode = false;
        removePatternOnLine = "";
        removePatternOnPara = "";
    }

    /**
     * Initialises the instance variables relating to any optional argumentes,
     * with any of the optional arguments that have been passed in
     * @param args = the structure containing the optional arguments
     */
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

    /**
     * Converts the text into LaTeX, by converting any placeholders/special characters/modifiers
     * into their LaTeX equivalent
     * @param textToConvert = the input text, that is to be Latexified (should be the text read from the pdf, and will act as the 'body' of the LaTeX text)
     * @param textToAddImages = the text which contains LaTeX text, which is used to add images to the text (each entry corresponds to the page index from the input pdf, whose contents have been read)
     * @param newPageSeperator = the temporary/placeholder text that is used to represent were a page-break prompt should be placed
     * @param args = the structure containing the optional arguments
     * @return the Latexified text
     */
    public static String convertTextToLatex(String textToConvert, String[] textToAddImages, String newPageSeperator, LatexifyOptionalArguments args)
    {
        initialiseOptionalArguments(args);
        StringBuilder asLatex  = getFullLaTexDocumentText(textToConvert, textToAddImages, newPageSeperator);

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

    private static StringBuilder getFullLaTexDocumentText(String textToConvert, String[] textToAddImages, String newPageSeperator)
    {
        StringBuilder textToCreateDocument = new StringBuilder();
        textToCreateDocument = addRequiredPageToStartOfDocument(textToCreateDocument);


        LatexifyDocumentBodyConvertor documentBodyToLatexifiedVersionGetter = new LatexifyDocumentBodyConvertor();
        textToCreateDocument = documentBodyToLatexifiedVersionGetter.getDocumentBody(textToCreateDocument, textToConvert, newPageSeperator, textToAddImages); //(add the data parsed from the pdf)


        textToCreateDocument.append("\\end{document}");
        return textToCreateDocument;
    }

    private static StringBuilder addRequiredPageToStartOfDocument(StringBuilder textToCreateDocument)
    {
        //add required start of latex document
        textToCreateDocument.append("\\documentclass[12pt]{article}\n\n");
        textToCreateDocument.append("\\usepackage[T1]{fontenc}");
        //for images
        textToCreateDocument.append("\\usepackage{graphicx}");
        textToCreateDocument.append("\\graphicspath{ {./images/} } ");
        //force images onto the page that they are placed onto
        textToCreateDocument.append("\\usepackage{float}");
        //begining the doc
        textToCreateDocument.append(BEGIN_DOC_PLACEHOLDER);
        return textToCreateDocument;
    }




    /**
     * Applies quiz formatting to the inputted LaTeX-style text.
     * This includes:
     * -before each page, adding a 'question' page.
     *      -which will contain a question, usually to describe <title of the page>
     * @param textToConvert = The LaTeX style text, which is to be quizified
     * @return a quizified version of the LaTex text
     */
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
        String[] splitByNewPages = documentBody.split(Pattern.quote(TEXT_TO_ADD_NEW_PAGE_IN_LATEX));

        StringBuilder quizPage = new StringBuilder();
        String pageTitle = "...";
        for (String page : splitByNewPages)
        {
            //find the page title
            if(page.contains(PAGE_TITLE_TEXT_INDICATOR_START)){
                String textAfterAndIncludingTitle;
                try {
                    textAfterAndIncludingTitle = page.split(Pattern.quote(PAGE_TITLE_TEXT_INDICATOR_START))[1];
                }
                catch(ArrayIndexOutOfBoundsException e){
                    textAfterAndIncludingTitle = page.split(Pattern.quote(PAGE_TITLE_TEXT_INDICATOR_START))[0];
                }
                try {
                    pageTitle = textAfterAndIncludingTitle.split(Pattern.quote(PAGE_TITLE_TEXT_INDICATOR_END))[0];
                }
                catch(ArrayIndexOutOfBoundsException e){
                    pageTitle = textAfterAndIncludingTitle.split(Pattern.quote(PAGE_TITLE_TEXT_INDICATOR_END))[1];
                }
            }
            //add quiz page indication text
            quizPage.append(QUIZ_PAGE_INDICATION_TEXT+"\n");

            //add the title to the quiz page
            quizPage.append("Describe: ").append(pageTitle).append("\n");

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
