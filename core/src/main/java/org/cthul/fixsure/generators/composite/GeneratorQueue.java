package org.cthul.fixsure.generators.composite;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.value.EmptySequence;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 * A queue of generators that fetches a certain number of elements from
 * each and then uses the next.
 */
public class GeneratorQueue<T> implements CopyableGenerator<T> {
    
    public static <T> GeneratorQueue<T> beginWith(DataSource<T> values) {
        return new GeneratorQueue<>(values);
    }
    
    public static <T> GeneratorQueue<T> beginWith(int n, DataSource<T> values) {
        return new GeneratorQueue<>(values, n);
    }
    
    public static <T> GeneratorQueue<T> beginWith(Generator<Integer> n, DataSource<T> values) {
        return new GeneratorQueue<>(values, n.next());
    }
    
    public static <T> GeneratorQueue<T> queue(DataSource<T>... values) {
        return new GeneratorQueue<>(values);
    }
    
    private Fetch<T> values;
    private Queue<Fetch<T>> moreValues = null;
    
    public GeneratorQueue(DataSource<T> values) {
        this(values, -1, null);
    }
    
    public GeneratorQueue(DataSource<T> values, int n) {
        this(values, n, null);
    }
    
    protected GeneratorQueue(DataSource<T> values, int n, Collection<Fetch<T>> moreValues) {
        if (n < 0) {
            this.values = new FetchAll<>(values.toGenerator());
        } else {
            this.values = new FetchFixed<>(values.toGenerator(), n);
        }
        if (moreValues != null) {
            this.moreValues = new ArrayDeque<>(moreValues.size());
            copyAll(moreValues, this.moreValues);
        }
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GeneratorQueue(final DataSource<T>... values) {
        if (values.length == 0) {
            this.values = new FetchAll<>(EmptySequence.<T>noValues());
        } else {
            this.values = new FetchAll<>(values[0].toGenerator());
            for (int i = 1; i < values.length; i++)
                _add(values[i].toGenerator());
        }
    }
    
    protected GeneratorQueue(GeneratorQueue<T> src) {
        this.values = src.values.copy();
        if (src.moreValues != null) {
            this.moreValues = new ArrayDeque<>(src.moreValues.size());
            copyAll(src.moreValues, this.moreValues);
        }
    }

    private void copyAll(Collection<? extends Fetch<T>> src, Collection<Fetch<T>> target) {
        for (Fetch<T> f: src) {
            target.add(f.copy());
        }
    }
    
    protected void _add(DataSource<? extends T> value) {
        if (moreValues == null) moreValues = new ArrayDeque<>();
        moreValues.add(new FetchAll<>(value));
    }
    
    protected void _add(Generator<? extends T> value, int n) {
        if (n < 0) {
            _add(value);
        } else {
            if (moreValues == null) moreValues = new ArrayDeque<>();
            moreValues.add(new FetchFixed<>(value, n));
        }
    }
    
    @Override
    public T next() {
        while (true) {
            try {
                if (values.hasNext()) {
                    return values.fetch();
                }
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

    public GeneratorQueue<T> thenAll(DataSource<? extends T>... moreValues) {
        GeneratorQueue<T> r = new GeneratorQueue<>(this);
        for (DataSource<? extends T> g: moreValues) {
            r._add(g);
        }
        return r;
    }

    @Override
    public GeneratorQueue<T> copy() {
        return new GeneratorQueue<>(this);
    }

    @Override
    public long randomSeedHint() {
        return values.randomSeedHint();
    }

    protected static abstract class Fetch<T> {
        public abstract boolean hasNext();
        public abstract T fetch();
        public abstract boolean expectException();
        public abstract Fetch<T> copy();
        public abstract long randomSeedHint();
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
            return new FetchFixed<>(newFromTemplate(), rem);
        }
        public Generator<? extends T> newFromTemplate() {
            return copyGenerator(values);
        }
        @Override
        public long randomSeedHint() {
            return GeneratorTools.getRandomSeedHint(values) ^ rem;
        }
    }
    
    private static class FetchAll<T> extends Fetch<T> {
        private final Generator<? extends T> values;
        public FetchAll(DataSource<? extends T> values) {
            this.values = values.toGenerator();
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
            return new FetchAll<>(newFromTemplate());
        }
        public Generator<? extends T> newFromTemplate() {
            return copyGenerator(values);
        }
        @Override
        public long randomSeedHint() {
            return GeneratorTools.getRandomSeedHint(values) ^ 0xa77a77a77L;
        }
    }
}
