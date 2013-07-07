package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.base.DistributionBase;
import org.hamcrest.Factory;

public class FluentDistributionWrapper extends DistributionBase {
    
    @Factory
    public static FlDistribution fluent(Distribution d) {
        if (d instanceof FlDistribution) {
            return (FlDistribution) d;
        }
        return new FluentDistributionWrapper(d);
    }
    
    private final Distribution d;

    public FluentDistributionWrapper(Distribution d) {
        this.d = d;
    }

    @Override
    public double map(double x) {
        return d.map(x);
    }
    
}
