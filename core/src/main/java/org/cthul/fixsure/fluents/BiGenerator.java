package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

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
        return bag2 -> {
            while (true) {
                next(bag);
                if (predicate.test(bag.get1(), bag.get2())) {
                    bag2.accept(bag.get1(), bag.get2());
                    return;
                }
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
        return () -> {
            next(bag);
            return function.apply(bag.get1(), bag.get2());
        };
    }

    @Override
    default <R> BiGenerator<R, U> map1(Function<? super T, ? extends R> function) {
        Bag<T, U> bag = new Bag<>();
        return bag2 -> {
            next(bag);
            T t = bag.get1();
            U u = bag.get2();
            bag2.accept(function.apply(t), u);
        };
    }

    @Override
    default <R> BiGenerator<T, R> map2(Function<? super U, ? extends R> function) {
        Bag<T, U> bag = new Bag<>();
        return bag2 -> {
            next(bag);
            T t = bag.get1();
            U u = bag.get2();
            bag2.accept(t, function.apply(u));
        };
    }

    @Override
    public default BiGenerator<T, U> toBiGenerator() {
        return this;
    }
    
//    class BiIterator<T, U> {
//        private final BiGenerator<T, U> gen;
//        private final Bag<T, U> bag = new Bag<>();
//
//        public BiIterator(BiGenerator<T, U> gen) {
//            this.gen = gen;
//        }
//        
//        public BiIterator<T, U> next() {
//            gen.next(bag);
//            return this;
//        }
//        
//        public T get1() {
//            return bag.get1();
//        }
//        
//        public U get2() {
//            return bag.get2();
//        }
//    }
    
    class Bag<T, U> implements BiConsumer<T, U> {
        public T t;
        public U u;

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
}
