public class LatexifyOptionalArguments {
    private  boolean quizMode = false;
    private  short numStartLinesToRemoveForEachPage = 0;
    private  String removePatternOnLine = "";
    private  String removePatternOnPara = "";

    LatexifyOptionalArguments()
    {}

    /**
     * @return the quiz mode
     */
    public boolean getQuizMode()
    {
        return quizMode;
    }

    /**
     * @return the number of lines at the start of the page to remove for each page
     */
    public short getNumStartLinesToRemoveForEachPage()
    {
        return numStartLinesToRemoveForEachPage;
    }

    /**
     * @return the the remove pattern for a single line
     */
    public String getRemovePatternOnLine()
    {
        return removePatternOnLine;
    }

    /**
     * @return the remove pattern for a paragrpah
     */
    public String getRemovePatternOnPara()
    {
        return removePatternOnPara;
    }



    /**
     * sets the new quiz mode
     * @param newQuizMode
     */
    public void setQuizMode(boolean newQuizMode)
    {
        quizMode  = newQuizMode;
    }

    /**
     * sets the new num. line to remove from the start of each pagee
     * @param newQuizMode
     */
    public void setNumStartLinesToRemoveForEachPage(short newQuizMode)
    {
        numStartLinesToRemoveForEachPage = newQuizMode;
    }

    /**
     * sets the new pattern to remove on a line
     * @param newNumLines
     */
    public void setRemovePatternOnLine(String newNumLines)
    {
        removePatternOnLine = newNumLines;
    }

    /**
     * sets the new paragraph remove pattern
     * @param newPatternToRemove
     */
    public void setRemovePatternOnPara(String newPatternToRemove)
    {
        removePatternOnPara = newPatternToRemove;
    }
}
