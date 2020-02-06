
package atomjson.exceptions;

/**
 * An exception that is thrown when something goes
 * wrong when parsing the JSON.
 * @author Justin Bonner
 */
public class JsonException extends RuntimeException {
    
    public JsonException() {
        
    }
    
    public JsonException(String message) {
        super(message);
    }
    
    public JsonException(String message, Throwable e) {
        super(message, e);
    }
    
    public JsonException(Throwable e) {
        super(e);
    }
    
}
