package org.cthul.fixsure.fluents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.SequenceLength;
import org.cthul.fixsure.fluents.BiGenerator.Bag;

/**
 *
 */
public interface BiSequence<T, U> extends BiTemplate<T, U>, SequenceLength {
    
    void value(long index, BiConsumer<? super T, ? super U> bag);
    
    @Override
    default FlSequence<T> onlyFirst() {
        Bag<T,U> bag = new Bag<>();
        return Sequence.sequence(this, l -> {
            value(l, bag);
            return bag.t;
        });
    }

    @Override
    default FlSequence<U> onlySecond() {
        Bag<T,U> bag = new Bag<>();
        return Sequence.sequence(this, l -> {
            value(l, bag);
            return bag.u;
        });
    }

    @Override
    default FlSequence<Pair<T, U>> pairs() {
        return map(Pair::new);
    }

    @Override
    default <R> FlSequence<R> map(BiFunction<? super T, ? super U, ? extends R> function) {
        Bag<T,U> bag = new Bag<>();
        return Sequence.sequence(this, l -> {
            value(l, bag);
            return function.apply(bag.t, bag.u);
        });
    }

    @Override
    default <R> BiSequence<R, U> map1(Function<? super T, ? extends R> function) {
        Bag<T,U> bag = new Bag<>();
        return create(this, (l, bag2) -> {
            value(l, bag);
            T t = bag.get1();
            U u = bag.get2();
            bag2.accept(function.apply(t), u);
        });
    }

    @Override
    default <R> BiSequence<T, R> map2(Function<? super U, ? extends R> function) {
        Bag<T,U> bag = new Bag<>();
        return create(this, (l, bag2) -> {
            value(l, bag);
            T t = bag.get1();
            U u = bag.get2();
            bag2.accept(t, function.apply(u));
        });
    }

    @Override
    public default BiGenerator<T, U> newBiGenerator() {
        return new BiGenerator<T, U>() {
            long index = 0;
            @Override
            public void next(BiConsumer<? super T, ? super U> bag) {
                SequenceLength.isInRange(index, BiSequence.this);
                value(index++, bag);
            }
        };
    }
    
    static <T, U> BiSequence<T, U> create(SequenceLength length, BiConsumer<? super Long, ? super BiConsumer<? super T, ? super U>> function) {
        long len = SequenceLength.toLong(length);
        return new BiSequence<T, U>() {
            @Override
            public void value(long index, BiConsumer<? super T, ? super U> bag) {
                function.accept(index, bag);
            }
            @Override
            public long length() {
                return len;
            }
        };
    }
}
