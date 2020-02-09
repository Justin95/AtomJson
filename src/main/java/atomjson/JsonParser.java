
package atomjson;

import atomjson.exceptions.JsonException;
import atomjson.exceptions.JsonSyntaxException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Stack;

/**
 * Parse a JSON string as described here: https://www.json.org/json-en.html
 * @author Justin Bonner
 */
public class JsonParser {
    
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    
    private final Reader jsonSource;
    //could make the parser stack only store bits instead of pointers to enum objects, would only benefit deeply nested jsons
    private final Stack<ParserState> parserStack;
    
    
    private JsonParser(Reader jsonSource) {
        if (jsonSource == null) {
            throw new NullPointerException("Reader cannot be null.");
        }
        this.jsonSource = jsonSource;
        this.parserStack = new Stack<>();
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
        JsonTokenizer tokenizer = new JsonTokenizer(new JsonReader(jsonSource));
        try {
            parse(handler, tokenizer);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }
    
    private void parse(JsonHandler handler, JsonTokenizer tokenizer) throws JsonException, IOException {
        JsonToken token = tokenizer.parseNextToken();
        if (token == JsonToken.BEGIN_OBJECT) {
            parserStack.push(ParserState.JSON_OBJECT);
        } else if (token == JsonToken.BEGIN_ARRAY) {
            parserStack.push(ParserState.JSON_ARRAY);
        } else {
            throw new JsonSyntaxException("Expected Object or Array. Found " + token.name() + ".");
        }
        boolean keepParsing = true;
        boolean justEntered = true;
        while(!parserStack.isEmpty() && keepParsing) {
            ParserState currState = parserStack.peek();
            if (currState == ParserState.JSON_OBJECT) {
                token = tokenizer.parseNextToken();
                if (token == JsonToken.END_OBJECT) {
                    parserStack.pop();
                    handler.handleJson(JsonParsingState.END_OBJECT, null, null);
                    justEntered = false;
                    continue;
                } else if (!justEntered && token == JsonToken.COMMA) {
                    token = tokenizer.parseNextToken();
                } else if (!justEntered && token != JsonToken.COMMA) {
                    throw new JsonSyntaxException("Expected comma or end object. Found " + token.name() + ".");
                }
                justEntered = false;
                if (token != JsonToken.STRING) {
                    throw new JsonSyntaxException("Expected key name in json object. Found " + token.name() + ".");
                }
                String name = tokenizer.readToken();
                token = tokenizer.parseNextToken();
                if (token != JsonToken.COLON) {
                    throw new JsonSyntaxException("Did not find colon after key name. Found " + token.name() + ".");
                }
                token = tokenizer.parseNextToken();
                JsonPrimitive value = null;
                JsonParsingState handlerState;
                switch(token) {
                    case STRING:
                        value = new JsonPrimitive(JsonPrimitiveType.STRING, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case NUMBER:
                        value = new JsonPrimitive(JsonPrimitiveType.NUMBER, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case BOOLEAN:
                        value = new JsonPrimitive(JsonPrimitiveType.BOOLEAN, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case NULL:
                        value = new JsonPrimitive(JsonPrimitiveType.NULL, "null");
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case BEGIN_OBJECT:
                        handlerState = JsonParsingState.BEGIN_OBJECT;
                        parserStack.push(ParserState.JSON_OBJECT);
                        justEntered = true;
                        break;
                    case BEGIN_ARRAY:
                        handlerState = JsonParsingState.BEGIN_ARRAY;
                        parserStack.push(ParserState.JSON_ARRAY);
                        justEntered = true;
                        break;
                    default:
                        throw new JsonSyntaxException("Did not find value token after key. Found " + token.name() + ".");
                }
                keepParsing = handler.handleJson(handlerState, name, value);
            } else if (currState == ParserState.JSON_ARRAY) {
                token = tokenizer.parseNextToken();
                if (token == JsonToken.END_ARRAY) {
                    parserStack.pop();
                    handler.handleJson(JsonParsingState.END_ARRAY, null, null);
                    justEntered = false;
                    continue;
                } else if (!justEntered && token == JsonToken.COMMA) {
                    token = tokenizer.parseNextToken();
                } else if (!justEntered && token != JsonToken.COMMA) {
                    throw new JsonSyntaxException("Expected comma or end array. Found " + token.name() + ".");
                }
                justEntered = false;
                JsonPrimitive value = null;
                JsonParsingState handlerState;
                switch(token) {
                    case STRING:
                        value = new JsonPrimitive(JsonPrimitiveType.STRING, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case NUMBER:
                        value = new JsonPrimitive(JsonPrimitiveType.NUMBER, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case BOOLEAN:
                        value = new JsonPrimitive(JsonPrimitiveType.BOOLEAN, tokenizer.readToken());
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case NULL:
                        value = new JsonPrimitive(JsonPrimitiveType.NULL, "null");
                        handlerState = JsonParsingState.READ_PRIMITIVE;
                        break;
                    case BEGIN_OBJECT:
                        handlerState = JsonParsingState.BEGIN_OBJECT;
                        parserStack.push(ParserState.JSON_OBJECT);
                        justEntered = true;
                        break;
                    case BEGIN_ARRAY:
                        handlerState = JsonParsingState.BEGIN_ARRAY;
                        parserStack.push(ParserState.JSON_ARRAY);
                        justEntered = true;
                        break;
                    default:
                        throw new JsonSyntaxException("Did not find value token in array. Found " + token.name());
                }
                keepParsing = handler.handleJson(handlerState, null, value);
            } else {
                throw new RuntimeException("Invalid parser state: " + currState);
            }
        }
        token = tokenizer.parseNextToken();
        if (token != JsonToken.END) {
            throw new JsonSyntaxException("Finished parsing json but source is not done. Found " + token.name() + ".");
        }
    }
    
    private static class JsonTokenizer {
        
        private final JsonReader reader;
        private final StringBuilder buffer;
        private boolean tokenReady;
        
        
        JsonTokenizer(JsonReader reader) {
            this.reader = reader;
            this.buffer = new StringBuilder();
            this.tokenReady = false;
        }
        
        public JsonToken calcNextToken() throws IOException {
            clearBuffer();
            tokenReady = true;
            int val = reader.read();
            if (val == -1) {
                return JsonToken.END;
            }
            char currChar = (char)val;
            switch(currChar) {
                case '{':
                    return JsonToken.BEGIN_OBJECT;
                case '}':
                    return JsonToken.END_OBJECT;
                case '[':
                    return JsonToken.BEGIN_ARRAY;
                case ']':
                    return JsonToken.END_ARRAY;
                case '"':
                    reader.finishString(buffer);
                    return JsonToken.STRING;
                case ',':
                    return JsonToken.COMMA;
                case ':':
                    return JsonToken.COLON;
                case 'n':
                    verify('n', "null");
                    return JsonToken.NULL;
                case 't':
                    verify('t', "true");
                    return JsonToken.BOOLEAN;
                case 'f':
                    verify('f', "false");
                    return JsonToken.BOOLEAN;
            }
            if (currChar == '+' || currChar == '-' || Character.isDigit(currChar)) { //number
                buffer.append(currChar);
                reader.primitiveRead(buffer);
                String num = buffer.toString();
                if(!JsonPrimitiveType.NUMBER.idRegex.matcher(num).matches()) { //TODO maybe do something better than a toString and a regex match
                    throw new JsonSyntaxException("'" + num + "' was not a number as expected.");
                }
                return JsonToken.NUMBER;
            }
            throw new JsonSyntaxException("Could not tokenize '" + currChar + "'.");
        }
        
        public JsonToken parseNextToken() throws IOException {
            JsonToken token = calcNextToken();
            //System.out.println(token.name());
            return token;
        }
        
        private void verify(char start, String expected) throws IOException {
            buffer.append(start);
            for (int i = 1; i < expected.length(); i++) {
                char exp = expected.charAt(i);
                char found = reader.reqRead();
                buffer.append(found);
                if (exp != found) {
                    throw new JsonSyntaxException("Expected to read '" + expected + "' but read '" + buffer.toString() + "'.");
                }
            }
        }
        
        public String readToken() {
            if (!tokenReady) {
                throw new IllegalStateException("Tried to read a token when not ready.");
            }
            String token = buffer.toString();
            clearBuffer();
            tokenReady = false;
            return token;
        }
        
        public void skipToken() {
            if (!tokenReady) {
                throw new IllegalStateException("Tried to read a token when not ready.");
            }
            clearBuffer();
            tokenReady = false;
        }
        
        private void clearBuffer() {
            if (buffer.length() != 0) {
                buffer.setLength(0);
                //possibly buffer = new StringBuilder() if the capacity has gotten super large
            }
        }
        
    }
    
    private static class JsonReader {
        
        private final Reader reader;
        private boolean inString;
        private boolean isEscaped;
        private boolean skipWhitespace;
        private Character guaranteedNextValue;
        
        private JsonReader(Reader source) {
            this.reader = source;
            this.inString = false;
            this.isEscaped = false;
            this.skipWhitespace = true;
            this.guaranteedNextValue = null;
        }
        
        public void finishString(StringBuilder dest) throws IOException {
            if (!inString) {
                throw new IllegalStateException("JsonReader is not reading a string.");
            }
            while(inString) {
                dest.append(reqRead());
            }
            dest.deleteCharAt(dest.length() - 1); //remove trailing '"'
        }
        
        public void primitiveRead(StringBuilder dest) throws IOException {
            if (inString) {
                throw new IllegalStateException("JsonReader is reading a string during a whitespace terminated read.");
            }
            this.skipWhitespace = false;
            char c;
            while(!Character.isWhitespace((c = reqRead())) && c != ',' && c != '}' && c != ']') {
                dest.append(c);
            }
            if (!Character.isWhitespace(c)) {
                this.guaranteedNextValue = c; //put back commas or closing brakets
            }
            this.skipWhitespace = true;
        }
        
        private boolean skipWhitespace() {
            return !inString && skipWhitespace;
        }
        
        public int read() throws IOException {
            if (guaranteedNextValue != null) {
                char value = guaranteedNextValue;
                this.guaranteedNextValue = null;
                return value;
            }
            if (isEscaped && !inString) {
                throw new IllegalStateException("Json Reader is escaped while not in a string.");
            }
            int nextResult = reader.read();
            if (skipWhitespace()) {
                while(Character.isWhitespace((char)nextResult)) {
                    nextResult = reader.read();
                    if (nextResult == -1) {
                        return -1;
                    }
                }
            }
            if (nextResult == -1) {
                if (inString) {
                    throw new JsonSyntaxException("EOF in a string.");
                } else {
                    return -1;
                }
            }
            char currChar = (char)nextResult;
            if (inString && (currChar <= 31 )) {
                //this case would be handled fine by the parser but is not valid json
                throw new JsonException("Unescaped control character not allowed in string.");
            }
            if (isEscaped) {
                isEscaped = false;
                switch(currChar) {
                    case '\\':
                    case '"':
                    case '/':
                        return currChar;
                    case 'b':
                        return '\b';
                    case 'f':
                        return '\f';
                    case 'n':
                        return '\n';
                    case 'r':
                        return '\r';
                    case 't':
                        return '\t';
                    case 'u':
                        //parse next 4 hex chars as unicode, uncommon code path string allocation fine
                        return parseUnicodeHex(new String(new char[]{reqRead(), reqRead(), reqRead(), reqRead()}));
                }
            }
            if (inString && currChar == '\\') {
                isEscaped = true;
                return read();
            }
            if (currChar == '"') {
                inString = !inString;
            }
            return currChar;
        }
        
        //TODO write a lot of tests for this
        private char parseUnicodeHex(String hex) {
            if (hex.length() != 4) {
                throw new RuntimeException("parseUnicodeHex() called improperly.");
            }
            int value = Integer.parseInt(hex, 16);
            if (!Character.isValidCodePoint(value)) {
                throw new JsonException("Invalid unicode character 'u" + hex + "'.");
            }
            if (Character.charCount(value) == 1) {
                return (char)value;
            } else { //charCount == 2
                this.guaranteedNextValue = Character.lowSurrogate(value);
                return Character.highSurrogate(value);
            }
        }
        
        private char reqRead() throws IOException {
            int value = read();
            if (value == -1) {
                throw new JsonSyntaxException();
            }
            return (char)value;
        }
        
    }
    
    private static enum JsonToken {
        BEGIN_OBJECT,
        END_OBJECT,
        BEGIN_ARRAY,
        END_ARRAY,
        STRING,
        COLON,
        COMMA,
        NUMBER,
        BOOLEAN,
        NULL,
        END
    }
    
    private static enum ParserState {
        JSON_OBJECT,
        JSON_ARRAY,
        
    }
    
}
