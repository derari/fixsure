package org.cthul.fixsure.base;

import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Values;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.generators.value.EmptyGenerator;

/**
 * Base class for {@link Fetcher}s.
 */
public abstract class FetcherBase implements Fetcher {

    @Override
    public abstract int nextLength();

    @Override
    public Integer next() {
        return nextLength();
    }

    @Override
    public <T> FlValues<T> of(Generator<T> generator) {
        return newValues(nextLength(), generator).__asValues();
    }

    @Override
    public <T> FlValues<T> ofEach(final Generator<? extends T>... generators) {
        if (generators.length == 0) {
            return of(EmptyGenerator.<T>noValues());
        } else {
            CombinableValues<T> values = newValues(nextLength(), generators[0]);
            for (int i = 1; i < generators.length; i++) {
                values.__addMore(nextLength(), generators[i]);
            }
            return values.__asValues();
        }
    }
    
    protected abstract <T> CombinableValues<T> newValues(int n, Generator<? extends T> g);
    
    protected static interface CombinableValues<T> {
        void __addMore(int n, Generator<? extends T> g);
        FlValues<T> __asValues();
    }
    
}
