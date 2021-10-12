package converting_text_to_latex_equivalent.conversion_tools;

import java.util.Stack;

public class LatexifyBulletPointHandler {
    private final static Stack<String> nestedBulletPointStyleStack = new Stack<String>();

    /**
     * handles the manipulation of the line string, when a bullet point is encountered.
     * @param line
     * @param bulletPointStyle
     * @param previousBulletStyle
     * @return
     */
    public BulletPointOperationOut handleBulletPointOperation(String line, String bulletPointStyle, String previousBulletStyle)
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
}
