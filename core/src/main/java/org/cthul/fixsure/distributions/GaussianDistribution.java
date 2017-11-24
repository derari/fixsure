package org.cthul.fixsure.distributions;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fluents.FlDistribution;

/**
 * A {@link Distribution} where each value has the same probability.
 */
public class GaussianDistribution extends AbstractDistribution {

    private static final GaussianDistribution INSTANCE = new GaussianDistribution(toSeed(GaussianDistribution.class), DistributionRandomizer.getGlobal().getSeedSupplier());
    private static final AtomicLong COUNTER = new AtomicLong();
    
    /**
     * Returns a normal or Gaussian distribution with default seed.
     * @return normal distribution
     */
    @Factory
    public static GaussianDistribution normalDistribution() {
        return INSTANCE;
    }
    
    /**
     * Returns a normal or Gaussian distribution with default seed.
     * @return normal distribution
     */
    @Factory
    public static GaussianDistribution normal() {
        return INSTANCE;
    }
    
    /**
     * Returns a normal or Gaussian distribution with the specified seed.
     * @param seed
     * @return normal distribution
     */
    @Factory
    public static GaussianDistribution normalDistribution(long seed) {
        return new GaussianDistribution(seed);
    }
    
    /**
     * Returns a normal or Gaussian distribution with the specified seed.
     * @param seed
     * @return normal distribution
     */
    @Factory
    public static GaussianDistribution normal(long seed) {
        return new GaussianDistribution(seed);
    }
    
    /**
     * Returns a new Gaussian random with a unique seed.
     * @return normal random
     */
    @Factory
    public static FlDistribution.FlRandom newNormalRandom() {
        return normal().toRandomNumbers(COUNTER.incrementAndGet());
    }

    public GaussianDistribution(LongSupplier seedSupplier) {
        super(seedSupplier);
    }

    public GaussianDistribution(long seed) {
        super(seed);
    }

    public GaussianDistribution(long seed, LongSupplier seedSupplier) {
        super(seed, seedSupplier);
    }

    protected GaussianDistribution(AbstractDistribution source) {
        super(source);
    }

    @Override
    protected FlRandom newRandom(long seed) {
        return new GDRandom(seed);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return super.toString(sb.append("Normal "));
    }
    
    protected static class GDRandom extends AbstractDistributionRandom {

        public GDRandom(long seed) {
            super(seed);
        }

        @Override
        protected double nextValue(Random rnd) {
            double d = 0.5 + rnd.nextGaussian() / 4;
            if (d < 0) return 0;
            if (d >= 1) return Math.nextDown(1d);
            return d;
        }
        
        @Override
        public StringBuilder toString(StringBuilder sb) {
            return super.toString(sb.append("Normal "));
        }
    }
}
