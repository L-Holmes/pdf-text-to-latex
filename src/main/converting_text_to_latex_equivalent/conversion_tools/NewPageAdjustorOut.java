package main.converting_text_to_latex_equivalent.conversion_tools;

/**
 * Output for the adjustAnyNewpages method
 * @param line = the line that is being checked for page break prompts / the line that will be updated to use to LaTeX style new page prompts & commands
 * @param prevLineWasPageBreak = true if the line that was previously processed contained a page-break; false otherwise
 * @param previousBulletStyle  = the set of characters relating to the style of bullet points that was previously used. (empty string if the prev line was not a bullet point)
 * @param textBuilder = The structure that is used to store the text that has been Latexified so far
 * @param lineToRemoveCount = the number of lines to be ignored/removed from the text starting after a page break prompt
 */
public record NewPageAdjustorOut(String line, boolean prevLineWasPageBreak, String previousBulletStyle, StringBuilder textBuilder, short lineToRemoveCount) {
}
