package org.cthul.fixsure.fluents;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
@FunctionalInterface
public interface BiTemplate<T, U> extends BiDataSource<T, U> {
    
    @Override
    default FlTemplate<T> onlyFirst() {
        return () -> newBiGenerator().onlyFirst();
    }

    @Override
    default FlTemplate<U> onlySecond() {
        return () -> newBiGenerator().onlySecond();
    }

    @Override
    default FlTemplate<Pair<T, U>> pairs() {
        return () -> newBiGenerator().pairs();
    }

    @Override
    default BiTemplate<T, U> filter(BiPredicate<? super T, ? super U> predicate) {
        return () -> newBiGenerator().filter(predicate);
    }

    @Override
    default BiTemplate<T, U> filter1(Predicate<? super T> predicate) {
        return () -> newBiGenerator().filter1(predicate);
    }

    @Override
    default BiTemplate<T, U> filter2(Predicate<? super U> predicate) {
        return () -> newBiGenerator().filter2(predicate);
    }

    @Override
    default <R> FlTemplate<R> map(BiFunction<? super T, ? super U, ? extends R> function) {
        return () -> newBiGenerator().map(function);
    }

    @Override
    default <R> BiTemplate<R, U> map1(Function<? super T, ? extends R> function) {
        return () -> newBiGenerator().map1(function);
    }

    @Override
    default <R> BiTemplate<T, R> map2(Function<? super U, ? extends R> function) {
        return () -> newBiGenerator().map2(function);
    }
    
    BiGenerator<T, U> newBiGenerator();

    @Override
    default BiGenerator<T, U> toBiGenerator() {
        return newBiGenerator();
    }
}
