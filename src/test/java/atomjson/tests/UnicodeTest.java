
package atomjson.tests;

import atomjson.JsonParser;
import atomjson.JsonParsingState;
import atomjson.exceptions.JsonException;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 * Test that unicode escapes are processed correctly,
 * that bad escapes generate the proper exception.
 * Unicode characters above uFFFF are not officially supported 
 * in strings and are not guaranteed to work correctly.
 * Escapes above uFFFF will not as the JSON spec does not allow them.
 * @author Justin Bonner
 */
public class UnicodeTest {
    
    private String result = null;
    
    @Test
    public void testUnicode() throws Exception {
        System.out.println("Testing unicode escapes '\\u0000' to '\\uFFFF'.");
        StringBuilder str = new StringBuilder();
        str.append("{\"test str\": \"");
        for (int i = 0x0000; i <= 0xFFFF; i++) {
            str.append("\\u");
            String hexStr = Integer.toHexString(i).toUpperCase();
            for (int j = 0; j < 4 - hexStr.length(); j++) {
                str.append('0');
            }
            str.append(hexStr);
        }
        str.append("\"}");
        JsonParser parser = JsonParser.getInstance(str.toString());
        
        parser.parse((parsingState, fieldName, value) -> {
            if (parsingState == JsonParsingState.READ_PRIMITIVE && fieldName.equals("test str")) {
                result = value.getAsString();
            }
            return true;
        });
        boolean failed = false;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) != (char)i) {
                System.out.println("Test failed for char '" + (char)i + "' (" + i + ").");
                failed = true;
            }
        }
        if (failed) {
            fail();
        }
    }
    
    private static final String[] BAD_ESCAPES = new String[]{
        "\\u00",
        "\\u",
        "\\u89RF",
        "\\u\\u0030\\u0030\\u0030\\u0030", //this would have parsed originally
        "\\u"
    };
    
    /**
     * Ensure that bad unicode escapes do not parse.
     */
    @Test
    public void testBadEscapes() {
        for (String escape : BAD_ESCAPES) {
            StringBuilder str = new StringBuilder();
            str.append("{\"test str\": \"");
            str.append(escape);
            str.append("\"}");
            JsonParser parser = JsonParser.getInstance(str.toString());
            try {
                parser.parse((parsingState, fieldName, value) -> true); //should fail
                System.out.println("Incorrect successful parsing of: " + str.toString());
                fail();
            } catch (JsonException e) {
                //expected
            }
        }
    }
    
    
}
