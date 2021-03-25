public class Latexify {
    public static String convertTextToLatex(String textToConvert, String newPageSeperator)
    {
        StringBuilder asLatex  = new StringBuilder();
        //add required start of latex document
        asLatex.append("\\documentclass[12pt]{article}\n\n");
        asLatex.append("\\begin{document}\n");
        //add the body of the document (add the data parsed from the pdf)
        asLatex = addDocumentBody(asLatex, textToConvert, newPageSeperator);
        //add required end of document
        asLatex.append("\\end{document}");
        return asLatex.toString();
    }

    private static StringBuilder addDocumentBody(StringBuilder textBuilder,String textToAdd, String newPageSeperator)
    {
        boolean prevLineWasPageBreak = false;

        for (String line : textToAdd.split("\n")){
            if(line.contains(newPageSeperator)){
                line = line.replace(newPageSeperator, "\\newpage\n");
                prevLineWasPageBreak = true;
            }

            if(prevLineWasPageBreak && !line.contains("\\newpage")){
                textBuilder.append("\\section{");
                textBuilder.append(line);
                textBuilder.append("}\n");
                prevLineWasPageBreak = false;
            }
            else{
                textBuilder.append(line);
                textBuilder.append("\n");
            }
        }
        return textBuilder;
    }
}
