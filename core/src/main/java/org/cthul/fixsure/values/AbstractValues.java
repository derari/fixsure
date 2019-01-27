package org.cthul.fixsure.values;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.RandomAccess;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Values;
import org.cthul.fixsure.fluents.FlValues;

/**
 * Base class for {@link Values} implementations.
 */
public abstract class AbstractValues<T> 
                extends AbstractList<T> 
                implements FlValues<T>, RandomAccess {

    private final Class<T> valueType;

    public AbstractValues(Class<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public FlValues<T> then(Generator<Integer> n, DataSource<? extends T> moreValues) {
        return then(n.next(), moreValues);
    }

    @Override
    public LazyValues<T> thenAll(DataSource<? extends T>... moreValues) {
        LazyValues<T> r = new LazyValues<>(this);
        for (DataSource<? extends T> g: moreValues) {
            r._add(g);
        }
        return r;
    }

    @Override
    public <A> A[] toArray(Class<A> clazz) {
        final A[] result = (A[]) Array.newInstance(clazz, size());
        return toArray(result);
    }

    @Override
    public T[] toArray() {
        if (valueType == null) {
            return (T[]) toObjectArray();
        }
        return toArray(valueType);
    }
    
    @Override
    public Object[] toObjectArray() {
        return super.toArray();
    }

    @Override
    public T value(long n) {
        return get((int) n);
    }

    @Override
    public long length() {
        return size();
    }
    
    @Override
    public Class<T> getValueType() {
        return valueType;
    }
}
