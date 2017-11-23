package org.cthul.fixsure.fluents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Fixsure;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.SequenceLength;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.composite.RandomizedSequenceGenerator;
import org.cthul.fixsure.generators.composite.RoundRobinSequence;
import org.cthul.fixsure.generators.composite.ShuffledSequenceGenerator;
import org.cthul.fixsure.generators.value.ItemsSequence;

/**
 *
 */
public interface FlSequence<T> extends FlTemplate<T>, Sequence<T> {

    @Override
    default FlGenerator<T> newGenerator() {
        return Sequence.super.newGenerator();
    }
    
    @Override
    default FlSequence<T> fluentData() {
        return this;
    }

    @Override
    default Class<T> getValueType() {
        return null;
    }
    
    default long randomSeedHint() {
        return LAMBDA_SEED_HINT;
    }
    
    @Override
    default T first() {
        return value(0);
    }
    
    @Override
    default <R> FlSequence<R> map(Function<? super T, ? extends R> function) {
        class MappingSequence implements FlSequence<R> {
            @Override
            public Class<R> getValueType() {
                return null;
            }
            @Override
            public R value(long n) {
                T t = FlSequence.this.value(n);
                return function.apply(t);
            }
            @Override
            public long length() {
                return FlSequence.this.length();
            }
            @Override
            public boolean isUnbounded() {
                return FlSequence.this.isUnbounded();
            }
            @Override
            public boolean negativeIndices() {
                return FlSequence.this.negativeIndices();
            }
        }
        return new MappingSequence();
    }
    
    default <U, R> FlSequence<R> map(FlSequence<U> other, BiFunction<? super T, ? super U, ? extends R> function) {
        return with(other).map(function);
    }
    
    @Override
    default FlSequence<T> peek(Consumer<? super T> consumer) {
        return map(e -> { consumer.accept(e); return e; });
    }

    
    default FlSequence<T> then(Sequence<? extends T>... more) {
        if (isUnbounded()) return this;
        long[] lengths = new long[more.length];
        long length = length();
        boolean unbounded = false;
        for (int i = 0; i < more.length; i++) {
            length += (lengths[i] = more[i].length());
            if (more[i].isUnbounded() || length < 0) {
                lengths[i] = L_UNBOUNDED;
                unbounded = true;
                break;
            }
        }
        return Sequence.sequence(unbounded ? L_UNBOUNDED : length, index -> {
            if (index < FlSequence.this.length()) {
                return FlSequence.this.value(index);
            }
            long l = index - FlSequence.this.length();
            for (int i = 0; i < more.length; i++) {
                long len = lengths[i];
                if (l < len || len < 0) {
                    return more[i].value(l);
                } else {
                    l -= len;
                }
            }
            throw new IndexOutOfBoundsException(""+index);
        });
    }
    
    

    @Override
    default FlSequence<T> repeat() {
        if (isUnbounded()) {
            if (negativeIndices()) {
                return this;
            }
            return Sequence.sequence(getValueType(), L_NEGATIVE_INDICES, l -> {
                if (l < 0) l &= Long.MAX_VALUE;
                return value(l);
            });
        }
        return Sequence.sequence(getValueType(), L_NEGATIVE_INDICES, l -> {
            if (l < 0) l &= Long.MAX_VALUE;
            return value(l % FlSequence.this.length());
        });
    }
    
    @Override
    default FlTemplate<T> shuffle() {
        return () -> ShuffledSequenceGenerator.shuffle(this);
    }
    
    default FlTemplate<T> random() {
        return random(randomSeedHint());
    }
    
    default FlTemplate<T> random(long seed) {
        return random(Fixsure.uniformDistribution(), seed);
    }
    
    default FlTemplate<T> random(Distribution distribution) {
        return random(distribution, randomSeedHint());
    }
    
    default FlTemplate<T> random(Distribution distribution, long seed) {
        return () -> new RandomizedSequenceGenerator<>(this, distribution, seed);
    }
    
    default FlSequence<T> sorted() {
        if (isUnbounded()) {
            throw new UnsupportedOperationException("unbounded");
        }
        if (length() >= Integer.MAX_VALUE) {
            throw new UnsupportedOperationException("too large (" + length() + ")");
        }
        List<T> list = new ArrayList<>(all());
        Collections.sort((List) list);
        return ItemsSequence.sequence(list);
    }
    
    default FlSequence<T> alternateWith(Sequence<? extends T>... more) {
        Sequence<T>[] sequences = new Sequence[more.length+1];
        sequences[0] = this;
        System.arraycopy(more, 0, sequences, 1, more.length);
        return RoundRobinSequence.alternate(sequences);
    }
    
    @Override
    default <U, V> BiSequence<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return BiSequence.create(this, (n, bag) -> {
            action.accept(this.value(n), bag);
        });
    }
    
    default <U> BiSequence<T, U> with(Sequence<U> source) {
        SequenceLength min = SequenceLength.min(this, source);
        return BiSequence.create(min, (n, bag) -> {
            T t = this.value(n);
            U u = source.value(n);
            bag.accept(t, u);
        });
    }

    @Override
    default Stream<T> stream() {
        if (isUnbounded()) {
            return FlTemplate.super.stream();
        }
        final int characteristics = Spliterator.IMMUTABLE | Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;
        class SSpliterator implements Spliterator<T> {
            long n, end;
            public SSpliterator() {
                n = 0;
                end = length();
            }
            public SSpliterator(long n, long end) {
                this.n = n;
                this.end = end;
            }
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                action.accept(value(n++));
                return n < end;
            }
            @Override
            public Spliterator<T> trySplit() {
                long n2 = (end - n) / 2;
                if (n2 < 16) return null;
                long n0 = n;
                n += n2;
                return new SSpliterator(n0, n2);
            }
            @Override
            public long estimateSize() {
                return end - n;
            }
            @Override
            public int characteristics() {
                return characteristics;
            }
        }
        return StreamSupport.stream(new SSpliterator(), false);
    }
    
    static long LAMBDA_SEED_HINT = DistributionRandomizer.toSeed(FlSequence.class);
}
