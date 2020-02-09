
package atomjson;

import java.util.Stack;

/**
 * A class that may be extended for additional JSON handling features.
 * @author Justin Bonner
 */
public abstract class JsonHandlerPlus implements JsonHandler {
    
    private final Stack<JsonBranch> jsonStack;
    protected final ReadonlyStack<JsonBranch> jsonTraversalView;
    
    public JsonHandlerPlus() {
        this.jsonStack = new Stack<>();
        this.jsonTraversalView = new ReadonlyStack<>(jsonStack);
    }
    
    //make this method final so it isnt accidentally overridden instead of handleJsonPlus
    @Override
    public final boolean handleJson(JsonParsingState parsingState, String fieldName, JsonPrimitive value) {
        boolean status = handleJsonPlus(parsingState, fieldName, value);
        switch(parsingState) {
            case BEGIN_OBJECT:
                jsonStack.push(new BranchObject(fieldName));
                break;
            case BEGIN_ARRAY:
                jsonStack.push(new BranchArray(fieldName));
                break;
            case END_OBJECT:
            case END_ARRAY:    
                jsonStack.pop();
                if (!jsonStack.isEmpty()) {
                    JsonBranch prev = jsonStack.peek();
                    if (prev.getBranchType() == JsonBranchType.JSON_ARRAY) {
                        ((BranchArray)prev).index++;
                    }
                }
                break;
            case READ_PRIMITIVE:
                JsonBranch prev = jsonStack.peek();
                if (prev.getBranchType() == JsonBranchType.JSON_ARRAY) {
                    ((BranchArray)prev).index++;
                }
                break;
        }
        return status;
    }
    
    /**
     * This method functions the same as JsonHandler.handleJson. Override this method
     * for custom parsing handling.
     * @param parsingState a representation of what is currently being parsed
     * @param fieldName the fieldName of the parsed field. This is null for the root object, any array entries, and null on END_OBJECT and END_ARRAY parsing states.
     * @param value the primitive value being read. This is only non-null when parsingState is READ_PRIMITIVE
     * @return should parsing continue, true if parsing should continue, false for an early stop
     */
    public abstract boolean handleJsonPlus(JsonParsingState parsingState, String fieldName, JsonPrimitive value);
    
    protected static enum JsonBranchType {
        JSON_OBJECT,
        JSON_ARRAY
    }
    
    protected interface JsonBranch {
        JsonBranchType getBranchType();
        
        /**
         * The name of this json object or array. Will be null if this object or array is not named.
         * ie the root or in an array.
         * @return the name
         */
        String getName();
        
    }
    
    protected static class BranchObject implements JsonBranch {
        
        private final String name;
        
        private BranchObject(String name) {
            this.name = name;
        }
        
        @Override
        public JsonBranchType getBranchType() {
            return JsonBranchType.JSON_OBJECT;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
    }
    
    protected static class BranchArray implements JsonBranch {
        
        /**
         * The name of this json array. Will be null if this array is not named. ie the root or in an array.
         */
        private final String name;
        private int index;
        
        private BranchArray(String name) {
            this.name = name;
            this.index = 0;
        }
        
        @Override
        public JsonBranchType getBranchType() {
            return JsonBranchType.JSON_ARRAY;
        }
        
        /**
         * Get the index of the array currently being parsed.
         * @return the current index
         */
        public int getCurrentIndex() {
            return index;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
    }
    
}
