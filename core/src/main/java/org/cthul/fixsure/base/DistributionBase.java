package org.cthul.fixsure.base;

import java.util.Random;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.distributions.DistributionRandom;
import org.cthul.fixsure.fluents.FlDistribution;

/**
 * Base class for {@link Distribution}s.
 */
public abstract class DistributionBase 
                extends GeneratorBase<Double> 
                implements FlDistribution {

    /**
     * The {@link Random} that should be used for genrating values.
     * @return random
     */
    protected Random rnd() {
        return DistributionRandom.rnd();
    }
    
    /** {@inheritDoc} */
    @Override
    public Double next() {
        return nextValue();
    }

    /** {@inheritDoc} */
    @Override
    public double nextValue() {
        double d = rnd().nextDouble();
        return map(d);
    }

    /** {@inheritDoc} */
    @Override
    public abstract double map(double x);

    /** {@inheritDoc} */
    @Override
    public int nextInt() {
        return (int) (Integer.MAX_VALUE * (nextValue()-0.5));
    }

    /** {@inheritDoc} */
    @Override
    public int nextInt(int n) {
        return (int) (n * nextValue());
    }
    
    /** {@inheritDoc} */
    @Override
    public long nextLong() {
        return (long) (Long.MAX_VALUE * (nextValue()-0.5));
    }

    /** {@inheritDoc} */
    @Override
    public long nextLong(long n) {
        return (long) (n * nextValue());
    }

    /**
     * {@inheritDoc}
     * @return {@code Double.class}
     */
    @Override
    public Class<Double> getValueType() {
        return Double.class;
    }
    
}
