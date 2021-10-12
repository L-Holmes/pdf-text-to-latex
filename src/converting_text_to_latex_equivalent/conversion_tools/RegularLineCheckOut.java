package converting_text_to_latex_equivalent.conversion_tools;

public record RegularLineCheckOut(String modifiedLine, StringBuilder textBuffer, boolean prevLineWasPageBreak, String prevBulletStyle) {
}
