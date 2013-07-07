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
    public int nextInt(int n) {
        return rnd().nextInt(n);
    }

    @Override
    public UniformDistribution newGenerator() {
        return this;
    }
}
