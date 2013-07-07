package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorWithDistribution;
import org.hamcrest.Factory;

/**
 * Generates integers in a given range.
 */
public class IntegersGenerator 
                extends GeneratorWithDistribution<Integer>
                implements GeneratorTemplate<Integer> {
    
    private static final IntegersGenerator DEFAULT = new IntegersGenerator();
    
    @Factory
    public static IntegersGenerator integers() {
        return DEFAULT;
    }
    
    @Factory
    public static IntegersGenerator integers(int high) {
        return new IntegersGenerator(high);
    }
    
    @Factory
    public static IntegersGenerator integers(int high, Distribution distribution) {
        return new IntegersGenerator(high, distribution);
    }
    
    @Factory
    public static IntegersGenerator integers(int low, int high) {
        return new IntegersGenerator(low, high);
    }
    
    @Factory
    public static IntegersGenerator integers(int low, int high, Distribution distribution) {
        return new IntegersGenerator(low, high, distribution);
    }
    
    protected static final int DEFAULT_LOW = 0;
    protected static final int DEFAULT_HIGH = 1 << 16;
    
    private final int base;
    private final int len;
    private final int mult;

    public IntegersGenerator() {
        this(DEFAULT_LOW, DEFAULT_HIGH, null);
    }

    public IntegersGenerator(int high) {
        this(DEFAULT_LOW, high, null);
    }
    
    public IntegersGenerator(int high, Distribution d) {
        this(DEFAULT_LOW, high, d);
    }
    
    public IntegersGenerator(int low, int high) {
        this(low, high, null);
    }
    
    public IntegersGenerator(int low, int high, Distribution d) {
        super(d);
        if (low == high) {
            throw new IllegalArgumentException(
                    "Empty range " + low + " - " + high);
        } else if (low < high) {
            this.base = low;
            this.len = high - low;
            this.mult = 1;
        } else {
            this.base = high;
            this.len = low - high;
            this.mult = -1;
        }
    }

    public IntegersGenerator(int base, int len, int mult) {
        this.base = base;
        this.len = len;
        this.mult = mult;
    }

    public IntegersGenerator(int base, int len, int mult, Distribution distribution) {
        super(distribution);
        this.base = base;
        this.len = len;
        this.mult = mult;
    }
    
    public int nextValue() {
        if (len == 1) return base;
        return base + mult * distribution.nextInt(len);
    }

    @Override
    public Integer next() {
        return nextValue();
    }

    @Override
    public Class<Integer> getValueType() {
        return Integer.class;
    }

    @Override
    public IntegersGenerator newGenerator() {
        return this;
    }
    
}
