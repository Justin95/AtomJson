
package atomjson.tests;

import atomjson.JsonParser;
import atomjson.exceptions.JsonException;
import java.io.Reader;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Justin Bonner
 */
public abstract class AbstractJsonFromReaderTest {
    
    public abstract Reader getTestJson();
    
    public abstract boolean shouldPass();
    
    @Test
    public void testJsonFromReader() {
        JsonParser parser = JsonParser.getInstance(getTestJson());
        try {
            parser.parse((state, name, value) -> true);
        } catch (JsonException e) {
            if (shouldPass()) {
                System.err.println(e);
                fail();
            } else {
                return;
            }
        }
        if (!shouldPass()) {
            System.err.println("Parsed json but shouldn't have.");
            fail();
        }
    }
    
}
