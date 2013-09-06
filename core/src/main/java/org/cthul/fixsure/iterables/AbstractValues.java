package org.cthul.fixsure.iterables;

import java.lang.reflect.Array;
import java.util.RandomAccess;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Values;
import org.cthul.fixsure.base.AbstractFlList;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.generators.value.ItemsGenerator;
import org.cthul.fixsure.generators.value.ItemsGenerator.FromList;

/**
 * Base class for {@link Values} implementations.
 */
public abstract class AbstractValues<T> 
                extends AbstractFlList<T> 
                implements FlValues<T>, RandomAccess {

    private FlSequence<T> generator = null;
    private final Class<T> valueType;

    public AbstractValues(Class<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public FlValues<T> then(Generator<Integer> n, Generator<? extends T> moreValues) {
        return then(n.next(), moreValues);
    }

    @Override
    public LazyValues<T> thenAll(Generator<? extends T>... moreValues) {
        LazyValues<T> r = new LazyValues<>(this);
        for (Generator<? extends T> g: moreValues) {
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
    public FromList<T> newGenerator() {
        return ItemsGenerator.from(this);
    }
    
    @Override
    public Class<T> getValueType() {
        return valueType;
    }

    @Override
    protected FlSequence<T> generator() {
        if (generator == null) {
            generator = newGenerator();
        }
        return generator;
    }
    
}
