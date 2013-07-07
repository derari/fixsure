package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.base.SequenceBase;
import org.hamcrest.Factory;

/**
 * Wraps a {@link Sequence} to implement the {@linkplain FlSequence fluent interface}
 * @param <T> valuet type
 */
public class FluentSequenceWrapper<T> extends SequenceBase<T> {
    
    @Factory
    public static <T> FlSequence<T> fluent(Sequence<T> generator) {
        if (generator instanceof FlSequence) {
            return (FlSequence<T>) generator;
        }
        return new FluentSequenceWrapper<>(generator);
    }

    private final Sequence<T> s;

    public FluentSequenceWrapper(Sequence<T> s) {
        this.s = s;
    }

    @Override
    public T next() {
        return s.next();
    }    

    @Override
    public long length() {
        return s.length();
    }

    @Override
    public T value(long n) {
        return s.value(n);
    }
    
    @Override
    protected Sequence<T> publishThis() {
        return s;
    }

}
