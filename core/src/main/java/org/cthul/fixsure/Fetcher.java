package org.cthul.fixsure;

import org.cthul.fixsure.iterables.EagerValues;
import org.cthul.fixsure.iterables.LazyValues;

/**
 * Retrieves elements from a generator. Fetching may occur eager or lazy.
 * <p>
 * The integer generated is the number of elements that would have been fetched
 * (May be different for each call).
 * @see LazyValues
 * @see EagerValues
 */
public interface Fetcher extends Generator<Integer> {

    /**
     * The next number of elements that would have been fetched.
     * @return length
     */
    int nextLength();

    /**
     * Fetches elements from {@code generator}
     * @param <T>
     * @param generator
     * @return values
     */
    <T> Values<T> of(Generator<T> generator);
        
    /**
     * Fetches elements from each genereator.
     * @param <T>
     * @param generators
     * @return values
     */
    <T> Values<T> ofEach(Generator<? extends T>... generators);
    
}
