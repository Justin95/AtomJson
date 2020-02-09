
package atomjson.tests;

import atomjson.JsonParser;
import atomjson.exceptions.JsonException;
import atomjson.utils.TestingUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Justin Bonner
 */
public class IncorrectJsonsTest {
    
    private static final String INCORRECT_JSON_DIR = "invalid_jsons";
    
    @Test
    public void testCorrectJsons() throws FileNotFoundException, IOException {
        for (File file : TestingUtil.getFiles(INCORRECT_JSON_DIR)) {
            FileReader reader = new FileReader(file);
            JsonParser parser = JsonParser.getInstance(reader);
            try {
                parser.parse((state, name, value) -> true);
            } catch (JsonException e) {
                System.out.println("Correctly failed to parse '" + file.getName() + "'. Message: " + e.getMessage());
                continue;
            } finally {
                reader.close();
            }
            System.out.println("Incorrectly successfully parsed '" + file.getName() + "' but should have failed.");
            fail();
        }
    }
    
}
