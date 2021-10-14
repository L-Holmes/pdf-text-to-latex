package main.converting_text_to_latex_equivalent.conversion_tools;

public record RegularLineCheckOut(StringBuilder textBuffer, boolean prevLineWasPageBreak, String prevBulletStyle) {
}
