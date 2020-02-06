
package atomjson;

import atomjson.exceptions.JsonException;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * Parse a JSON string as described here: https://www.json.org/json-en.html
 * @author Justin Bonner
 */
public class JsonParser {
    
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    
    private final Reader jsonSource;
    
    
    private JsonParser(Reader jsonSource) {
        if (jsonSource == null) {
            throw new NullPointerException("Reader cannot be null.");
        }
        this.jsonSource = jsonSource;
    }
    
    /**
     * Create a JsonParser that reads from the given Reader.
     * @param jsonSource the source of the JSON
     * @return the JsonParser
     */
    public static JsonParser getInstance(Reader jsonSource) {
        return new JsonParser(jsonSource);
    }
    
    /**
     * Create a JsonParser that reads from the given String.
     * @param jsonString the JSON string to parse
     * @return the JsonParser
     */
    public static JsonParser getInstance(String jsonString) {
        Reader jsonSource = new StringReader(jsonString);
        return new JsonParser(jsonSource);
    }
    
    /**
     * Create a JsonParser that reads from the given byte array.
     * @param jsonByteArray the JSON encoded in a byte array
     * @param charset the Charset of the String in the byte array
     * @return the JsonParser
     */
    public static JsonParser getInstance(byte[] jsonByteArray, Charset charset) {
        Reader jsonSource = new InputStreamReader(new ByteArrayInputStream(jsonByteArray), charset);
        return new JsonParser(jsonSource);
    }
    
    /**
     * Create a JsonParser that reads from the given byte array.
     * Assume the byte array is a 'UTF-8' string.
     * @param jsonByteArray the JSON encoded in a byte array
     * @return the JsonParser
     */
    public static JsonParser getInstance(byte[] jsonByteArray) {
        Reader jsonSource = new InputStreamReader(new ByteArrayInputStream(jsonByteArray), DEFAULT_CHARSET);
        return new JsonParser(jsonSource);
    }
    
    /**
     * Parse the JSON.
     * @param handler the JsonHandler to be called on parsing events
     * @throws JsonException if a parsing error occurs
     */
    public void parse(JsonHandler handler) throws JsonException {
        
    }
    
}
