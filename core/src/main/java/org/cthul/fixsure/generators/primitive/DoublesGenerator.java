package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorWithDistribution;
import org.hamcrest.Factory;

/**
 * Generates integers in a given range.
 */
public class DoublesGenerator 
                extends GeneratorWithDistribution<Double>
                implements GeneratorTemplate<Double> {
    
    private static final DoublesGenerator DEFAULT = new DoublesGenerator();
    
    @Factory
    public static DoublesGenerator doubles() {
        return DEFAULT;
    }
    
    @Factory
    public static DoublesGenerator doubles(int high) {
        return new DoublesGenerator(high);
    }
    
    @Factory
    public static DoublesGenerator doubles(int high, Distribution distribution) {
        return new DoublesGenerator(high, distribution);
    }
    
    @Factory
    public static DoublesGenerator doubles(int low, int high) {
        return new DoublesGenerator(low, high);
    }
    
    @Factory
    public static DoublesGenerator doubles(int low, int high, Distribution distribution) {
        return new DoublesGenerator(low, high, distribution);
    }
    
    protected static final double DEFAULT_HIGH = 1 << 16;
    protected static final double DEFAULT_LOW = - DEFAULT_HIGH;
    
    private final double base;
    private final double len;

    public DoublesGenerator() {
        this(DEFAULT_LOW, DEFAULT_HIGH, null);
    }

    public DoublesGenerator(double high) {
        this(0, high, null);
    }
    
    public DoublesGenerator(double high, Distribution d) {
        this(0, high, d);
    }
    
    public DoublesGenerator(double low, double high) {
        this(low, high, null);
    }
    
    public DoublesGenerator(double low, double high, Distribution d) {
        super(d);
        if (low == high) {
            throw new IllegalArgumentException(
                    "Empty range " + low + " - " + high);
        }
        this.base = low;
        this.len = high - low;
    }
    
    public double nextValue() {
        return base + len * distribution.next();
    }

    @Override
    public Double next() {
        return nextValue();
    }

    @Override
    public Class<Double> getValueType() {
        return Double.class;
    }

    @Override
    public DoublesGenerator newGenerator() {
        return this;
    }
    
}
