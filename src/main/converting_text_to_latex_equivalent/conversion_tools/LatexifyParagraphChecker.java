package main.converting_text_to_latex_equivalent.conversion_tools;

public class LatexifyParagraphChecker {

    /**
     * Checks if a new paragraph has started.
     * If so, adds the currently contstructed paragraph to the output string, if it is valid
     * the next paragraph is then started.
     * Note: assumes that paragraphs have an entire blank line between them
     *
     * @param line                     = the line that is being checked to see if it is the last line of the paragraph
     * @param paragraphBuffer          = contains the current paragraph that is being constructed, line-by-line
     * @param textBuilder              = contains the output text, that has been constructed thus far
     * @param patternToRemoveParagraph = the pattern of letters/string, that will invalidate a paragraph, should the paragraph contain it (i.e. the paragraph will be deleted; e.g. if it contains: 'remove me', delete this paragraph)
     * @return the updated information; the current line that is being investigated, the current paragraph that is being investigated, and the output text.
     */
    public ParagraphSearchOut performParagraphChecks(String line, StringBuilder paragraphBuffer, StringBuilder textBuilder, String patternToRemoveParagraph) {
        //if line is not blank, then we are in a paragraph (i.e. the line would be between paragraphs otherwise)
        if (!line.isBlank()) return new ParagraphSearchOut(line, paragraphBuffer, textBuilder);
        return handleLineBetweenParagraphs(line, paragraphBuffer, textBuilder, patternToRemoveParagraph);
    }

    private ParagraphSearchOut handleLineBetweenParagraphs(String line, StringBuilder paragraphBuffer, StringBuilder textBuilder, String patternToRemoveParagraph) {
        //only add the previous paragraph if it does not contain the pattern to remove [the line]
        if (!paragraphContainsPattern(paragraphBuffer, patternToRemoveParagraph)) {
            textBuilder.append(paragraphBuffer.toString());
        }

        //empty the buffer, for the start of the next paragraph
        paragraphBuffer.setLength(0);
        return new ParagraphSearchOut(line, paragraphBuffer, textBuilder);
    }

    /**
     * Used to identify patterns of letters/words within a paragraph.
     * e.g. to remove a page number / recurring text for each page in the document
     * Essentially, paragraph checks require the matching of a string, which may span over multiple lines.
     * This method takes the approach of splitting this 'search pattern' line by line, and checking for
     * the matching of the pattern, on a series of lines, in order for the pattern to be 'found'
     *
     * @param paragraphBuffer          = the paragraph that is being searched for the pattern
     * @param patternToRemoveParagraph = the pattern of characters that is being searched for
     * @return true if the paragraph contains the pattern, false otherwise
     */
    private boolean paragraphContainsPattern(StringBuilder paragraphBuffer, String patternToRemoveParagraph) {
        String currentParagraphAsText = paragraphBuffer.toString();
        String[] currentParagraphSplitByNewlines = currentParagraphAsText.split("\n");
        String[] searchPatternSplitByNewlines = patternToRemoveParagraph.split("\n");
        int numTotalChecksToVerify = searchPatternSplitByNewlines.length;
        int foundMatchedPatterns = 0; //number of cumulative lines, which have matched the corresponding pattern being searched for
        int numWhiteSpacesBeforeFirstSearchQuery = -1; //for a search query (e.g:   findMe!), this is the number of leading whitespaces that the actual search term has (e.g: 3)
        int startIndexOfFirstMatch = -1; //for the (whitespace trimmed) first search term, this is the index (from within the line being searched), that the first letter of the search term starts at

        //check paragraph line-by-line
        for (String paraLine : currentParagraphSplitByNewlines) {
            lineContainsPatternOut patternCheckOut = determineIfLineContainsPattern(searchPatternSplitByNewlines, paraLine, numTotalChecksToVerify, foundMatchedPatterns, numWhiteSpacesBeforeFirstSearchQuery, startIndexOfFirstMatch);
            //update variables for following lines
            numTotalChecksToVerify = patternCheckOut.numTotalChecksToVerify();
            foundMatchedPatterns = patternCheckOut.foundMatchedPatterns();
            numWhiteSpacesBeforeFirstSearchQuery = patternCheckOut.numWhiteSpacesBeforeFirstSearchQuery();
            startIndexOfFirstMatch = patternCheckOut.startIndexOfFirstMatch();

            //handle definite 'yes, does contain pattern' or 'no, doesn't contain pattern'
            String doesContainPattern = patternCheckOut.lineContainsPattern();
            if(doesContainPattern == "true") return true;
            else if (doesContainPattern == "false") return false;
        }
        return false;
    }

    private lineContainsPatternOut determineIfLineContainsPattern(String[] searchPatternSplitByNewlines, String paraLine, int numTotalChecksToVerify, int foundMatchedPatterns, int numWhiteSpacesBeforeFirstSearchQuery, int startIndexOfFirstMatch){
        String searchQueryForThisLine = searchPatternSplitByNewlines[foundMatchedPatterns];

        //finding the first line, that matches the first line of the search pattern
        if (foundMatchedPatterns <= 0) {
            numWhiteSpacesBeforeFirstSearchQuery = searchQueryForThisLine.indexOf(searchQueryForThisLine.trim());
            if (paraLine.contains(searchQueryForThisLine)) {
                //find the index that it starts at
                startIndexOfFirstMatch = paraLine.indexOf(searchQueryForThisLine);
                //increment num.found matches
                foundMatchedPatterns++;
            }
        } else {
            //finding any following lines that also match their corresponding line from the search pattern...
            followingLinePatternCheckOut followingPatternCheckOut = findFollowingLinesThatContinueToFitThePattern(paraLine, searchQueryForThisLine, foundMatchedPatterns, startIndexOfFirstMatch, numWhiteSpacesBeforeFirstSearchQuery);
            //update variables
            foundMatchedPatterns = followingPatternCheckOut.foundMatchedPatterns();

            //if pattern has stopped, stop looking
            boolean patternHasStopped = followingPatternCheckOut.patternHasStopped();
            if(patternHasStopped) return new lineContainsPatternOut("false", numTotalChecksToVerify, foundMatchedPatterns, numWhiteSpacesBeforeFirstSearchQuery, startIndexOfFirstMatch);
        }
        //if passed for all 'x' lines of the pattern, then this paragraph contains the pattern
        if (foundMatchedPatterns >= numTotalChecksToVerify) {
            return new lineContainsPatternOut("true", numTotalChecksToVerify, foundMatchedPatterns, numWhiteSpacesBeforeFirstSearchQuery, startIndexOfFirstMatch);
        }
        //don't know if paragraph contains pattern, move onto next line
        return new lineContainsPatternOut(null, numTotalChecksToVerify, foundMatchedPatterns, numWhiteSpacesBeforeFirstSearchQuery, startIndexOfFirstMatch);
    }

    private record lineContainsPatternOut(String lineContainsPattern, int numTotalChecksToVerify, int foundMatchedPatterns, int numWhiteSpacesBeforeFirstSearchQuery, int startIndexOfFirstMatch){}

    private followingLinePatternCheckOut findFollowingLinesThatContinueToFitThePattern(String paraLine, String searchQueryForThisLine, int foundMatchedPatterns, int startIndexOfFirstMatch, int numWhiteSpacesBeforeFirstSearchQuery)
    {
        boolean patternContinues = false;
        //find the relative start index of this line compared to the last (num whitespaces before text)
        if (numWhiteSpacesBeforeFirstSearchQuery >= 0 && startIndexOfFirstMatch >= 0) {
            int numWhiteSpacesBeforeSearchQueryForThisLine = searchQueryForThisLine.indexOf(searchQueryForThisLine.trim());
            int shiftRelToFirstSearchQuery = numWhiteSpacesBeforeFirstSearchQuery - numWhiteSpacesBeforeSearchQueryForThisLine;
            int requiredStartIndexForThisLine = startIndexOfFirstMatch + shiftRelToFirstSearchQuery;

            //if the trimmed line contains the corresponding sub-pattern, at the correct index, it matches.
            if (paraLine.contains(searchQueryForThisLine)) {
                int startIndexForMatchOnThisLine = paraLine.indexOf(searchQueryForThisLine);
                if (startIndexForMatchOnThisLine == requiredStartIndexForThisLine) {
                    foundMatchedPatterns++;
                    patternContinues = true;
                }
            }
        }
        //if it does not contain the next sub-pattern, this paragraph does not contain the search term
        if (!patternContinues) {
            return new followingLinePatternCheckOut(true, foundMatchedPatterns);
        }
        return new followingLinePatternCheckOut(false, foundMatchedPatterns);
    }

    private record followingLinePatternCheckOut(boolean patternHasStopped, int foundMatchedPatterns){}
}
