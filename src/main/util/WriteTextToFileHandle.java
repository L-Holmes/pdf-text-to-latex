package main.util;

import java.io.*;

/**
 * used to write text to a given file location
 */
public class WriteTextToFileHandle {
    private String fileToWriteToPath = null;
    private String dataToWrite = null;

    public WriteTextToFileHandle(String pathToFile, String dataToWriteToFile)
    {
        dataToWrite = dataToWriteToFile;
        fileToWriteToPath = pathToFile;
    }

    /**
     * writes game data to non-volatile location
     */
    public void writeDataToFile()
    {

        File dataFile = new File(System.getProperty("user.dir")+ "/"+ fileToWriteToPath);
        try {
            dataFile.createNewFile();
        } catch (IOException e) {
            System.out.println("could not create the file: "+ System.getProperty("user.dir")+ "/"+ fileToWriteToPath);
            e.printStackTrace();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(dataFile, false);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter fileWriter = new BufferedWriter(outputWriter);
            PrintWriter textWriter = new PrintWriter(fileWriter, false);
            textWriter.write(dataToWrite);
            textWriter.flush();
        } catch (FileNotFoundException e) {
            System.out.println("file: '"+ fileToWriteToPath+ "' could not be found...");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("could not encode file: '"+ fileToWriteToPath);
            e.printStackTrace();
        }
    }
}
//writing to files
//https://stackoverflow.com/questions/26380744/which-is-the-best-way-to-create-file-and-write-to-it-in-java
//accessed: 25/02/2021
