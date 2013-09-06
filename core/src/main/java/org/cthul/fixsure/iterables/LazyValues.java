package org.cthul.fixsure.iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorTools;
import org.hamcrest.Factory;

/**
 * Values fetched from generators on demand.
 */
public class LazyValues<T> extends AbstractValues<T> {
    
    /**
     * Fetches up to {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values
     */
    @Factory
    public static <T> LazyValues<T> get(int n, Generator<? extends T> values) {
        return new LazyValues<>(values, n);
    }
    
    /**
     * Fetches up to {@code n} elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values
     */
    @Factory
    public static <T> LazyValues<T> get(Generator<Integer> n, Generator<? extends T> values) {
        return new LazyValues<>(values, n.next());
    }
    
    /**
     * Fetches elements from {@code values}.
     * @param <T>
     * @param n
     * @param values
     * @return values
     */
    @Factory
    public static <T> LazyValues<T> unbound(Generator<? extends T> values) {
        return new LazyValues<>(values);
    }
    
    private Fetch<T> values;
    private final List<T> cache;
    private int totalSize;
    private Queue<Fetch<T>> moreValues = null;
    
    public LazyValues(Generator<? extends T> values, int n) {
        this(null, values, n);
    }
    
    public LazyValues(Collection<? extends T> initial, Generator<? extends T> values, int n) {
        this(initial, values, null, n);
    }
    
    public LazyValues(Generator<? extends T> values) {
        this(null, values, -1);
    }
    
    public LazyValues(Collection<? extends T> initial, Generator<? extends T> values) {
        this(initial, values, null, -1);
    }
    
    protected LazyValues(Class<T> valueType, Collection<? extends T> values, Collection<? extends T> moreValues) {
        super(valueType);
        this.values = new FetchCollection<>(moreValues);
        long total = values.size() + (long) moreValues.size();
        if (total >= Integer.MAX_VALUE) total = -1;
        this.totalSize = (int) total;
        this.cache = new ArrayList<>(values);
    }
    
    protected LazyValues(Collection<? extends T> initial, Generator<? extends T> values, Collection<? extends Fetch<T>> moreValues, int n) {
        super((Class) GeneratorTools.typeOf(values));
        if (n < 0) {
            this.values = new FetchAll<>(values);
        } else {
            this.values = new FetchFixed<>(values, n);
        }
        this.totalSize = n;
        if (initial != null) {
            this.cache = new ArrayList<>(initial);
        } else {
            this.cache = new ArrayList<>();
        }
        if (moreValues != null) {
            this.moreValues = new LinkedList<>();
            copyAll(moreValues, this.moreValues);
        }
    }
    
    protected LazyValues(LazyValues<T> src) {
        super(src.getValueType());
        this.values = src.values.copy();
        this.totalSize = src.totalSize;
        this.cache = new ArrayList<>(src.cache);
        if (src.moreValues != null) {
            this.moreValues = new LinkedList<>();
            copyAll(moreValues, this.moreValues);
        }
    }
    
    private void copyAll(Collection<? extends Fetch<T>> src, Collection<Fetch<T>> target) {
        for (Fetch<T> f: src) {
            target.add(f.copy());
        }
    }
    
    protected void _add(Generator<? extends T> value) {
        if (moreValues == null) moreValues = new LinkedList<>();
        moreValues.add(new FetchAll<>(value));
        totalSize = -1;
    }
    
    protected void _add(Generator<? extends T> value, int n) {
        if (moreValues == null) moreValues = new LinkedList<>();
        moreValues.add(new FetchFixed<>(value, n));
        if (totalSize >= 0) totalSize += n;
    }
    
    protected void _add(Collection<? extends T> value) {
        if (moreValues == null) moreValues = new LinkedList<>();
        moreValues.add(new FetchCollection<>(value));
        totalSize = -1;
    }
    
    @Override
    public T get(int index) {
        if (cache.size() <= index) {
            fetchValues(index - cache.size() + 1);
        }
        return cache.get(index);
    }

    @Override
    public int size() {
        if (totalSize < 0) {
            return Integer.MAX_VALUE;
        }
        return totalSize;
    }

    private void fetchValues(int i) {
        if (totalSize >= 0 && cache.size() + i > totalSize) {
            i = totalSize - cache.size();
        }
        while (i > 0) {
            try {
                i -= values.fetch(cache, i);
            } catch (GeneratorException e) {
                if (!values.expectException()) {
                    throw e;
                }
                if (moreValues == null || moreValues.isEmpty()) {
                    throw e;
                }
                values = moreValues.remove();
                i++;
            }
        }
    }

    @Override
    public LazyValues<T> then(Generator<? extends T> moreValues) {
        LazyValues<T> r = new LazyValues<>(this);
        r._add(moreValues);
        return r;
    }

    @Override
    public LazyValues<T> then(int n, Generator<? extends T> moreValues) {
        LazyValues<T> r = new LazyValues<>(this);
        r._add(moreValues, n);
        return r;
    }

    @Override
    public LazyValues<T> then(Collection<? extends T> moreValues) {
        LazyValues<T> r = new LazyValues<>(this);
        r._add(moreValues);
        return r;
    }
    
    protected static abstract class Fetch<T> {
        public abstract int fetch(List<T> target, int n);
        public abstract boolean expectException();
        public abstract Fetch<T> copy();
    }
    
    private static class FetchFixed<T> extends Fetch<T> {
        private final Generator<? extends T> values;
        private int rem;
        public FetchFixed(Generator<? extends T> values, int size) {
            this.values = values;
            this.rem = size;
        }
        @Override
        public int fetch(List<T> target, int n) {
            if (n > rem) n = rem;
            for (int i = 0; i < n; i++) {
                target.add(values.next());
            }
            rem -= n;
            return n;
        }
        @Override
        public boolean expectException() {
            return false;
        }
        @Override
        public Fetch<T> copy() {
            return new FetchFixed<>(values, rem);
        }
    }
    
    private static class FetchAll<T> extends Fetch<T> {
        private final Generator<? extends T> values;
        public FetchAll(Generator<? extends T> values) {
            this.values = values;
        }
        @Override
        public int fetch(List<T> target, int n) {
            for (int i = 0; i < n; i++) {
                target.add(values.next());
            }
            return n;
        }
        @Override
        public boolean expectException() {
            return true;
        }
        @Override
        public Fetch<T> copy() {
            return new FetchAll<>(values);
        }
    }
    
    private static class FetchCollection<T> extends Fetch<T> {
        private final Iterator<? extends T> values;
        public FetchCollection(Iterator<? extends T> values) {
            this.values = values;
        }
        public FetchCollection(Collection<? extends T> values) {
            this.values = values.iterator();
        }
        @Override
        public int fetch(List<T> target, int n) {
            for (int i = 0; i < n; i++) {
                target.add(values.next());
            }
            return n;
        }
        @Override
        public boolean expectException() {
            return true;
        }
        @Override
        public Fetch<T> copy() {
            return new FetchCollection<>(values);
        }
    }
    
}
