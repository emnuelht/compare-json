package compare.json;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        File fileOld = new File("src/main/resources/fileOld.json");
        File fileNew = new File("src/main/resources/fileNew.json");

        // Printando o retorno da diferença entre um arquivo(json) e outro
        try {
            for (Map.Entry<String, String> entry : CompareJson.run(fileOld, fileNew).entrySet()) {
                System.out.println("Key: " + entry.getKey() + " >=< " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
