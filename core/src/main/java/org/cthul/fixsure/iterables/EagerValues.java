package org.cthul.fixsure.iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlValues;
import org.hamcrest.Factory;

/**
 * Values immediately fetched from a generator.
 */
public class EagerValues<T> extends AbstractValues<T> {
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> first(int n, Generator<T> values) {
        return new EagerValues<>(values, n);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> first(Generator<Integer> n, Generator<T> values) {
        return new EagerValues<>(values, n.next());
    }
    
    /**
     * Fetches the next element from {@code values}.
     * @param <T>
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> firstOf(Generator<T> values) {
        return new EagerValues<>(values, 1);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> next(int n, Generator<T> values) {
        return new EagerValues<>(values, n);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> next(Generator<Integer> n, Generator<T> values) {
        return new EagerValues<>(values, n.next());
    }
    
    /**
     * Fetches elements from {@code values}, 
     * until it throws a {@link GeneratorException}.
     * @param <T>
     * @param values
     * @return values 
     */
    @Factory
    public static <T> EagerValues<T> all(Generator<T> values) {
        return new EagerValues<>(values, -1);
    }
    
    private final List<T> values;
    
    public EagerValues(Generator<? extends T> values, int length) {
        super((Class) GeneratorTools.typeOf(values));
        if (length < 0) {
            this.values = new ArrayList<>();
            try {
                while (true) {
                    this.values.add(values.next());
                }
            } catch (GeneratorException e) {
                // expected
            }
        } else {
            this.values = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                this.values.add(values.next());
            }
        }
    }
    
    protected EagerValues(Class<T> valueType, Collection<T> initial, Generator<? extends T> values, int length) {
        super(valueType);
        this.values = new ArrayList<>(initial.size() + length);
        this.values.addAll(initial);
        for (int i = 0; i < length; i++) {
            this.values.add(values.next());
        }
    }

    protected EagerValues(Class<T> valueType, Collection<T> values, Collection<? extends T> moreValues) {
        super(valueType);
        this.values = new ArrayList<>(values.size() + moreValues.size());
        this.values.addAll(values);
        this.values.addAll(moreValues);
    }
    
    protected void _add(T value) {
        values.add(value);
    }
    
    protected void _add(Generator<? extends T> values, int length) {
        for (int i = 0; i < length; i++) {
            this.values.add(values.next());
        }
    }
    
    protected void _addAll(Collection<T> values) {
        values.addAll(values);
    }
    
    @Override
    public T get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public LazyValues<T> then(Generator<? extends T> moreValues) {
        return new LazyValues<>(values, moreValues);
    }

    @Override
    public EagerValues<T> then(int n, Generator<? extends T> moreValues) {
        return new EagerValues<>(getValueType(), values, moreValues, n);
    }

    @Override
    public FlValues<T> then(Collection<? extends T> moreValues) {
        if (moreValues.size() < 256) {
            return new EagerValues<>(getValueType(), values, moreValues);
        } else {
            return new LazyValues<>(getValueType(), values, moreValues);
        }
    }

}
