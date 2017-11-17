package org.cthul.fixsure.fluents;

import java.util.Collection;
import java.util.stream.Stream;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Values;

/**
 *
 */
public interface FlValues<T> extends Values<T>, FlSequence<T> {
    
    FlValues<T> then(DataSource<? extends T> moreValues);
    
    FlValues<T> then(int n, DataSource<? extends T> moreValues);
    
    FlValues<T> then(Generator<Integer> n, DataSource<? extends T> moreValues);
    
    FlValues<T> then(Collection<? extends T> moreValues);
    
    FlValues<T> thenAll(DataSource<? extends T>... moreValues);
    
    @Override
    default FlValues<T> fluentData() {
        return this;
    }

    @Override
    default Stream<T> stream() {
        return FlSequence.super.stream();
    }
}
