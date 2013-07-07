package org.cthul.fixsure.base;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.UniformDistribution;
import org.cthul.fixsure.fluents.FlDistribution;
import org.cthul.fixsure.fluents.FluentDistributionWrapper;

/**
 * Base class for {@link Generator}s that require an distribution.
 * If none is specified, {@link UniformDistribution} will be used.
 */
public abstract class GeneratorWithDistribution<T> extends GeneratorBase<T>{
    
    protected final FlDistribution distribution;

    public GeneratorWithDistribution() {
        this((Distribution) null);
    }

    public GeneratorWithDistribution(Distribution distribution) {
        this.distribution = distribution != null ? 
                FluentDistributionWrapper.fluent(distribution) : 
                UniformDistribution.uniformDistribution();
    }

    protected GeneratorWithDistribution(GeneratorWithDistribution src) {
        this.distribution = src.distribution;
    }
   
}
