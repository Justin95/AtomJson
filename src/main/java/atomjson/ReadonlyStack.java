
package atomjson;

import java.util.Iterator;
import java.util.Stack;

/**
 * Java doesn't have an Unmodifiable Stack class.
 * @author Justin Bonner
 */
public class ReadonlyStack<E> implements Iterable {
    
    private final Stack<E> stack;
    
    public ReadonlyStack(Stack<E> stack) {
        this.stack = stack;
    }
    
    public E peek() {
        return stack.peek();
    }
    
    /**
     * Read the element at the given position in the stack.
     * 0 is the bottom of the stack. size() - 1 is the top of the stack.
     * @param index
     * @return 
     */
    public E elementAt(int index) {
        return stack.elementAt(index);
    }
    
    /**
     * Get the index of the top of the stack.
     * @return 
     */
    public int topIndex() {
        return size() - 1;
    }
    
    public int size() {
        return stack.size();
    }
    
    public boolean isEmpty() {
        return stack.isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return stack.iterator();
    }
    
}
