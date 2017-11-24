package org.cthul.fixsure.fluents;

import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.composite.*;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;
import org.cthul.fixsure.Cardinality;

/**
 * Extends the {@link Generator} interface for fluent methods.
 * @param <T> value type
 */

@FunctionalInterface
public interface FlGenerator<T> extends FlDataSource<T>, Generator<T> {

    @Override
    @Deprecated
    default Generator<T> toGenerator() {
        return this;
    }
    
    @Override
    default FlGenerator<T> fluentData() {
        return this;
    }
    
    default long randomSeedHint() {
        return LAMBDA_SEED_HINT;
    }
    
    default LazyValues<T> any(int length) {
        return LazyValues.any(length, toGenerator());
    }
    
    default LazyValues<T> any(DataSource<Integer> length) {
        return LazyValues.any(length.toGenerator(), toGenerator());
    }
    
    default EagerValues<T> next(int length) {
        return EagerValues.next(length, toGenerator());
    }
    
    default EagerValues<T> next(DataSource<Integer> length) {
        return EagerValues.next(length.toGenerator(), toGenerator());
    }
    
    default FlValues<T> next(Cardinality fetcher) {
        return fetcher.toFetcher().of(this).fluentData();
    }
    
    @Override
    default FlValues<T> fetch(Cardinality fetcher) {
        return fetcher.toFetcher().of(this).fluentData();
    }

    @Override
    default LazyValues<T> cached() {
        return Fetchers.cache().of(this);
    }
    
    @Override
    default EagerValues<T> all() {
        return Fetchers.all().of(this);
    }
    
    @Override
    default EagerValues<T> one() {
        return Fetchers.one().of(this);
    }
    
    @Override
    default EagerValues<T> two() {
        return Fetchers.two().of(this);
    }
    
    @Override
    default EagerValues<T> three() {
        return Fetchers.three().of(this);
    }
    
    @Override
    default EagerValues<T> few() {
        return GeneratorTools.cacheConsumers(this).getFew().of(this);
    }
    
    @Override
    default EagerValues<T> some() {
        return GeneratorTools.cacheConsumers(this).getSome().of(this);
    }
    
    @Override
    default EagerValues<T> several() {
        return GeneratorTools.cacheConsumers(this).getSeveral().of(this);
    }
    
    @Override
    default EagerValues<T> many() {
        return GeneratorTools.cacheConsumers(this).getMany().of(this);
    }

    @Override
    default FlGenerator<T> distinct() {
        return DistinctGenerator.distinct(toGenerator());
    }
    
    @Override
    default FlGenerator<T> filter(Predicate<? super T> predicate) {
        return FilteringGenerator.filter(toGenerator(), predicate);
    }
    
    @Override
    default <R> FlGenerator<R> flatMap(Function<? super T, ? extends DataSource<R>> function) {
        return FlatMappingGenerator.map(toGenerator(), function);
    }
    
    @Override
    default <R> FlGenerator<R> map(Function<? super T, ? extends R> function) {
        return MappingGenerator.map(toGenerator(), function);
    }
    
    @Override
    default <U, R> FlGenerator<R> map(DataSource<U> other, BiFunction<? super T, ? super U, ? extends R> function) {
        return with(other).map(function);
    }
    
    @Override
    default FlGenerator<T> peek(Consumer<? super T> consumer) {
        return map(e -> { consumer.accept(e); return e; });
    }
    
    @Override
    default FlGenerator<T> then(DataSource<? extends T>... more) {
        return GeneratorQueue.beginWith(toGenerator()).thenAll(more);
    }

    @Override
    default FlGenerator<T> shuffle() {
        return shuffle(randomSeedHint());
    }

    @Override
    default FlGenerator<T> shuffle(long seed) {
        return ShufflingGenerator.shuffle(toGenerator(), seed);
    }
    
    @Override
    default FlGenerator<T> mixWith(DataSource<? extends T>... more) {
        Generator<T>[] generators = DataSource.toGenerators(toGenerator(), (DataSource[]) more);
        return MixingGenerator.mix(generators);
    }
    
    @Override
    default FlGenerator<T> alternateWith(DataSource<? extends T>... more) {
        Generator<T>[] generators = DataSource.toGenerators(toGenerator(), (DataSource[]) more);
        return RoundRobinGenerator.alternate(generators);
    }
    
    @Override
    default FlTemplate<T> snapshot() {
        throw new UnsupportedOperationException("not copayble");
    }

    @Override
    default <U> BiGenerator<T, U> split(Function<? super T, ? extends U> function) {
        return new BiGenerator.Anonymous<T, U>() {
            @Override
            public void next(BiConsumer<? super T, ? super U> bag) {
                T t = FlGenerator.this.next();
                bag.accept(t, function.apply(t));
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlGenerator.this.toString(sb).append(".split(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
    }

    @Override
    default <U, V> BiGenerator<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return new BiGenerator.Anonymous<U, V>() {
            @Override
            public void next(BiConsumer<? super U, ? super V> bag) {
                action.accept(FlGenerator.this.next(), bag);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlGenerator.this.toString(sb).append(".split(");
                return GeneratorTools.lambdaToString(action, sb).append(')');
            }
        };
    }
    
    @Override
    default <U> BiGenerator<T, U> with(DataSource<U> source) {
        Generator<U> gen2 = source.toGenerator();
        return new BiGenerator.Anonymous<T, U>() {
            @Override
            public void next(BiConsumer<? super T, ? super U> bag) {
                bag.accept(FlGenerator.this.next(), gen2.next());
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlGenerator.this.toString(sb.append('('));
                return gen2.toString(sb.append(';')).append(')');
            }
        };
    }
    
    @Override
    default Stream<T> stream() {
        class GSpliterator extends AbstractSpliterator<T> {
            public GSpliterator() {
                super(Long.MAX_VALUE, Spliterator.IMMUTABLE);
            }
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                try {
                    action.accept(next());
                    return true;
                } catch (GeneratorException e) {
                    return false;
                }
            }
        }
        return StreamSupport.stream(new GSpliterator(), false);
    }
    
    static long LAMBDA_SEED_HINT = DistributionRandomizer.toSeed(FlGenerator.class);
}
