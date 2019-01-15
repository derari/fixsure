package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.fixsure.*;
import org.cthul.fixsure.values.LazyValues;

/**
 *
 */
@FunctionalInterface
public interface FlTemplate<T> extends FlDataSource<T>, Template<T> {

    @Override
    FlGenerator<T> newGenerator();
    
    @Override
    @Deprecated
    default Generator<T> toGenerator() {
        return newGenerator();
    }
    
    @Override
    default Class<T> getValueType() {
        return Typed.typeOf(newGenerator());
    }
    
    default T first() {
        return newGenerator().next();
    }
    
    default LazyValues<T> first(int length) {
        return LazyValues.any(length, this);
    }
    
    default LazyValues<T> first(Generator<Integer> length) {
        return LazyValues.any(length, this);
    }

    @Override
    default FlTemplate<T> distinct() {
        return () -> newGenerator().distinct();
    }
    
    @Override
    default FlTemplate<T> filter(Predicate<? super T> predicate) {
        return () -> newGenerator().filter(predicate);
    }
    
    @Override
    default <R> FlTemplate<R> flatMap(Function<? super T, ? extends DataSource<R>> function) {
        return () -> newGenerator().flatMap(function);
    }
    
    @Override
    default <R> FlTemplate<R> map(Function<? super T, ? extends R> function) {
        return () -> newGenerator().map(function);
    }
    
    @Override
    default <U, R> FlTemplate<R> map(DataSource<U> other, BiFunction<? super T, ? super U, ? extends R> function) {
        return with(other).map(function);
    }
    
    @Override
    default FlTemplate<T> peek(Consumer<? super T> consumer) {
        return () -> newGenerator().peek(consumer);
    }
    
    @Override
    default FlTemplate<T> then(DataSource<? extends T>... more) {
        return () -> newGenerator().then(more);
    }

    @Override
    default FlTemplate<T> shuffle() {
        return () -> newGenerator().shuffle();
    }

    @Override
    default FlTemplate<T> mixWith(DataSource<? extends T>... more) {
        return () -> newGenerator().mixWith(more);
    }

    @Override
    default FlTemplate<T> alternateWith(DataSource<? extends T>... more) {
        return () -> newGenerator().alternateWith(more);
    }

    @Override
    default FlTemplate<T> snapshot() {
        return this;
    }

    @Override
    default <U, V> BiTemplate<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return () -> newGenerator().split(action);
    }

    @Override
    default <U> BiTemplate<T, U> with(DataSource<U> source) {
        return () -> newGenerator().with(source);
    }
}
