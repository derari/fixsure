package org.cthul.fixsure.fluents;

import java.util.Collection;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Values;

/**
 *
 */
public interface FlValues<T> extends Values<T>, FlSequence<T>, FlGeneratorTemplate<T> {
    
    FlValues<T> then(Generator<? extends T> moreValues);
    
    FlValues<T> then(int n, Generator<? extends T> moreValues);
    
    FlValues<T> then(Generator<Integer> n, Generator<? extends T> moreValues);
    
    FlValues<T> then(Collection<? extends T> moreValues);
    
    FlValues<T> thenAll(Generator<? extends T>... moreValues);
}
