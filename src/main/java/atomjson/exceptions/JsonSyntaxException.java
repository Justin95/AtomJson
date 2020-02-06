
package atomjson.exceptions;

/**
 * An exception thrown when incorrect JSON syntax is encountered.
 * @author Justin Bonner
 */
public class JsonSyntaxException extends JsonException {
    
    public JsonSyntaxException() {
        
    }
    
    public JsonSyntaxException(String message) {
        super(message);
    }
    
    public JsonSyntaxException(String message, Throwable e) {
        super(message, e);
    }
    
    public JsonSyntaxException(Throwable e) {
        super(e);
    }
    
}
