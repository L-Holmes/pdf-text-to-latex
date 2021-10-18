package main;

import main.file_choosing.FileSelector;
import main.util.WriteTextToFileHandle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/*
TODO:

-test for different pdfs
-test the paragraph and line search thing

 */
class Main
{
    /*
    steps for notes:
    -enter the pdf location
    -run program
    -create pdf output
    -check pdf output against original for issues with this app
        - adjust code if necessary
    -compare latex file with output pdf, adjusting where necessary
    -save latex file as final version.
    -create pdf output.
     */
    public static void main(String[] args)
    {
        Main inst = new Main();
        inst.generateOutputQuiz();

        /*
        TODO:

         steps now:
         -make folder called generated outp;ut that holds all of the files
            -maybe make a seperate folder containng just the latex and output pdf?
         -try with the full pdf
         -try with all of the pdfs from this week

         */

        /*
        TODO (OTHER)
        - make the process of entering a new file easier
            * currently have to change both files in the main code, and in the pdflatex shell script

        - maybe remove repeat pictures for each page?
            * could maybe check if an image exists before creating a new image png file

        - Possibly put pictures side by side?
        - possibly remove page numbers
            - check for standalone numbers
            - that are also = previous page number + 1.
            - then remove that line
        - is there still the 2 empty slides at the end?
        - automatically add bullet points if there was none in the first place
         */
    }

    public void generateOutputQuiz()
    {
        String inputPdfLocation = new FileSelector().selectPdfFile();
        String inputPdfFromProjectRootLocation = inputPdfLocation.substring(inputPdfLocation.indexOf(".")+2);//+2 removes the "./" at the start

        String inputWithDashOutAdded = inputPdfFromProjectRootLocation.substring(0, inputPdfFromProjectRootLocation.lastIndexOf(".")) + "-out"+ inputPdfFromProjectRootLocation.substring(inputPdfFromProjectRootLocation.lastIndexOf("."));
        String outputTextFileFromProjectRootLocation = replaceFilePathExtensionWithTextFileExtension(inputWithDashOutAdded);

        RunProgram runner = new RunProgram(inputPdfFromProjectRootLocation, outputTextFileFromProjectRootLocation);
        System.out.println("finished running...");

        //compile latex into pdf
        Main m = new Main();
        try {
            System.out.println("aboot to compile!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            m.executeScript(outputTextFileFromProjectRootLocation);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("compiled into latex!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");


        organiseOutputIntoFolders(inputPdfFromProjectRootLocation, outputTextFileFromProjectRootLocation);

    }

    //https://stackoverflow.com/questions/49558546/run-shell-script-from-java-code
    //accessed: 16/10/21
    public void executeScript(String outputTextFileFromProjectRootLocation) throws IOException, InterruptedException {
        File wd = new File(System.getProperty("user.dir") + "/");

        String command = "./convert2texAndCompile.sh";

        int lastSlashIndexInInputFilePath = outputTextFileFromProjectRootLocation.lastIndexOf("/");
        String folderLocation = outputTextFileFromProjectRootLocation.substring(0, lastSlashIndexInInputFilePath);
        String nameOfOutputLatexFileToCompile = RunProgram.replaceFilePathExtensionWithLatexFileExtension(outputTextFileFromProjectRootLocation.substring(lastSlashIndexInInputFilePath + 1)); // +1 removes the "/" at the start


        //String[] cmd = { command,  "static/test" , "SCC361-Wk1-L1-TEST-out.tex"};
        String[] cmd = { command,  folderLocation , nameOfOutputLatexFileToCompile};

        Process p = Runtime.getRuntime().exec(cmd, null, wd);
        System.out.println("helf through.?!");
        //TODO: gets stuck here for some reason???????????????????????????????????????

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));


        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        line = "";
        while ((line = errorReader.readLine()) != null) {
            System.out.println(line);
        }

        p.waitFor();//TODO: MAYBE PUT THIS AT END?????????
    }

    public static String replaceFilePathExtensionWithTextFileExtension(String inputFilePath)
    {
        //remove after the last dot
        if (inputFilePath == null || inputFilePath.length() <= 0 ) return null;
        int endIndex = inputFilePath.lastIndexOf(".");
        if (endIndex == -1) return null;
        String asLatex = inputFilePath.substring(0, endIndex);
        //add .tex
        asLatex = asLatex.concat(".txt");
        return asLatex;
    }

    private void organiseOutputIntoFolders(String inputFilePath, String outputFilePath) {
        String userDir = System.getProperty("user.dir");
        //mkdir generated-output-[name]
        String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf("/"));
        String inputFileNameWithoutExtension = inputFileName.substring(1, inputFileName.lastIndexOf(".")); //+1 to remove leading "/"
        String folderTheOutputFilesAreIn = outputFilePath.substring(0, outputFilePath.lastIndexOf("/"));
        String outputFolderName = "/generated-output-".concat(inputFileNameWithoutExtension);
        String outputFilesFolderFullPath =userDir.concat("/".concat(folderTheOutputFilesAreIn));
        String outputDir = outputFilesFolderFullPath.concat(outputFolderName);
        new File(outputDir).mkdirs();

        String outputFileWithoutExtensionFullPath = outputFilesFolderFullPath.concat("/").concat(inputFileNameWithoutExtension);

        //move the tex and pdf to generated-output
        String pdfFileSource = outputFileWithoutExtensionFullPath.concat("-out.pdf");
        String latexFileSource = outputFileWithoutExtensionFullPath.concat("-out.tex");

        String pdfFullFilePathName = outputDir.concat(pdfFileSource.substring(pdfFileSource.lastIndexOf("/")));
        String latexFullFilePathName = outputDir.concat(latexFileSource.substring(latexFileSource.lastIndexOf("/")));

        Path pdfSourcePath = Paths.get(pdfFileSource);
        Path pdfNewPath = Paths.get(pdfFullFilePathName);

        Path latexSourcePath = Paths.get(latexFileSource);
        Path latexNewPath = Paths.get(latexFullFilePathName);

        try {
            Files.move(pdfSourcePath, pdfNewPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("pdf output file already moved or doesn't exist");
        }

        try {
            Files.move(latexSourcePath, latexNewPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("latex file already moved or doesn't exist");
        }

        //mkdir other,
        String otherDir = outputDir.concat("/other");
        new File(otherDir).mkdirs();

        //move the aux, log, txt to other

        String logFileSource = outputFileWithoutExtensionFullPath.concat("-out.log");
        String textFileSource = outputFileWithoutExtensionFullPath.concat("-out.txt");
        String auxFileSource = outputFileWithoutExtensionFullPath.concat("-out.aux");

        String logFullFilePathName = otherDir.concat(logFileSource.substring(logFileSource.lastIndexOf("/")));
        String textFullFilePathName = otherDir.concat(textFileSource.substring(textFileSource.lastIndexOf("/")));
        String auxFullFilePathName = otherDir.concat(auxFileSource.substring(auxFileSource.lastIndexOf("/")));

        Path logSourcePath = Paths.get(logFileSource);
        Path logNewPath = Paths.get(logFullFilePathName);

        Path textSourcePath = Paths.get(textFileSource);
        Path textNewPath = Paths.get(textFullFilePathName);


        Path auxSourcePath = Paths.get(auxFileSource);
        Path auxNewPath = Paths.get(auxFullFilePathName);

        try {
            Files.move(logSourcePath, logNewPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("log file already moved or doesn't exist");
        }
        try {
            Files.move(textSourcePath, textNewPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("text file already moved or doesn't exist");
        }
        try {
            Files.move(auxSourcePath, auxNewPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("aux file already moved or doesn't exist");
        }

        //move the images folder
        String imagesFolderLocation = outputFilesFolderFullPath.concat("/images");
        String newImagesFolderDestination = outputDir.concat("/images");

        Path imageFolderPath = Paths.get(imagesFolderLocation);
        Path newImageFolderDestinationPath = Paths.get(newImagesFolderDestination);

        try {
            Files.move(imageFolderPath, newImageFolderDestinationPath, REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("images folder already moved or doesn't exist");
        }
    }

}