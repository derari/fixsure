package org.cthul.fixsure.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.fluents.FlValues;

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
    public static <T> EagerValues<T> first(int n, DataSource<T> values) {
        return new EagerValues<>(values, n);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    public static <T> EagerValues<T> first(Generator<Integer> n, DataSource<T> values) {
        return new EagerValues<>(values, n.next());
    }
    
    /**
     * Fetches the next element from {@code values}.
     * @param <T>
     * @param values
     * @return values 
     */
    public static <T> EagerValues<T> firstOf(DataSource<T> values) {
        return new EagerValues<>(values, 1);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    public static <T> EagerValues<T> next(int n, DataSource<T> values) {
        return new EagerValues<>(values, n);
    }
    
    /**
     * Fetches the next {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values 
     */
    public static <T> EagerValues<T> next(Generator<Integer> n, DataSource<T> values) {
        return new EagerValues<>(values, n.next());
    }
    
    /**
     * Fetches elements from {@code values}, 
     * until it throws a {@link GeneratorException}.
     * @param <T>
     * @param values
     * @return values 
     */
    public static <T> EagerValues<T> all(DataSource<T> values) {
        return new EagerValues<>(values, -1);
    }
    
    private final List<T> values;
    
    public EagerValues(DataSource<? extends T> values, int length) {
        super((Class) GeneratorTools.typeOf(values));
        Generator<? extends T> gen = values.toGenerator();
        if (length < 0) {
            this.values = new ArrayList<>();
            try {
                while (true) {
                    this.values.add(gen.next());
                }
            } catch (GeneratorException e) {
                // expected
            }
        } else {
            this.values = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                this.values.add(gen.next());
            }
        }
    }
    
    protected EagerValues(Class<T> valueType, Collection<T> initial, DataSource<? extends T> values, int length) {
        super(valueType);
        this.values = new ArrayList<>(initial.size() + length);
        this.values.addAll(initial);
        Generator<? extends T> gen = values.toGenerator();
        for (int i = 0; i < length; i++) {
            this.values.add(gen.next());
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
    
    protected void _add(DataSource<? extends T> values, int length) {
        Generator<? extends T> gen = values.toGenerator();
        for (int i = 0; i < length; i++) {
            this.values.add(gen.next());
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
    public LazyValues<T> then(DataSource<? extends T> moreValues) {
        return new LazyValues<>(values, moreValues);
    }

    @Override
    public EagerValues<T> then(int n, DataSource<? extends T> moreValues) {
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
