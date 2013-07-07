package org.cthul.fixsure;

/**
 * A {@link Generator} that allows random access on its elements.
 * Its elements don't need to be unique.
 * <p>
 * Calls to {@link #value(long)} should not affect calls to {@link #next()},
 * and vice versa.
 */
public interface Sequence<T> extends Generator<T> {
    
    /**
     * Number of elements that can be accessed through {@link #value(long)};
     * -1 if any positive long is accepted.
     * @return length
     */
    long length();
    
    /**
     * Returns the nth element. 
     * May throw an exception if {@code n >=} {@link #length()}.
     * @param n
     * @return value
     */
    T value(long n);
    
}
