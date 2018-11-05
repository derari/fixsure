package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.fixsure.SequenceLength;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.fluents.BiGenerator.Bag;
import org.cthul.fixsure.generators.AnonymousSequence;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
public interface BiSequence<T, U> extends BiTemplate<T, U>, SequenceLength {
    
    void value(long index, BiConsumer<? super T, ? super U> bag);
    
    @Override
    default FlSequence<T> onlyFirst() {
        Bag<T,U> bag = new Bag<>();
        return new AnonymousSequence<T>(this) {
            @Override
            public T value(long n) {
                BiSequence.this.value(n, bag);
                return bag.t;
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return BiSequence.this.toString(sb).append(".first()");
            }
        };
    }

    @Override
    default FlSequence<U> onlySecond() {
        Bag<T,U> bag = new Bag<>();
        return new AnonymousSequence<U>(this) {
            @Override
            public U value(long n) {
                BiSequence.this.value(n, bag);
                return bag.u;
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return BiSequence.this.toString(sb).append(".second()");
            }
        };
    }

    @Override
    default FlSequence<Pair<T, U>> pairs() {
        return map(Pair::new);
    }

    @Override
    default <R> FlSequence<R> map(BiFunction<? super T, ? super U, ? extends R> function) {
        return new AnonymousSequence<R>(this) {
            Bag<T,U> bag = new Bag<>();
            @Override
            public Class<R> getValueType() {
                return null;
            }
            @Override
            public R value(long n) {
                BiSequence.this.value(n, bag);
                return function.apply(bag.t, bag.u);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiSequence.this.toString(sb).append(".map(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
    }

    @Override
    default <R> BiSequence<R, U> map1(Function<? super T, ? extends R> function) {
        Bag<T,U> bag = new Bag<>();
        return new Anonymous<R, U>(this) {
            @Override
            public void value(long index, BiConsumer<? super R, ? super U> bag2) {
                BiSequence.this.value(index, bag);
                T t = bag.get1();
                U u = bag.get2();
                bag2.accept(function.apply(t), u);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiSequence.this.toString(sb).append(".map1(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
    }

    @Override
    default <R> BiSequence<T, R> map2(Function<? super U, ? extends R> function) {
        Bag<T,U> bag = new Bag<>();
        return new Anonymous<T, R>(this) {
            @Override
            public void value(long index, BiConsumer<? super T, ? super R> bag2) {
                BiSequence.this.value(index, bag);
                T t = bag.get1();
                U u = bag.get2();
                bag2.accept(t, function.apply(u));
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                BiSequence.this.toString(sb).append(".map2(");
                return GeneratorTools.lambdaToString(function, sb).append(')');
            }
        };
    }

    @Override
    public default BiGenerator<T, U> newBiGenerator() {
        return new BiGenerator.Anonymous<T, U>() {
            long index = 0;
            @Override
            public void next(BiConsumer<? super T, ? super U> bag) {
                SequenceLength.isInRange(index, BiSequence.this);
                value(index++, bag);
            }
            @Override
            public StringBuilder toString(StringBuilder sb) {
                return BiSequence.this.toString(sb)
                        .append('[').append(index).append(']');
            }
        };
    }
    
    static abstract class Anonymous<T, U> extends AbstractStringify implements BiSequence<T, U> {
        private final long length;

        public Anonymous(long length) {
            this.length = length;
        }
        

        public Anonymous(SequenceLength length) {
            this.length = SequenceLength.toLong(length);
        }

        @Override
        public long length() {
            return length;
        }
    }
    
//    static <T, U> BiSequence<T, U> create(SequenceLength length, BiConsumer<? super Long, ? super BiConsumer<? super T, ? super U>> function) {
//        long len = SequenceLength.toLong(length);
//        return new BiSequence<T, U>() {
//            @Override
//            public void value(long index, BiConsumer<? super T, ? super U> bag) {
//                function.accept(index, bag);
//            }
//            @Override
//            public long length() {
//                return len;
//            }
//        };
//    }
}
