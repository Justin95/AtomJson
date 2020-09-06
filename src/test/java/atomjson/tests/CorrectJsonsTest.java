
package atomjson.tests;

import atomjson.JsonParser;
import atomjson.exceptions.JsonException;
import atomjson.utils.TestingUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;

/**
 * Make sure that all the JSON files in valid_jsons parse without error.
 * @author Justin Bonner
 */
public class CorrectJsonsTest {
    
    private static final String CORRECT_JSON_DIR = "/valid_jsons";
    
    @Test
    public void testCorrectJsons() throws FileNotFoundException, IOException {
        for (File file : TestingUtil.getFiles(CORRECT_JSON_DIR)) {
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
    
}
