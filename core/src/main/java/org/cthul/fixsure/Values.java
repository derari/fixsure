package org.cthul.fixsure;

import java.util.List;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;

/**
 * A list of values that has been produced by a generator.
 * @see LazyValues
 * @see EagerValues
 */
public interface Values<T> extends List<T>, Sequence<T> {
    
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
    
    @Override
    FlValues<T> fluentData();
}
