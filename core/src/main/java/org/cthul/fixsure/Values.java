package org.cthul.fixsure;

import java.util.List;
import org.cthul.fixsure.iterables.EagerValues;
import org.cthul.fixsure.iterables.LazyValues;

/**
 * A list of values that has bee produced by a genereator.
 * @see LazyValues
 * @see EagerValues
 */
public interface Values<T> extends List<T>, Sequence<T>, GeneratorTemplate<T> {
    
    /**
     * 
     * @param <A>
     * @param clazz
     * @return array
     * @see List#toArray(T[]);
     */
    <A> A[] toArray(Class<A> clazz);
    
    /**
     * Creates an array using the 
     * {@linkplain Typed#getValueType() value type} of the source.
     * @return array
     */
    @Override
    T[] toArray();
    
    /**
     * Alias for {@link List#toArray()}
     * @return array
     */
    Object[] toObjectArray();
    
}
