
package atomjson;

import atomjson.exceptions.JsonException;

/**
 * Represent a JSON primitive value. ie a boolean, string, number or null.
 * @author Justin Bonner
 */
public class JsonPrimitive {
    
    /**
     * The underlying string of this Json primitive.
     * ex:
     * "true" or "false" for a boolean
     * "1", "1.3", or "2.3e-3" for a number
     * "null" for null
     * ""A String"", or ""The full text of 'Moby Dick'"" for a string.
     * In the case of strings the primitiveStr starts and ends with a '"' character.
     */
    private final String primitiveStr;
    
    /**
     * The Type of this JsonPrimitive.
     */
    private final JsonPrimitiveType type;
    
    /**
     * 
     * @param primitiveStr the underlying string of this Json primitive
     */
    JsonPrimitive(JsonPrimitiveType type, String primitiveStr) {
        this.primitiveStr = primitiveStr;
        this.type = type;
    }
    
    /**
     * Get the type of this JsonPrimitive.
     * @return the type
     */
    public JsonPrimitiveType getType() {
        /*
        if (this.type == null) {
            for (JsonPrimitiveType potentialType : JsonPrimitiveType.values()) {
                if (potentialType.idRegex.matcher(primitiveStr).matches()) {
                    this.type = potentialType;
                    break;
                }
            }
            throw new JsonSyntaxException("Could not parse type of primitive.");
        }
        */
        return type;
    }
    
    /**
     * Determine if this JsonPrimitive is null.
     * @return true if this is null
     */
    public boolean isNull() {
        return getType() == JsonPrimitiveType.NULL;
    }
    
    /**
     * Get this JsonPrimitive as a boolean. This should only
     * be called on JsonPrimtives that are booleans.
     * @return 
     */
    public boolean getAsBoolean() {
        if (getType() != JsonPrimitiveType.BOOLEAN) {
            throw new JsonException("Tried to read " + getType().name() + " as a boolean.");
        }
        if (primitiveStr.equals("true")) {
            return true;
        } else if (primitiveStr.equals("false")) {
            return false;
        } else {
            throw new JsonException("Could not parse boolean.");
        }
    }
    
    /**
     * Get this JsonPrimitive as a String. This should only be
     * called on JsonPrimitives that are Strings.
     * @return a String
     */
    public String getAsString() {
        if (getType() != JsonPrimitiveType.STRING) {
            throw new JsonException("Tried to read " + getType().name() + " as a String.");
        }
        return primitiveStr;
    }
    
    /**
     * Determine if this JsonPrimitive is a number and has no decimal component and
     * does not contain a negative exponent.
     * This method does not check if this number can fit into a 32 bit signed int.
     * This method will consider '1000e-1' as not an integer due to
     * the negative exponent despite being an integer. Numbers like this will
     * have to be parsed as decimal numbers.
     * @return true if this is an integer number
     */
    public boolean isInteger() {
        return getType() == JsonPrimitiveType.NUMBER 
            && !primitiveStr.contains(".")
            && !primitiveStr.contains("e-")
            && !primitiveStr.contains("E-");
    }
    
    /**
     * Get this JsonPrimitive as a long. This method should only
     * be called on JsonPrimitives that are integers.
     * @return a long
     */
    public long getAsLong() {
        if (getType() != JsonPrimitiveType.NUMBER) {
            throw new JsonException("Tried to read " + getType().name() + " as a Long.");
        }
        if (!isInteger()) {
            throw new JsonException("Tried to read a non integer as a Long.");
        }
        //primitiveStr will contain at most one e or E
        String expoChar = null;
        if (primitiveStr.contains("e")) {
            expoChar = "e";
        } else if (primitiveStr.contains("E")) {
            expoChar = "E";
        }
        if (expoChar != null) {
            String[] expoSplit = primitiveStr.split(expoChar);
            long base = Long.parseLong(expoSplit[0]);
            int exponent = Integer.parseInt(expoSplit[1]);
            if (exponent < 0) {
                throw new JsonException("Tried to parse an integer out of a number with a negative exponent.");
            }
            long multiplier = (long) Math.pow(10, exponent);
            return base * multiplier;
        }
        return Long.parseLong(primitiveStr);
    }
    
    /**
     * Get this JsonPrimitive as a double. This should
     * only be called on JsonPrimitives that are numbers.
     * @return a double
     */
    public double getAsDouble() {
        if (getType() != JsonPrimitiveType.NUMBER) {
            throw new JsonException("Tried to read " + getType().name() + " as a Double.");
        }
        return Double.parseDouble(primitiveStr);
    }
    
}
