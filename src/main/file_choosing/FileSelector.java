package main.file_choosing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelector
{
    public FileSelector(){}

    /**
     * Opens a gui file chooser, where the user can select the pdf file that they wish to use/open
     * @return the path to the pdf file that the user selected using the gui file chooser
     */
    public String selectPdfFile()
    {
        File foundFile;
        String filePath;
        JFileChooser chooser = new JFileChooser();
        chooser = setupChooser(chooser);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            foundFile = chooser.getSelectedFile();
            filePath = foundFile.getAbsolutePath();
            return filePath;
        } else {
            return null;
        }
    }

    private JFileChooser setupChooser(JFileChooser chooser)
    {
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("input pdf file selector");
        chooser = addPdfFileFilter(chooser);
        return chooser;
    }

    private JFileChooser addPdfFileFilter(JFileChooser chooser)
    {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF format (.pdf)", "pdf");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        return chooser;
    }
}