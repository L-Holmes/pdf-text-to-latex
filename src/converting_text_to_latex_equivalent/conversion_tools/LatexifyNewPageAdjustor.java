package converting_text_to_latex_equivalent.conversion_tools;

import converting_text_to_latex_equivalent.Latexify;

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

        currentPage++;

        //add the images to the end of the previous page
        line = addImageAddingTextToEndOfPreviousPage(line, newPageSeperator, textToAddImages);

        //add the newpage indications
        line = line.replace(newPageSeperator, TEXT_TO_ADD_NEW_PAGE_IN_LATEX);

        //end any bullet points
        if (previousBulletStyle != ""){
            textBuilder.append("\\end{itemize}\n");
            previousBulletStyle = "";
        }

        return new NewPageAdjustorOut(line, true, previousBulletStyle, textBuilder, numStartLinesToRemoveForEachPage);
    }

    private String getImageAddingText(String[] textToAddImages)
    {
        String  currentPageImageAddingText;
        try {
            currentPageImageAddingText= textToAddImages[currentPage-1];
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
