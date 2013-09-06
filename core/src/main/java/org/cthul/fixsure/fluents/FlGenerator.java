package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Converter;
import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.Values;

/**
 * Extends the {@link Generator} interface for fluent methods.
 * @param <T> value type
 */
public interface FlGenerator<T> extends Generator<T>, Typed<T> {
    
    // Should not exist to avoid ambiguity with Collection#get(int),
    // use #next(int) instead
    //Values<T> get(int length);
    
    FlValues<T> get(Generator<Integer> length);
    
    Values<T> get(Fetcher fetcher);
    
    FlValues<T> get(FlFetcher fetcher);
    
    FlValues<T> unbound();
    
    FlValues<T> next(int length);
    
    FlValues<T> next(Generator<Integer> length);
    
    FlValues<T> all();
    
    <T2> FlGenerator<T2> each(Converter<? super T, T2> converter);
    
    <T2> FlGenerator<T2> mergeWith(Generator<? extends T2>... generators);
    
    <T2> FlGenerator<T2> mixWith(Generator<? extends T2>... generators);
    
    <T2> FlGenerator<T2> alternateWith(Generator<? extends T2>... generators);
    
    /**
     * If this generator is finite, returns its output repeatedly.
     * (Optional operation)
     * @throws 
     *   UnsupportedOperationException if generator is not repeatable.
     * @return 
     */
    FlGenerator<T> repeat() throws UnsupportedOperationException;
    
    <T2> FlGenerator<T2> invoke(String m);
    
    <T2> FlGenerator<T2> invoke(String m, Object... args);
    
}
