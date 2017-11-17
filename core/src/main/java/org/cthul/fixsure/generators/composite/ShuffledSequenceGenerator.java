package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.Sequence;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.distributions.UniformDistribution;
import org.cthul.fixsure.fluents.FlDistribution;
import org.cthul.fixsure.fluents.FlDistribution.FlRandom;
import org.cthul.fixsure.generators.CopyableGenerator;

/**
 * Returns the elements of a sequence in random order.
 */
public class ShuffledSequenceGenerator<T> implements CopyableGenerator<T> {
    
    private static final long CLASS_SEED = toSeed(ShuffledSequenceGenerator.class);
    
    public static <T> ShuffledSequenceGenerator<T> shuffle(Sequence<T> seq) {
        return new ShuffledSequenceGenerator<>(seq);
    }
    
    private final Sequence<T> source;
    private final long l, m, first;
    private final int a, c;
    private long i = -1;

    public ShuffledSequenceGenerator(Sequence<T> source) {
        this(source, UniformDistribution.uniform(), CLASS_SEED);
    }
    
    public ShuffledSequenceGenerator(Sequence<T> source, Distribution randomSource, long seedHint) {
        this.source = source;
        long len = source.length();
        if (len < 0) len = Long.MAX_VALUE;
        l = len;
        m = nextPowerOf2(l);
        int mInt = (m > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) m;
        FlRandom rnd = FlDistribution.wrap(randomSource.toRandomNumbers(seedHint));
        c = rnd.nextInt(mInt / 2) * 2 + 1;
        a = rnd.nextInt(mInt / 4) * 4 + 1;
        first = rnd.nextLong() % m;
    }
    
    protected ShuffledSequenceGenerator(ShuffledSequenceGenerator<T> src) {
        this.source = src.source;
        this.l = src.l;
        this.m = src.m;
        this.c = src.c;
        this.a = src.a;
        this.first = src.first;
        this.i = src.i;
    }
    
    private long nextPowerOf2(long n) {
        if (n < 0 || n > (1L << 30))                                                                                                                  {
            return 1L << 30;
        }
        long p = 2;
        while (p < n) p <<= 1;
        return p;
    }

    @Override
    public T next() {
        if (i == first) {
            throw new GeneratorException();
        }
        if (i < 0) {
            i = first;
        }
        do {
            i = ((a * i + c) & Long.MAX_VALUE) % m;
            if (i < l) {
                return source.value(i);
            }
        } while (i != first);
        throw new GeneratorException();
    }

    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }

    @Override
    public ShuffledSequenceGenerator<T> copy() {
        return new ShuffledSequenceGenerator<>(this);
    }
    
}
