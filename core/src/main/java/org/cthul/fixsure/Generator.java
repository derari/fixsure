package org.cthul.fixsure;

/**
 * Endlessly produces values.
 * <p>
 * If the generator cannot continue, throws {@link GeneratorException}.
 * @param <T> value type
 */
public interface Generator<T> {
    
    /**
     * Produces next value.
     * @return next value
     * @throws GeneratorException if no more values can be produced
     */
    T next();
    
}
