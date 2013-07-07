package org.cthul.fixsure.generators.composite;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.cthul.fixsure.generators.value.EmptyGenerator;
import org.hamcrest.Factory;

/**
 *
 */
public class GeneratorQueue<T> 
                extends GeneratorBase<T>
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> GeneratorQueue<T> beginWith(Generator<T> values) {
        return new GeneratorQueue<>(values);
    }
    
    @Factory
    public static <T> GeneratorQueue<T> beginWith(int n, Generator<T> values) {
        return new GeneratorQueue<>(values, n);
    }
    
    @Factory
    public static <T> GeneratorQueue<T> beginWith(Generator<Integer> n, Generator<T> values) {
        return new GeneratorQueue<>(values, n.next());
    }
    
    @Factory
    public static <T> GeneratorQueue<T> queue(Generator<T>... values) {
        return new GeneratorQueue<>(values);
    }
    
    private Fetch<T> values;
    private Queue<Fetch<T>> moreValues = null;
    
    public GeneratorQueue(Generator<T> values) {
        this(values, -1, null);
    }
    
    public GeneratorQueue(Generator<T> values, int n) {
        this(values, n, null);
    }
    
    protected GeneratorQueue(Generator<T> values, int n, Collection<Fetch<T>> moreValues) {
        if (n < 0) {
            this.values = new FetchAll<>(values);
        } else {
            this.values = new FetchFixed<>(values, n);
        }
        if (moreValues != null) {
            this.moreValues = new LinkedList<>();
            copyAll(moreValues, this.moreValues);
        }
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GeneratorQueue(final Generator<T>... values) {
        if (values.length == 0) {
            this.values = new FetchAll<>(EmptyGenerator.<T>noValues());
        } else {
            this.values = new FetchAll<>(values[0]);
            for (int i = 1; i < values.length; i++)
                _add(values[i]);
        }
    }
    
    protected GeneratorQueue(GeneratorQueue<T> src) {
        this.values = src.values.copy();
        if (src.moreValues != null) {
            this.moreValues = new LinkedList<>();
            copyAll(src.moreValues, this.moreValues);
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
    }
    
    protected void _add(Generator<? extends T> value, int n) {
        if (n < 0) {
            _add(value);
        } else {
            if (moreValues == null) moreValues = new LinkedList<>();
            moreValues.add(new FetchFixed<>(value, n));
        }
    }
    
    @Override
    public T next() {
        while (true) {
            try {
                if (values.hasNext())
                    return values.fetch();
            } catch (GeneratorException e) {
                if (!values.expectException()) {
                    throw e;
                }
                if (moreValues == null || moreValues.isEmpty()) {
                    throw e;
                }
            }
            if (moreValues == null || moreValues.isEmpty()) {
                throw new GeneratorException("End of queue");
            }
            values = moreValues.remove();
        }
    }

    public GeneratorQueue<T> then(Generator<? extends T> moreValues) {
        GeneratorQueue<T> r = new GeneratorQueue<>(this);
        r._add(moreValues);
        return r;
    }

    public GeneratorQueue<T> then(int n, Generator<? extends T> moreValues) {
        GeneratorQueue<T> r = new GeneratorQueue<>(this);
        r._add(moreValues, n);
        return r;
    }

    public GeneratorQueue<T> then(Generator<Integer> n, Generator<? extends T> moreValues) {
        return then(n.next(), moreValues);
    }

    public GeneratorQueue<T> thenAll(Generator<? extends T>... moreValues) {
        GeneratorQueue<T> r = new GeneratorQueue<>(this);
        for (Generator<? extends T> g: moreValues) {
            r._add(g);
        }
        return r;
    }

    @Override
    public GeneratorQueue<T> newGenerator() {
        GeneratorQueue<T> r = new GeneratorQueue(values.newFromTemplate(), values.getLength());
        if (moreValues != null) {
            for (Fetch<T> f: moreValues) {
                r._add(f.newFromTemplate(), f.getLength());
            }
        }
        return r;
    }

    protected static abstract class Fetch<T> {
        public abstract boolean hasNext();
        public abstract T fetch();
        public abstract boolean expectException();
        public abstract Fetch<T> copy();
        public abstract int getLength();
        public abstract Generator<? extends T> newFromTemplate();
    }
    
    private static class FetchFixed<T> extends Fetch<T> {
        private final Generator<? extends T> values;
        private int rem;
        public FetchFixed(Generator<? extends T> values, int size) {
            this.values = values;
            this.rem = size;
        }
        @Override
        public boolean hasNext() {
            return rem > 0;
        }
        @Override
        public T fetch() {
            if (rem == 0) throw new GeneratorException();
            rem--;
            return values.next();
        }
        @Override
        public boolean expectException() {
            return rem == 0;
        }
        @Override
        public Fetch<T> copy() {
            return new FetchFixed<>(values, rem);
        }
        @Override
        public int getLength() {
            return rem;
        }
        @Override
        public Generator<? extends T> newFromTemplate() {
            return GeneratorTools.newGeneratorFromTemplate(values);
        }
    }
    
    private static class FetchAll<T> extends Fetch<T> {
        private final Generator<? extends T> values;
        public FetchAll(Generator<? extends T> values) {
            this.values = values;
        }
        @Override
        public boolean hasNext() {
            return true;
        }
        @Override
        public T fetch() {
            return values.next();
        }
        @Override
        public boolean expectException() {
            return true;
        }
        @Override
        public Fetch<T> copy() {
            return new FetchAll<>(values);
        }
        @Override
        public int getLength() {
            return -1;
        }
        @Override
        public Generator<? extends T> newFromTemplate() {
            return GeneratorTools.newGeneratorFromTemplate(values);
        }
    }
    
}
