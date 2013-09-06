package org.cthul.fixsure;

/**
 * Converts values from one type to another.
 * @param <In>
 * @param <Out> 
 */
public interface Converter<In, Out> {
    
    /**
     * Converts a value from {@code In} to {@code Out}.
     * @param value
     * @return converted value
     */
    Out convert(In value);
    
}
