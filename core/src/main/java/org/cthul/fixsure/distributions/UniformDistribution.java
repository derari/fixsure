package org.cthul.fixsure.distributions;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.base.DistributionBase;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 * A {@link Distribution} where each value has the same propability.
 */
public class UniformDistribution 
                extends DistributionBase
                implements FlGeneratorTemplate<Double> {

    private static final UniformDistribution INSTANCE = new UniformDistribution();
    
    @Factory
    public static UniformDistribution uniformDistribution() {
        return INSTANCE;
    }
    
    @Factory
    public static UniformDistribution uniform() {
        return INSTANCE;
    }
    
    @Override
    public double map(double x) {
        return x;
    }

    @Override
    public int nextInt() {
        return rnd().nextInt();
    }

    @Override
    public int nextPositiveInt() {
        return rnd().nextInt() >>> 1;
    }

    @Override
    public int nextInt(int n) {
        return rnd().nextInt(n);
    }

    @Override
    public long nextLong() {
        return rnd().nextLong();
    }

    @Override
    public long nextPositiveLong() {
        return rnd().nextLong() >>> 1;
    }

    @Override
    public long nextLong(long n) {
        return nextPositiveLong() % n;
    }

    @Override
    public UniformDistribution newGenerator() {
        return this;
    }
}
