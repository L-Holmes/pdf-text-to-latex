package converting_text_to_latex_equivalent.conversion_tools;

public record DocumentLineOut(StringBuilder textBuilder, boolean previousLineWasPageBreak, String prevBulletStyle) {
}
