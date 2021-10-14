package converting_text_to_latex_equivalent;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LatexifyConvertorToQuiz {
    private final static String BEGIN_DOC_PLACEHOLDER = "fsahfvmaekl343nm!";//a placeholder/temp for the text that this class will use/interpret as 'starting the main document body'
    private final static  String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";//the text that LaTeX will interpret as a 'new page prompt'
    private final static String QUIZ_PAGE_INDICATION_TEXT = "(Q)"; //text added to the start of each quiz page, to indicate that this particular page is a quiz page(i.e. a page asking a question)


    private final String PAGE_TITLE_TEXT_INDICATOR_START = "\\section".concat("{");
    private final String PAGE_TITLE_TEXT_INDICATOR_END = "}";

    /**
     * Applies quiz formatting to the inputted LaTeX-style text.
     * This includes:
     * -before each page, adding a 'question' page.
     *      -which will contain a question, usually to describe <title of the page>
     * @param textToConvert = The LaTeX style text, which is to be quizified
     * @return a quizified version of the LaTex text
     */
    public String applyQuizFormatting(String textToConvert)
    {
        ArrayList<String> textAsQuiz = new ArrayList<String>();
        String[] splitAtDocBodyBeginning = textToConvert.split(BEGIN_DOC_PLACEHOLDER);
        String documentStart = splitAtDocBodyBeginning[0];
        String documentBody = getDocumentBody(splitAtDocBodyBeginning);
        String[] splitByNewPages = documentBody.split(Pattern.quote(TEXT_TO_ADD_NEW_PAGE_IN_LATEX));

        for (String page : splitByNewPages) textAsQuiz = getTextToCreateQuizPage(page, textAsQuiz);

        //join all pages back together with Latex's new page indicator text
        return documentStart.concat(BEGIN_DOC_PLACEHOLDER).concat("\n").concat(String.join(TEXT_TO_ADD_NEW_PAGE_IN_LATEX, textAsQuiz));
    }

    private String getDocumentBody(String[] splitAtDocBodyBeginning)
    {
        String documentBody;
        try{
            documentBody = splitAtDocBodyBeginning[1];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            documentBody = "";
        }
        return documentBody;
    }

    private ArrayList<String> getTextToCreateQuizPage(String page, ArrayList<String> textAsQuiz)
    {
        StringBuilder quizPage = constructQuizPage(page) ;

        //add the quiz page (question page) to the output
        textAsQuiz.add(quizPage.toString());
        quizPage.setLength(0);

        //add the regular [answer] page to the output
        textAsQuiz.add(page);

        return textAsQuiz;
    }

    private String findPageTitle(String page){
        String pageTitle = "...";
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
        return pageTitle;

    }

    private StringBuilder constructQuizPage(String page)
    {
        StringBuilder quizPage = new StringBuilder();;
        //find the page title
        String pageTitle = findPageTitle(page);

        //add quiz page indication text
        quizPage.append(QUIZ_PAGE_INDICATION_TEXT+"\n");

        //add the title to the quiz page
        quizPage.append("Describe: ").append(pageTitle).append("\n");
        return quizPage;
    }
}
