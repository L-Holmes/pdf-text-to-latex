package main.converting_text_to_latex_equivalent.conversion_tools;

import main.converting_text_to_latex_equivalent.Latexify;

import java.util.regex.Pattern;

public class LatexifyNewPageAdjustor {
    //optional arguments
    private  int currentPage = 0; //the page number (of the input pdf), whose contents are currently being processed
    private short numStartLinesToRemoveForEachPage = 0;// the number of lines to remove/ignore, when processing the text for each page of the pdf text input

    //
    private final String TEXT_TO_ADD_NEW_PAGE_IN_LATEX = "\\clearpage\n";//the text that LaTeX will interpret as a 'new page prompt'


    /**
     * Formats & performs any new page actions/checks for the line, if it contains a page break prompts,
     * @param line = the line that is being checked for page break prompts / the line that will be updated to use to LaTeX style new page prompts & commands
     * @param newPageSeperator = the temporary/placeholder text that is used to represent were a page-break prompt should be placed
     * @param prevLineWasPageBreak = true if the line that was previously processed contained a page-break; false otherwise
     * @param previousBulletStyle = the set of characters relating to the style of bullet points that was previously used. (empty string if the prev line was not a bullet point)
     * @param textBuilder = The structure that is used to store the text that has been Latexified so far
     * @param linesToRemoveCount = the number of lines to be ignored/removed from the text starting after a page break prompt
     * @param textToAddImages = the text which contains LaTeX text, which is used to add images to the text (each entry corresponds to the page index from the input pdf, whose contents have been read)
     * @return the updated line, and any possibly changed variable that relate to the text (metadata style variables)
     */
    public NewPageAdjustorOut adjustAnyNewpages(String line, String newPageSeperator, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder textBuilder, short linesToRemoveCount, String[] textToAddImages)
    {
        if(!line.contains(newPageSeperator)) return new NewPageAdjustorOut(line, prevLineWasPageBreak, previousBulletStyle, textBuilder, linesToRemoveCount);
        System.out.println("Found a newpage line!!!!!!!!!"+ line);


        currentPage++;

        //add the images to the end of the previous page
        line = addImageAddingTextToEndOfPreviousPage(line, newPageSeperator, textToAddImages);

        //add the newpage indications
        line = line.replace(newPageSeperator, TEXT_TO_ADD_NEW_PAGE_IN_LATEX);

        //make next page text a title (if necessary)
        nextPageTitlizationOutput titlizationCheckOutput = handlePossibleNextPageTitleWithinNewPageIndicationLine(line);
        line = titlizationCheckOutput.line();
        boolean stillNeedToPerformNewPageTitlization = titlizationCheckOutput.stillNeedToPerformNewPageTitlization();

        //end any bullet points
        if (previousBulletStyle != ""){
            textBuilder.append("\\end{itemize}\n");
            previousBulletStyle = "";
        }

        return new NewPageAdjustorOut(line, stillNeedToPerformNewPageTitlization, previousBulletStyle, textBuilder, numStartLinesToRemoveForEachPage);
    }

    /**
     * Checks if the line with the new page indication also contained the text that would be used as the title for the following page.
     * If that text is found, the relevant titlization is applied to the text
     * @return true if  'next page title' was not found, and future text needs to be made as the title of the next page; false otherwise
     */
    private nextPageTitlizationOutput handlePossibleNextPageTitleWithinNewPageIndicationLine(String lineWithNewPageIndication)
    {
        System.out.println("***handling possible titlization for text: "+ lineWithNewPageIndication);
        System.out.println("with the text splitter:"+TEXT_TO_ADD_NEW_PAGE_IN_LATEX+":");
        //split the line on the TEXT TO ADD NEW PAGE THING
        String[] splitByNewPageText = lineWithNewPageIndication.split(Pattern.quote(TEXT_TO_ADD_NEW_PAGE_IN_LATEX));
        //if the 2nd half of the split is not empty
        if(splitByNewPageText.length > 1){
            System.out.println("more than 1, doing "+ lineWithNewPageIndication);

            String nextPageTitleText = splitByNewPageText[1];
            nextPageTitleText = makeALatexHeading(nextPageTitleText);
            String adjustedTitlizedLine = splitByNewPageText[0].concat(TEXT_TO_ADD_NEW_PAGE_IN_LATEX).concat(nextPageTitleText);
            return new nextPageTitlizationOutput(false, adjustedTitlizedLine);
        }
        return new nextPageTitlizationOutput(true, lineWithNewPageIndication);
    }

    private record nextPageTitlizationOutput(boolean stillNeedToPerformNewPageTitlization, String line){}

    private String makeALatexHeading(String line)
    {
        return "\\section{".concat(line).concat("}\n");
    }

    private String getImageAddingText(String[] textToAddImages)
    {
        //jump
        String  currentPageImageAddingText;
        try {
            currentPageImageAddingText= textToAddImages[currentPage-1];

            System.out.println("£££££££££££££££££££££££ text to add images for this page:££££££££££££££££3");
            System.out.println(" (at page #"+ (currentPage-1) + ")");
            System.out.println(currentPageImageAddingText);
            System.out.println("££££££££££££££££££££££3");
        }
        catch(ArrayIndexOutOfBoundsException e){
            currentPageImageAddingText= "";
        }
        return currentPageImageAddingText;
    }

    private String addImageAddingTextToEndOfPreviousPage(String line, String newPageSeperator, String[] textToAddImages)
    {
        //get the image text
        String  currentPageImageAddingText = getImageAddingText(textToAddImages);


        String[] lineSplitByNewPage = line.split(Pattern.quote(newPageSeperator));
        try {
            line = lineSplitByNewPage[0].concat(currentPageImageAddingText).concat(newPageSeperator).concat(lineSplitByNewPage[1]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            try{
                //if the newpage is at the immediate start/end of the sentence
                line = currentPageImageAddingText.concat(lineSplitByNewPage[0]).concat(newPageSeperator);
            }
            catch(ArrayIndexOutOfBoundsException e2){
                //if the entire line is the newline text
                line = currentPageImageAddingText.concat(line).concat(newPageSeperator);
            }
        }
        return line;
    }
}
