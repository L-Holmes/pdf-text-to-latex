import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ConvertOutTextToLatex implements Runnable {
    @Override
    public void run() {
        createLatexCopy();
        convertTheTextFileToLatex();
    }


    /**
     * creates a copy of the text file holding the parsed pdf output.
     * also changes the extension of the copied file to .tex
      */
    private void createLatexCopy()
    {
        Path copied = Paths.get(System.getProperty("user.dir")+"/static/out.tex");
        Path originalPath = Paths.get(System.getProperty("user.dir")+"/static/out.txt");
        try {
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("successfully created copy of out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void convertTheTextFileToLatex()
    {
        //source: https://stackoverflow.com/questions/30622160/running-a-shell-script-from-java-code
        //accessed: 24/03/2021
        try {
            String target = new String(System.getProperty("user.dir")+"/convert2texAndCompile.sh");
// String target = new String("mkdir stackOver");
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(target);
            proc.waitFor();
            StringBuffer output = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            System.out.println("### " + output);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
