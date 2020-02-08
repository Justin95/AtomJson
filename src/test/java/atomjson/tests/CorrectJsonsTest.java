
package atomjson.tests;

import atomjson.JsonParser;
import atomjson.exceptions.JsonException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

/**
 *
 * @author Justin Bonner
 */
public class CorrectJsonsTest {
    
    private static final String CORRECT_JSON_DIR = "valid_jsons";
    
    @Test
    public void testCorrectJsons() throws FileNotFoundException, IOException {
        for (File file : getFiles()) {
            FileReader reader = new FileReader(file);
            JsonParser parser = JsonParser.getInstance(reader);
            try {
                parser.parse((state, name, value) -> true);
            } catch (JsonException e) {
                System.out.println("Failed to parse '" + file.getName() + "'.");
                throw e;
            } finally {
                reader.close();
            }
            System.out.println("Successfully parsed '" + file.getName() + "'.");
        }
    }
    
    public static File[] getFiles() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(CORRECT_JSON_DIR);
        String path = url.getPath();
        return new File(path).listFiles();
    }
    
}
