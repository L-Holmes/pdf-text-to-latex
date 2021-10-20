package main.converting_text_to_latex_equivalent.conversion_tools;

import java.util.Stack;

public class LatexifyBulletPointHandler {
    private final static Stack<String> nestedBulletPointStyleStack = new Stack<String>();

    /**
     * Intended to be used when a bullet point is found within the regular text
     * Handles the conversion of the bullet point into a LaTex bullet point,
     * alongside accounting for previous and future bullet points within the same
     * section of bullet points
     *
     * @param line = the line of text that contains a bullet point
     * @param bulletPointStyle = the textual representation of the bullet point itself, in the regular text line
     * @param previousBulletStyle = the bullet point style of the line above the one being processed here
     * @return : - The line, but with the bullet points converted into a LaTex format.
     *           - The required meta-data to process future bullet points. In this case;
     *                 - The style of this bullet point
     */
    public BulletPointOperationOut handleBulletPointOperation(String line, String bulletPointStyle, String previousBulletStyle)
    {
        line = changeTheBulletPointToALatexBulletPoint(line, bulletPointStyle);
        if(bulletPointStyleHasChanged(bulletPointStyle, previousBulletStyle)) line = handleBulletPointStyleChange(line, bulletPointStyle);
        return new BulletPointOperationOut(line, bulletPointStyle);
    }

    private String changeTheBulletPointToALatexBulletPoint(String lineContainingBulletPoint, String bulletPointStyle){
        return "  \\item ".concat(lineContainingBulletPoint.replace(bulletPointStyle, ""));
    }

    private boolean bulletPointStyleHasChanged(String bulletPointStyle, String previousBulletStyle)
    {
        try{
            if (previousBulletStyle.equals(bulletPointStyle)) return false;
        }
        catch(NullPointerException e){
            if(previousBulletStyle == null && bulletPointStyle == null) return false;
        }
        return true;
    }

    private String handleBulletPointStyleChange(String line, String bulletPointStyle)
    {
        //start a new set of bullet points
        if (nestedBulletPointStyleStack.isEmpty() || !nestedBulletPointStyleStack.contains(bulletPointStyle)){
            line = "\\begin{itemize}\n".concat(line);
            //if starting a new bullet point, and we are indenting into this new style, add to bullet point style stack
            nestedBulletPointStyleStack.push(bulletPointStyle);
        }
        else{
            /*
            either the nested bullet point style stack is not empty
            or the style stack does not contain the bullet point style
             */

            //only end the bullet points, if unindenting the bullet points
            line = "\\end{itemize}\n".concat(line);
            nestedBulletPointStyleStack.pop();
        }
        return line;
    }

    /**
     *
     * @return true if there are bullet points that have not been ended; false if there are no bullet points to end
     */
    protected boolean hasNestedBulletsThatHaveNotBeenEnded()
    {
        System.out.println("bullet point stack is empty????????????"+ nestedBulletPointStyleStack.isEmpty());
        return !(nestedBulletPointStyleStack.isEmpty());
    }

    /**
     * Removes the top level of nested bullet points from the
     * nested bullet point stack (used to keep track of the levels of nested bullet points)
     */
    protected void removeTopNestedBulletPoint()
    {
        nestedBulletPointStyleStack.pop();
    }

}
