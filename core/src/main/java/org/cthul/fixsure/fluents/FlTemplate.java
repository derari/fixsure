package org.cthul.fixsure.fluents;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.fixsure.*;
import org.cthul.fixsure.generators.AnonymousTemplate;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.values.EagerValues;
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
    default EagerValues<T> few() {
        return newGenerator().few();
    }

    @Override
    default EagerValues<T> some() {
        return newGenerator().some();
    }

    @Override
    default EagerValues<T> several() {
        return newGenerator().several();
    }

    @Override
    default EagerValues<T> many() {
        return newGenerator().many();
    }
        
    @Override
    default FlTemplate<T> distinct() {
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return FlTemplate.this.newGenerator().distinct();
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return FlTemplate.this.toString(sb).append(".distrinct()");
            }
        };
    }
    
    @Override
    default FlTemplate<T> filter(Predicate<? super T> predicate) {
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return FlTemplate.this.newGenerator().filter(predicate);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlTemplate.this.toString(sb).append(".filter(");
                GeneratorTools.lambdaToString(predicate, sb);
                return sb.append(")");
            }
        };
    }
    
    @Override
    default <R> FlTemplate<R> flatMap(Function<? super T, ? extends DataSource<R>> function) {
        return new AnonymousTemplate<R>() {
            @Override
            public FlGenerator<R> newGenerator() {
                return FlTemplate.this.newGenerator().flatMap(function);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlTemplate.this.toString(sb).append(".flatMap(");
                GeneratorTools.lambdaToString(function, sb);
                return sb.append(")");
            }
        };
    }
    
    @Override
    default <R> FlTemplate<R> map(Function<? super T, ? extends R> function) {
        return new AnonymousTemplate<R>() {
            @Override
            public FlGenerator<R> newGenerator() {
                return FlTemplate.this.newGenerator().map(function);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                FlTemplate.this.toString(sb).append(".map(");
                GeneratorTools.lambdaToString(function, sb);
                return sb.append(")");
            }
        };
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
        return new AnonymousTemplate<T>() {
            @Override
            public FlGenerator<T> newGenerator() {
                return FlTemplate.this.newGenerator().then(more);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                sb.append('{');
                GeneratorTools.printList(FlTemplate.this, Arrays.asList(more), sb);
                return sb.append('}');
            }
        };
    }

    @Override
    default FlTemplate<Values<T>> aggregate(DataSource<Integer> length) {
        return () -> newGenerator().aggregate(length);
    }

    @Override
    default FlTemplate<Values<T>> aggregate(int length) {
        return () -> newGenerator().aggregate(length);
    }

    @Override
    default FlTemplate<T> shuffle() {
        return () -> newGenerator().shuffle();
    }

    @Override
    default FlTemplate<T> shuffle(long seed) {
        return () -> newGenerator().shuffle(seed);
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
    default <U> BiTemplate<T, U> split(Function<? super T, ? extends U> function) {
        return () -> newGenerator().split(function);
    }

    @Override
    default <U, V> BiTemplate<U, V> split(BiConsumer<? super T, ? super BiConsumer<? super U, ? super V>> action) {
        return () -> newGenerator().split(action);
    }

    @Override
    default <U> BiTemplate<T, U> with(DataSource<U> source) {
        return () -> newGenerator().with(source);
    }
    
    default <R> R transform(Function<? super FlTemplate<? extends T>, ? extends R> function) {
        return function.apply(this);
    }
}
