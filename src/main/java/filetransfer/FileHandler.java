package filetransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHandler {

    public static void writeString(String file, String data) {
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String readString(String file) {
        try {
            Path path = Paths.get(file);
            String data = Files.readString(path);
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
