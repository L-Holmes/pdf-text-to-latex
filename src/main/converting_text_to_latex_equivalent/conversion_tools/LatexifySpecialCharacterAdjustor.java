package main.converting_text_to_latex_equivalent.conversion_tools;

public class LatexifySpecialCharacterAdjustor {
    private final String[] CHARACTERS_TO_BACKSLASH = {"_", "&" , "%" , "$" , "#" , "{" , "}" , "~" , "^"};  //characters which are interpreted as special/modifier characters in latex, and so must be prepended by a backslash when converted

    /**
     * modifies any characters/group of characters, that
     * must be changed to be interpreted correctly
     * when converted to LaTeX code
     * @param line = the line, whose special characters are being updated
     * @return the updated line
     */
    public String performSpecialCharacterAdjustments(String line)
    {
        line = backslashAllSpecialCharacters(line);
        line = replaceEqualityOperators(line);
        return line;
    }

    private String backslashAllSpecialCharacters(String line)
    {
        for (String specialChar : CHARACTERS_TO_BACKSLASH) line = addBackslashToStartOfSpecialCharacter(line, specialChar);
        return line;
    }

    private String addBackslashToStartOfSpecialCharacter(String line, String specialChar)
    {
        //prepend a backslash to any special characters so that they are literally interpreted in the latex code
        if (line.contains(specialChar)){
            line = line.replace(specialChar, "\\".concat(specialChar));
        }
        return line;
    }

    private String replaceEqualityOperators(String line)
    {
        line = line.replace("<", " \\textless ");
        line = line.replace(">", " \\textgreater ");
        return line;
    }
}
