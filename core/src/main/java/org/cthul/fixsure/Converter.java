package org.cthul.fixsure;

/**
 * Converts values from one type to another.
 */
public interface Converter<In, Out> {
    
    Out convert(In value);
    
}
