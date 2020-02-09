
package atomjson.tests;

import java.io.Reader;

/**
 * Test a deeply nested JSON array.
 * @author Justin Bonner
 */
public class DeepNestTest extends AbstractJsonFromReaderTest {
    
    @Override
    public boolean shouldPass() {
        return true;
    }
    
    @Override
    public Reader getTestJson() {
        final int DEPTH = 100000000;
        return new TestReader(DEPTH);
    }
    
    private class TestReader extends Reader {
        
        private long index;
        private final long depth;
        
        TestReader(long depth) {
            index = 1;
            this.depth = depth;
        }
        
        @Override
        public int read() {
            if (index <= depth) {
                index++;
                return '[';
            } else if (index <= depth * 2) {
                index++;
                return ']';
            } else {
                return -1;
            }
        }
        
        @Override
        public int read(char[] dest, int a, int b) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void close() {
            
        }
        
    }
    
}
