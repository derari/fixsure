package org.cthul.fixsure;

import org.cthul.fixsure.api.Factory;
import java.util.function.LongFunction;
import static org.cthul.fixsure.SequenceLength.isInRange;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 * A source of values supporting random access.
 * @param <T>
 */
public interface Sequence<T> extends Template<T>, SequenceLength {
    
    /**
     * Returns the nth element. 
     * @param n
     * @return value
     * @throws IndexOutOfBoundsException
     */
    T value(long n);

    @Override
    default FlGenerator<T> newGenerator() {
        class SequenceElements implements CopyableGenerator<T> {
            private final Class<T> type = Typed.typeOf(Sequence.this);
            private long index = 0;
            @Override
            public T next() {
                if (isUnbounded()) {
                    if (index < 0 && !negativeIndices()) {
                        index = 0;
                    }
                } else if (index >= length()) {
                    throw new GeneratorException(new IndexOutOfBoundsException("" + index));
                }
                return value(index++);
            }
            @Override
            public Class<T> getValueType() {
                return type;
            }
            @Override
            public CopyableGenerator<T> copy() {
                SequenceElements copy = new SequenceElements();
                copy.index = index;
                return copy;
            }
            @Override
            public long randomSeedHint() {
                return GeneratorTools.getRandomSeedHint(Sequence.this);
            }
        }
        return new SequenceElements();
    }
    
    @Override
    default FlSequence<T> fluentData() {
        return sequence(this, this::value);
    }
    
    /**
     * Creates a sequence with the given length from a long function.
     * @param <T>
     * @param length
     * @param function
     * @return sequence
     */
    static <T> FlSequence<T> sequence(SequenceLength length, LongFunction<T> function) {
        return sequence(SequenceLength.toLong(length), function);
    }
    
    /**
     * Converts a function into an unbounded sequence.
     * @param <T>
     * @param function
     * @return sequence
     */
    @Factory
    static <T> FlSequence<T> sequence(LongFunction<T> function) {
        return sequence(L_UNBOUNDED, function);
    }
    
    /**
     * Converts a function into a bounded sequence.
     * @param <T>
     * @param length
     * @param function
     * @return sequence
     */
    @Factory
    static <T> FlSequence<T> sequence(long length, LongFunction<T> function) {
        Class<T> clazz = Typed.typeOf(function);
        return sequence(clazz, length, function);
    }
    
    /**
     * Converts a function into a typed, bounded sequence
     * @param <T>
     * @param clazz
     * @param length
     * @param function
     * @return sequence
     */
    @Factory
    static <T> FlSequence<T> sequence(Class<T> clazz, long length, LongFunction<T> function) {
        class TypedSequence implements FlSequence<T> {
            @Override
            public T value(long n) {
                if (!isInRange(n, this)) {
                    throw new IndexOutOfBoundsException("" + n);
                }
                return function.apply(n);
            }
            @Override
            public Class<T> getValueType() {
                return clazz;
            }
            @Override
            public long length() {
                return length;
            }
        }
        return new TypedSequence();
    }
}
