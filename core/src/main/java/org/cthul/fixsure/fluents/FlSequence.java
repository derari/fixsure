package org.cthul.fixsure.fluents;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.cthul.fixsure.generators.AnonymousTemplate;
import org.cthul.fixsure.generators.AnonymousSequence;
import org.cthul.fixsure.generators.GeneratorTools;
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
        return new AnonymousSequence<R>(this) {
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
            public StringBuilder toString(StringBuilder sb) {
                FlSequence.this.toString(sb).append(".map(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
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
        return new AnonymousSequence<T>(unbounded ? L_UNBOUNDED : length) {
            @Override
            public T value(long index) {
                assertInRange(index);
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
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return GeneratorTools.printList(FlSequence.this, Arrays.asList(more), sb.append('{')).append('}');
            }
        };
    }
    
    @Override
    default FlSequence<T> repeat() {
        if (isUnbounded()) {
            if (negativeIndices()) {
                return this;
            }
            return new AnonymousSequence<T>(FlSequence.this, L_NEGATIVE_INDICES) {
                @Override
                public T value(long n) {
                    n &= Long.MAX_VALUE;
                    return FlSequence.this.value(n);
                }
                @Override
                public StringBuilder toString(StringBuilder sb) {
                    return super.toString(sb).append(".repeat()");
                }
            };
        }
        return Sequence.sequence(getValueType(), L_NEGATIVE_INDICES, l -> {
            l &= Long.MAX_VALUE;
            return value(l % FlSequence.this.length());
        });
    }
    
    @Override
    default FlTemplate<T> shuffle() {
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return ShuffledSequenceGenerator.shuffle(FlSequence.this);
            }
        };
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
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return new RandomizedSequenceGenerator<>(FlSequence.this, distribution, seed);
            }
        };
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
    default <U> BiSequence<T, U> split(Function<? super T, ? extends U> function) {
        return new BiSequence.Anonymous<T, U>(this) {
            @Override
            public void value(long index, BiConsumer<? super T, ? super U> bag) {
                T t = FlSequence.this.value(index);
                bag.accept(t, function.apply(t));
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlSequence.this.toString(sb).append(".split(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
    }
    
    @Override
    default <U, V> BiSequence<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return new BiSequence.Anonymous<U, V>(this) {
            @Override
            public void value(long index, BiConsumer<? super U, ? super V> bag) {
                action.accept(FlSequence.this.value(index), bag);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlSequence.this.toString(sb).append(".split(");
                return GeneratorTools.lambdaToString(action, sb).append(')');
            }
        };
    }
    
    default <U> BiSequence<T, U> with(Sequence<U> source) {
        SequenceLength min = SequenceLength.min(this, source);
        return new BiSequence.Anonymous<T, U>(min) {
            @Override
            public void value(long index, BiConsumer<? super T, ? super U> bag) {
                T t = FlSequence.this.value(index);
                U u = source.value(index);
                bag.accept(t, u);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlSequence.this.toString(sb.append('('));
                return source.toString(sb.append(';')).append(')');
            }
        };
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
