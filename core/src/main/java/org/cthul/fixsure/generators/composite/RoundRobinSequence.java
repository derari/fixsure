package org.cthul.fixsure.generators.composite;

import java.util.Arrays;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.fluents.FlSequence;

/**
 *
 */
public class RoundRobinSequence<T> implements FlSequence<T> {
    
    public static <T> RoundRobinSequence<T> rotate(Sequence<? extends T>... generators) {
        return new RoundRobinSequence<T>(generators);
    }
    
    public static <T> RoundRobinSequence<T> alternate(Sequence<? extends T>... generators) {
        return new RoundRobinSequence<T>(generators);
    }
    
    private final Sequence<? extends T>[] sequences;
    private long length = -3;
    private Class<?> valueType = void.class;

    public RoundRobinSequence(Sequence<? extends T>[] generators) {
        this.sequences = generators;
    }

    public RoundRobinSequence(Class<T> valueType, Sequence<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    protected RoundRobinSequence(RoundRobinSequence<T> src) {
        this.sequences = src.sequences.clone();
        this.length = src.length;
        this.valueType = src.valueType;
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) sequences);
        }
        return (Class) valueType;
    }

    @Override
    public T value(long n) {
        int g = (int) (n % sequences.length);
        long i = n / sequences.length;
        return sequences[g].value(i);
    }

    @Override
    public long length() {
        if (length == -3) {
            if (Arrays.stream(sequences).allMatch(Sequence::negativeIndices)) {
                length = Sequence.L_NEGATIVE_INDICES;
            } else if (Arrays.stream(sequences).allMatch(Sequence::isUnbounded)) {
                length = Sequence.L_UNBOUNDED;
            } else {
                long l = Long.MAX_VALUE;
                for (Sequence<?> s: sequences) {
                    long l2 = s.length();
                    if (l2 >= 0) l = Math.min(l, l2);
                }
                if ((l * sequences.length) / sequences.length != l) {
                    // long overflow
                    length = Sequence.L_UNBOUNDED;
                } else {
                    length = l * sequences.length;
                }
            }
        }
        return length;
    }

    @Override
    public long randomSeedHint() {
        long seed = DistributionRandomizer.toSeed(getClass());
        for (Sequence<?> s: sequences) {
            seed ^= GeneratorTools.getRandomSeedHint(s);
        }
        return seed;
    }
}
