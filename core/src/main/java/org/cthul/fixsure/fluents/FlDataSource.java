package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.cthul.fixsure.*;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.generators.AnonymousTemplate;
import org.cthul.fixsure.generators.composite.RepeatingGenerator;
import org.cthul.fixsure.values.EagerValues;
import org.cthul.fixsure.values.LazyValues;

/**
 * Extends the {@link DataSource} interface with methods for data composition.
 * @param <T> value type
 */
public interface FlDataSource<T> extends DataSource<T>, Typed<T> {

    @Override
    Generator<T> toGenerator();

    @Override
    default Class<T> getValueType() {
        return null;
    }
    
    default FlValues<T> fetch(Cardinality cardinality) {
        return cardinality.toFetcher().of(this).fluentData();
    }

    default LazyValues<T> cached() {
        return Fetchers.cache().of(this);
    }
    
    default EagerValues<T> all() {
        return Fetchers.all().of(this);
    }
    
    default EagerValues<T> one() {
        return Fetchers.one().of(this);
    }
    
    default EagerValues<T> two() {
        return Fetchers.two().of(this);
    }
    
    default EagerValues<T> three() {
        return Fetchers.three().of(this);
    }
    
    EagerValues<T> few();
    
    EagerValues<T> some();
    
    EagerValues<T> several();
    
    EagerValues<T> many();
    
    // al methods will be implemented by FlGenerator or FlTemplate,
    // implementing them here would have no clear semantic
    
    FlDataSource<T> distinct();
    
    FlDataSource<T> filter(Predicate<? super T> predicate);
    
    <R> FlDataSource<R> flatMap(Function<? super T, ? extends DataSource<R>> function);
    
    <R> FlDataSource<R> map(Function<? super T, ? extends R> function);
    
    <U, R> FlDataSource<R> map(DataSource<U> other, BiFunction<? super T, ? super U, ? extends R> function);
    
    FlDataSource<T> peek(Consumer<? super T> consumer);
    
    FlDataSource<T> then(DataSource<? extends T>... more);
    
    FlDataSource<Values<T>> aggregate(int length);
    
    FlDataSource<Values<T>> aggregate(DataSource<Integer> length);
    
    default FlTemplate<T> repeat() {
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return RepeatingGenerator.repeat(FlDataSource.this);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return FlDataSource.this.toString(sb).append(".repeat()");
            }
        };
    }
    
    FlDataSource<T> shuffle();
    
    FlDataSource<T> shuffle(long seed);
    
    FlDataSource<T> mixWith(DataSource<? extends T>... more);
    
    FlDataSource<T> alternateWith(DataSource<? extends T>... more);
    
    FlTemplate<T> snapshot();
    
    <U> BiDataSource<T, U> split(Function<? super T, ? extends U> function);
    
    <U, V> BiDataSource<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action);
    
    <U> BiDataSource<T, U> with(DataSource<U> source);
    
    default Stream<T> stream() {
        return toGenerator().fluentData().stream();
    }
    
    default <R> R transformSource(Function<? super FlDataSource<? extends T>, ? extends R> function) {
        return function.apply(this);
    }
}
