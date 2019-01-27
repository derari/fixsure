package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.fluents.FlCardinality;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.generators.value.EmptySequence;

/**
 * Base class for {@link Fetcher}s.
 */
public abstract class AbstractFetcher implements FlCardinality.FlFetcher {

    @Override
    public abstract int nextLength();

    @Override
    public <T> FlValues<T> of(DataSource<T> generator) {
        return newValues(nextLength(), generator).__asValues();
    }

    @Override
    public <T> FlValues<T> ofEach(DataSource<? extends T>... generators) {
        switch (generators.length) {
            case 0:
                return of(EmptySequence.<T>noValues());
            case 1:
                return of((DataSource) generators[0]);
            default:
                CombinableValues<T> values = newValues(nextLength(), generators[0]);
                for (int i = 1; i < generators.length; i++) {
                    values.__addMore(nextLength(), generators[i]);
                }
                return values.__asValues();
        }
    }
    
    protected abstract <T> CombinableValues<T> newValues(int n, DataSource<? extends T> g);
    
    protected static interface CombinableValues<T> {
        void __addMore(int n, DataSource<? extends T> g);
        FlValues<T> __asValues();
    }
    
}
