package org.cthul.fixsure.generators.primitives;

import java.util.Locale;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 * Generates random booleans.
 */
public class BooleansGenerator 
                extends GeneratorWithDistribution<Boolean> 
                implements CopyableGenerator<Boolean> {
    
    private static final long CLASS_SEED = toSeed(BooleansGenerator.class);
    
    /**
     * Generates random booleans
     * @return random booleans
     */
    @Factory
    public static FlTemplate<Boolean> booleans() {
        return () -> new BooleansGenerator();
    }
    
    /**
     * Generates random booleans.
     * @param ratio ratio of true values
     * @return random booleans
     */
    @Factory
    public static FlTemplate<Boolean> booleans(double ratio) {
        return () -> new BooleansGenerator(ratio);
    }
    
    /**
     * Generates random booleans
     * @param distribution
     * @return random booleans
     */
    @Factory
    public static FlTemplate<Boolean> booleans(Distribution distribution) {
        return () -> new BooleansGenerator(distribution);
    }
    
    @Factory
    public static FlTemplate<Boolean> booleans(double threshold, Distribution distribution, long seedHint) {
        return () -> new BooleansGenerator(threshold, distribution, seedHint);
    }

    private final double ratio;

    public BooleansGenerator() {
        this(null, CLASS_SEED);
    }

    public BooleansGenerator(Distribution distribution) {
        this(distribution, CLASS_SEED);
    }

    public BooleansGenerator(Distribution distribution, long seedHint) {
        this(0.5, distribution, seedHint);
    }

    public BooleansGenerator(double threshold) {
        this(threshold, null);
    }

    public BooleansGenerator(double threshold, Distribution distribution) {
        this(threshold, distribution, CLASS_SEED);
    }

    public BooleansGenerator(double threshold, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        this.ratio = threshold;
    }

    protected BooleansGenerator(BooleansGenerator src) {
        super(src);
        this.ratio = src.ratio;
    }

    @Override
    public Boolean next() {
        return rnd().nextValue() < ratio;
    }

    @Override
    public Class<Boolean> getValueType() {
        return Boolean.class;
    }

    @Override
    public BooleansGenerator copy() {
        return new BooleansGenerator(this);
    }
    
    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append("Booleans(");
        sb.append(String.format(Locale.ENGLISH, "%.2f>", ratio));
        return super.toString(sb).append(')');
    }
}
