package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorWithDistribution;
import org.hamcrest.Factory;

/**
 *
 */
public class BooleansGenerator 
                extends GeneratorWithDistribution<Boolean> 
                implements GeneratorTemplate<Boolean> {
    
    private static final BooleansGenerator DEFAULT = new BooleansGenerator();
    
    @Factory
    public static BooleansGenerator booleans() {
        return DEFAULT;
    }
    
    @Factory
    public static BooleansGenerator booleans(double threshold) {
        return new BooleansGenerator(threshold);
    }
    
    @Factory
    public static BooleansGenerator booleans(Distribution distribution) {
        return new BooleansGenerator(distribution);
    }

    private final double threshold;
    
    public BooleansGenerator() {
        this(0.5);
    }

    public BooleansGenerator(Distribution distribution) {
        this(0.5, distribution);
    }

    protected BooleansGenerator(BooleansGenerator src) {
        super(src);
        this.threshold = src.threshold;
    }

    public BooleansGenerator(double threshold) {
        this.threshold = threshold;
    }

    public BooleansGenerator(double threshold, Distribution distribution) {
        super(distribution);
        this.threshold = threshold;
    }

    @Override
    public Boolean next() {
        return distribution.next() >= threshold;
    }

    @Override
    public BooleansGenerator newGenerator() {
        return this;
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }
    
}
