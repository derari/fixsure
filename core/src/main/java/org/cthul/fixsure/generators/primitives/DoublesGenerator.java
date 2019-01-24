package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 * Generates random doubles in a given range.
 */
public class DoublesGenerator 
                extends GeneratorWithDistribution<Double>
                implements CopyableGenerator<Double> {
    
    private static final long CLASS_SEED = toSeed(DoublesGenerator.class);
    
    /**
     * Generates random doubles.
     * @return random doubles
     */
    @Factory
    public static FlTemplate<Double> doubles() {
        return () -> new DoublesGenerator();
    }
    
    /**
     * Generates random doubles.
     * @param high
     * @return random doubles
     */
    @Factory
    public static FlTemplate<Double> doubles(int high) {
        return () -> new DoublesGenerator(high);
    }
    
    /**
     * Generates random doubles.
     * @return random doubles
     */
    @Factory
    public static FlTemplate<Double> doubles(int high, Distribution distribution) {
        return () -> new DoublesGenerator(high, distribution);
    }
    
    /**
     * Generates random doubles.
     * @param low
     * @param high
     * @return random doubles
     */
    @Factory
    public static FlTemplate<Double> doubles(int low, int high) {
        return () -> new DoublesGenerator(low, high);
    }
    
    /**
     * Generates random doubles.
     * @param low
     * @param high
     * @param distribution
     * @return random doubles
     */
    @Factory
    public static FlTemplate<Double> doubles(int low, int high, Distribution distribution) {
        return () -> new DoublesGenerator(low, high, distribution);
    }
    
    protected static final double DEFAULT_HIGH = 1 << 16;
    protected static final double DEFAULT_LOW = - DEFAULT_HIGH;
    
    private final double base;
    private final double len;
    
    public DoublesGenerator() {
        this(DEFAULT_LOW, DEFAULT_HIGH);
    }
    
    public DoublesGenerator(double high) {
        this(0, high);
    }

    public DoublesGenerator(double high, Distribution distribution) {
        this(0, high, distribution);
    }

    public DoublesGenerator(double high, Distribution distribution, long seedHint) {
        this(0, high, distribution, seedHint);
    }
    
    public DoublesGenerator(double low, double high) {
        this(low, high, null);
    }

    public DoublesGenerator(double low, double high, Distribution distribution) {
        this(low, high, distribution, CLASS_SEED ^ Double.doubleToLongBits(low) ^ Double.doubleToLongBits(high));
    }

    public DoublesGenerator(double low, double high, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        if (low == high) {
            throw new IllegalArgumentException(
                    "Empty range " + low + " - " + high);
        }
        this.base = low;
        this.len = high - low;
    }
    
    protected DoublesGenerator(DoublesGenerator src) {    
        super(src);
        this.base = src.base;
        this.len = src.len;
    }

    public double nextValue() {
        return base + len * rnd().nextValue();
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
    public DoublesGenerator copy() {
        return new DoublesGenerator(this);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append(String.format("{%.2f-%.2f}[", base, base+len));
        return super.toString(sb).append(']');
    }
}
