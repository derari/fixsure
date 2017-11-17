package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 * Generates random integers in a given range.
 */
public class IntegersGenerator 
                extends GeneratorWithDistribution<Integer>
                implements CopyableGenerator<Integer> {
    
    private static final long CLASS_SEED = toSeed(IntegersGenerator.class);
    
    /**
     * Generates random integers.
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers() {
        return () -> new IntegersGenerator();
    }
    
    /**
     * Generates random integers.
     * @param end upper bound, exclusive
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers(int end) {
        return () -> new IntegersGenerator(end);
    }
    
    /**
     * Generates random integers.
     * @param end upper bound, exclusive
     * @param distribution
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers(int end, Distribution distribution) {
        return () -> new IntegersGenerator(end, distribution);
    }
    
    /**
     * Generates random integers.
     * @param start
     * @param end upper bound, exclusive
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers(int start, int end) {
        return () -> new IntegersGenerator(start, end);
    }
    
    /**
     * Generates random integers.
     * @param start
     * @param end upper bound, exclusive
     * @param distribution
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers(int start, int end, Distribution distribution) {
        return () -> new IntegersGenerator(start, end, distribution);
    }
    
    protected static final int DEFAULT_LOW = 0;
    protected static final int DEFAULT_HIGH = 1 << 17;
    
    private final int base;
    private final int len;
    private final int mult;
    
    public IntegersGenerator() {
        this(DEFAULT_LOW, DEFAULT_HIGH);
    }
    
    public IntegersGenerator(int end) {
        this(DEFAULT_LOW, end);
    }

    public IntegersGenerator(int end, Distribution distribution) {
        this(DEFAULT_LOW, end, distribution);
    }

    public IntegersGenerator(int end, Distribution distribution, long seedHint) {
        this(DEFAULT_LOW, end, distribution, seedHint);
    }
    
    public IntegersGenerator(int start, int end) {
        this(start, end, null);
    }

    public IntegersGenerator(int start, int end, Distribution distribution) {
        this(start, end, distribution, CLASS_SEED ^ start ^ ((long) end << 32));
    }

    public IntegersGenerator(int start, int end, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        if (start == end) {
            throw new IllegalArgumentException(
                    "Empty range " + start + " - " + end);
        } else if (start < end) {
            this.base = start;
            this.len = end - start;
            this.mult = 1;
        } else {
            this.base = end;
            this.len = start - end;
            this.mult = -1;
        }
    }
    
    public IntegersGenerator(IntegersGenerator src) {    
        super(src);
        this.base = src.base;
        this.len = src.len;
        this.mult = src.mult;
    }

    public int nextValue() {
        if (len == 1) return base;
        return base + mult * rnd().nextInt(len);
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
    public IntegersGenerator copy() {
        return new IntegersGenerator(this);
    }
}
