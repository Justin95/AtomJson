
package atomjson;

/**
 * The handler for JSON parsing. This method gets called on every array or object beginning and ending and for each JSON primitive that
 * is parsed.
 * @author Justin Bonner
 */
public interface JsonHandler {
    
    /**
     * The handler for JSON parsing. This method gets called on every array or object beginning and ending and for each JSON primitive that
     * is parsed.
     * @param parsingState a representation of what is currently being parsed
     * @param fieldName the fieldName of the parsed field. This is null for the root object, any array entries, and null on END_OBJECT and END_ARRAY parsing states.
     * @param value the primitive value being read. This is only non-null when parsingState is READ_PRIMITIVE
     */
    public void handleJson(JsonParsingState parsingState, String fieldName, JsonPrimitive value);
    
}
