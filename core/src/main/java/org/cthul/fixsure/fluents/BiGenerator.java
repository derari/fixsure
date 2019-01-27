package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.generators.AnonymousGenerator;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
@FunctionalInterface
public interface BiGenerator<T, U> extends BiDataSource<T, U> {
    
    void next(BiConsumer<? super T, ? super U> bag);

    @Override
    default FlGenerator<T> onlyFirst() {
        return map((t, u) -> t);
    }

    @Override
    default FlGenerator<U> onlySecond() {
        return map((t, u) -> u);
    }

    @Override
    default FlGenerator<Pair<T, U>> pairs() {
        return map(Pair<T,U>::new);
    }

    @Override
    default BiGenerator<T, U> filter(BiPredicate<? super T, ? super U> predicate) {
        Bag<T, U> bag = new Bag<>();
        return new Anonymous<T, U>() {
            @Override
            public void next(BiConsumer<? super T, ? super U> bag2) {
                while (true) {
                    next(bag);
                    if (predicate.test(bag.get1(), bag.get2())) {
                        bag2.accept(bag.get1(), bag.get2());
                        return;
                    }
                }
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiGenerator.this.toString(sb).append(".filter(");
                GeneratorTools.lambdaToString(predicate, sb);
                return sb.append(')');
            }
        };
    }
    
    @Override
    default BiGenerator<T, U> filter1(Predicate<? super T> predicate) {
        return filter((t, u) -> predicate.test(t));
    }
    
    @Override
    default BiGenerator<T, U> filter2(Predicate<? super U> predicate) {
        return filter((t, u) -> predicate.test(u));
    }
    
    @Override
    default <R> FlGenerator<R> map(BiFunction<? super T, ? super U, ? extends R> function) {
        Bag<T, U> bag = new Bag<>();
        return new AnonymousGenerator<R>() {
            @Override
            public R next() {
                BiGenerator.this.next(bag);
                return function.apply(bag.get1(), bag.get2());
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiGenerator.this.toString(sb).append(".map(");
                GeneratorTools.lambdaToString(function, sb);
                return sb.append(')');
            }
        };
    }

    @Override
    default <R> BiGenerator<R, U> map1(Function<? super T, ? extends R> function) {
        Bag<T, U> bag = new Bag<>();
        return new Anonymous<R, U>() {
            @Override
            public void next(BiConsumer<? super R, ? super U> bag2) {
                BiGenerator.this.next(bag);
                T t = bag.get1();
                U u = bag.get2();
                bag2.accept(function.apply(t), u);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiGenerator.this.toString(sb).append(".map1(");
                GeneratorTools.lambdaToString(function, sb);
                return sb.append(')');
            }
        };
    }

    @Override
    default <R> BiGenerator<T, R> map2(Function<? super U, ? extends R> function) {
        Bag<T, U> bag = new Bag<>();
        return new Anonymous<T, R>() {
            @Override
            public void next(BiConsumer<? super T, ? super R> bag2) {
                BiGenerator.this.next(bag);
                T t = bag.get1();
                U u = bag.get2();
                bag2.accept(t, function.apply(u));
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiGenerator.this.toString(sb).append(".map2(");
                GeneratorTools.lambdaToString(function, sb);
                return sb.append(')');
            }
        };
    }

    @Override
    public default BiGenerator<T, U> toBiGenerator() {
        return this;
    }
    
    class Bag<T, U> implements BiConsumer<T, U> {
        private T t;
        private U u;

        @Override
        public void accept(T t, U u) {
            this.t = t;
            this.u = u;
        }
        
        public T get1() {
            return t;
        }
        
        public U get2() {
            return u;
        }
    }
    
    abstract class Anonymous<T, U> extends AbstractStringify implements BiGenerator<T, U> {
        
    }
}
