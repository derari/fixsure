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
import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.composite.*;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;

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
    
    default LazyValues<T> any(int length) {
        return LazyValues.any(length, toGenerator());
    }
    
    default LazyValues<T> any(Generator<Integer> length) {
        return LazyValues.any(length, toGenerator());
    }
    
    default EagerValues<T> next(int length) {
        return EagerValues.next(length, toGenerator());
    }
    
    default EagerValues<T> next(Generator<Integer> length) {
        return EagerValues.next(length, toGenerator());
    }
    
    default FlValues<T> next(Fetcher fetcher) {
        return fetcher.toItemConsumer().of(this).fluentData();
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
        return ShufflingGenerator.shuffle(toGenerator());
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
    default <U, V> BiGenerator<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return bag -> action.accept(next(), bag);
    }
    
    @Override
    default <U> BiGenerator<T, U> with(DataSource<U> source) {
        Generator<U> gen2 = source.toGenerator();
        return bag -> bag.accept(next(), gen2.next());
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
}
