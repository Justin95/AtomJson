
package atomjson.tests;

import atomjson.JsonHandlerPlus;
import atomjson.JsonParser;
import atomjson.JsonParsingState;
import atomjson.JsonPrimitive;
import atomjson.utils.TestingUtil;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;

/**
 *
 * @author Justin Bonner
 */
public class JsonHandlerPlusTest {
    
    private static final String TEST_JSON_FILEPATH = "/test_specific_jsons/json_handler_plus_test.json";
    
    @Test
    public void test() throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(TestingUtil.getFile(TEST_JSON_FILEPATH));
        JsonParser parser = JsonParser.getInstance(reader);
        parser.parse(new JsonHandlerPlusTestImpl());
        reader.close();
    }
    
    private static class JsonHandlerPlusTestImpl extends JsonHandlerPlus {

        @Override
        public boolean handleJsonPlus(JsonParsingState parsingState, String fieldName, JsonPrimitive value) {
            if (parsingState != JsonParsingState.READ_PRIMITIVE) {
                return true;
            }
            StringBuilder strBuilder = new StringBuilder();
            JsonBranch prev = null;
            for (JsonBranch jsonBranch : this.jsonTraversalView) {
                if (jsonBranch.getName() != null) {
                    strBuilder.append(jsonBranch.getName());
                } else if (prev != null && prev.getBranchType() == JsonBranchType.JSON_ARRAY) {
                    strBuilder.append('[');
                    strBuilder.append(((BranchArray)prev).getCurrentIndex());
                    strBuilder.append(']');
                }
                strBuilder.append("/");
                prev = jsonBranch;
            }
            if (prev != null && prev.getBranchType() == JsonBranchType.JSON_OBJECT) {
                strBuilder.append(fieldName);
            } else if (prev != null && prev.getBranchType() == JsonBranchType.JSON_ARRAY) {
                strBuilder.append('[');
                strBuilder.append(((BranchArray)prev).getCurrentIndex());
                strBuilder.append(']');
            }
            strBuilder.append(":");
            strBuilder.append(value.getUnderlyingString());
            System.out.println(strBuilder.toString());
            return true;
        }
        
    }
    
}
