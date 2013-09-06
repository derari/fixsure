package org.cthul.fixsure.generators.composite;

import java.util.Random;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.distributions.DistributionRandom;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 * Returns the elements of a sequence in random order.
 */
public class ShufflingGenerator<T> 
                extends GeneratorBase<T>
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> ShufflingGenerator<T> shuffle(Sequence<T> seq) {
        return new ShufflingGenerator<>(seq);
    }
    
    private final Sequence<T> source;
    private final long l, m, first;
    private final int a, c;
    private long i = -1;

    public ShufflingGenerator(Sequence<T> source) {
        this.source = source;
        long len = source.length();
        if (len < 0) len = Long.MAX_VALUE;
        l = len;
        m = nextPowerOf2(l);
        int mInt = (m > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) m;
        Random rnd = DistributionRandom.rnd();
        c = rnd.nextInt(mInt / 2) * 2 + 1;
        a = rnd.nextInt(mInt / 4) * 4 + 1;
        first = rnd.nextLong() % m;
    }
    
    protected ShufflingGenerator(ShufflingGenerator<T> src) {
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
            i = (a * i + c) % m;
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
    public ShufflingGenerator<T> newGenerator() {
        return new ShufflingGenerator<>(this);
    }
    
}
