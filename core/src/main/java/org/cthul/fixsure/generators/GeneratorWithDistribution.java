package org.cthul.fixsure.generators;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.distributions.UniformDistribution;
import org.cthul.fixsure.fluents.FlDistribution;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 * Base class for {@link Generator}s that require an distribution.
 * If none is specified, {@link UniformDistribution} will be used.
 * @param <T>
 */
public abstract class GeneratorWithDistribution<T> implements FlGenerator<T>{

    private final FlDistribution.FlRandom random;

//    public GeneratorWithDistribution() {
//        this((Distribution) null);
//    }
//
//    public GeneratorWithDistribution(Distribution distribution) {
//        this.random = toRnd(distribution, randomSeedHint());
//    }

    public GeneratorWithDistribution(long seedHint) {
        this.random = toRnd(null, seedHint);
    }

    public GeneratorWithDistribution(Distribution distribution, long seedHint) {
        this.random = toRnd(distribution, seedHint);
    }

    protected GeneratorWithDistribution(GeneratorWithDistribution src) {
        this.random = src.random.copy();
    }
    
    protected FlDistribution.FlRandom rnd() {
        return random;
    }

    @Override
    public long randomSeedHint() {
        return toSeed(getClass());
    }
    
    private static FlDistribution.FlRandom toRnd(Distribution distribution, long seedHint) {
        return distribution != null ? 
                FlDistribution.wrap(distribution.toRandomNumbers(seedHint)) : 
                UniformDistribution.uniform().toRandomNumbers(seedHint);
    }
}
