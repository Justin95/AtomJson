
package atomjson;

import java.util.regex.Pattern;

/**
 *
 * @author Justin Bonner
 */
public enum JsonPrimitiveType {
    STRING("\".*\""), //anything between quotes
    NUMBER("-?\\d+(\\.\\d+)?([eE][+\\-]?\\d+)?"),
    BOOLEAN("(true)|(false)"),
    NULL("null"),
    ;

    /**
     * A Regex identifying this primitive type.
     * A primitiveStr of this JsonPrimitiveType must match
     * this and no other JsonPrimitiveType idRegex.
     */
    final Pattern idRegex;

    JsonPrimitiveType(String idRegex) {
        this.idRegex = Pattern.compile(idRegex);
    }
}
