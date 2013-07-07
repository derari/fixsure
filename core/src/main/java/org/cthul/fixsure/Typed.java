package org.cthul.fixsure;

/**
 * Optional interface for {@link Converter}, {@link Generator}, 
 * and {@link Values}, to provide information about their values.
 */
public interface Typed<T> {

    /**
     * Returns the type of values produced/contained by this instance.
     * Can return {@code null} if the type is unknown; 
     * should never return a primitive type.
     * @return value type
     */
    Class<T> getValueType();
    
}
