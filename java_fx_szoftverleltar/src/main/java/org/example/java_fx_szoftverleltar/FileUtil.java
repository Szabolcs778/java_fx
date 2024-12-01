package org.example.java_fx_szoftverleltar;

import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void saveToFile(String fileName, String content) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content);
        }
    }
}
