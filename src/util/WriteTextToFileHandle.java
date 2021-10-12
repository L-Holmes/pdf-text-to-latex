package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        System.out.println("writing data to file: "+fileToWriteToPath);

        //
        /*
        Path path = Paths.get(System.getProperty("user.dir")+ fileToWriteToPath);
        System.out.println(Files.exists(path));
        Path path2 = Paths.get(fileToWriteToPath);
        System.out.println(Files.exists(path2));
        Path path3 = Paths.get("/Users/lindonholmes/Documents/personal_projects/notes_generator/project/static/year3_lecture_slides/SCC361/out/");
        System.out.println(Files.exists(path3));
        Path path4 = Paths.get("/Users/lindonholmes/Documents/personal_projects/notes_generator/project/static/year3_lecture_slides/SCC361/");
        System.out.println(Files.exists(path4));
         */

        //TODO: there needs to be a slash between the project and the static folders

        //

        File dataFile = new File(System.getProperty("user.dir")+ "/"+ fileToWriteToPath);
        try {
            dataFile.createNewFile();
            System.out.println("done this; created the file: "+ System.getProperty("user.dir")+ "/"+fileToWriteToPath);
        } catch (IOException e) {
            System.out.println("could not create the file: "+ System.getProperty("user.dir")+ "/"+ fileToWriteToPath);
            e.printStackTrace();
        }
        System.out.println("moving on to section 2");
        try {
            FileOutputStream outputStream = new FileOutputStream(dataFile, false);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
            BufferedWriter fileWriter = new BufferedWriter(outputWriter);
            PrintWriter textWriter = new PrintWriter(fileWriter, false);
            System.out.println("about to write the data to the file...");
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
