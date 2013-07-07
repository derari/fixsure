package org.cthul.fixsure;

import org.cthul.fixsure.iterables.LazyValues;

/**
 * Creates generators that will always produce the same values,
 * unless they are {@link Distribution}-based 
 * (in this case, they use the same distribution).
 * <p>
 * A generator that implements this interface will return a snapshot of its
 * current state. 
 * Stateless generators that implement this interface may return themselves.
 * <p>
 * Generators that depend on a {@link Distribution} do not have reproducable
 * behavior. Use {@link LazyValues#all()} to cache their output.
 */
public interface GeneratorTemplate<T> {
    
    /**
     * Creates a generator.
     * @return generator
     */
    Generator<T> newGenerator();
    
}
