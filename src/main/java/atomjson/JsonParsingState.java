
package atomjson;

/**
 *
 * @author Justin Bonner
 */
public enum JsonParsingState {
    BEGIN_OBJECT,
    END_OBJECT,
    BEGIN_ARRAY,
    END_ARRAY,
    READ_PRIMITIVE
}
