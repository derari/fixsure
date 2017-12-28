package org.cthul.fixsure.distributions;

import java.util.Random;
import java.util.function.LongSupplier;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;

/**
 * A {@link Distribution} where each value has the same probability.
 */
public class UniformDistribution extends AbstractDistribution {

    private static final UniformDistribution INSTANCE = new UniformDistribution(toSeed(UniformDistribution.class), DistributionRandomizer.getGlobal().getSeedSupplier());
    
    /**
     * Returns a uniform distribution with default seed.
     * @return uniform distribution
     */
    @Factory
    public static UniformDistribution uniformDistribution() {
        return INSTANCE;
    }
    
    /**
     * Returns a uniform distribution with default seed.
     * @return uniform distribution
     */
    @Factory
    public static UniformDistribution uniform() {
        return INSTANCE;
    }
    
    /**
     * Returns a uniform distribution with the specified seed.
     * @param seed
     * @return uniform distribution
     */
    @Factory
    public static UniformDistribution uniformDistribution(long seed) {
        return new UniformDistribution(seed);
    }
    
    /**
     * Returns a uniform distribution with the specified seed.
     * @param seed
     * @return uniform distribution
     */
    @Factory
    public static UniformDistribution uniform(long seed) {
        return new UniformDistribution(seed);
    }
    
    public UniformDistribution(LongSupplier seedSupplier) {
        super(seedSupplier);
    }

    public UniformDistribution(long seed) {
        super(seed);
    }

    public UniformDistribution(long seed, LongSupplier seedSupplier) {
        super(seed, seedSupplier);
    }

    protected UniformDistribution(AbstractDistribution source) {
        super(source);
    }

    @Override
    protected FlRandom newRandom(long seed) {
        return new UDRandom(seed);
    }
    
    @Override
    public StringBuilder toString(StringBuilder sb) {
        return super.toString(sb.append("Uniform "));
    }
    
    protected static class UDRandom extends AbstractDistributionRandom {

        public UDRandom(long seed) {
            super(seed);
        }

        @Override
        protected double nextValue(Random rnd) {
            return rnd.nextDouble();
        }

        @Override
        public int nextInt() {
            return rnd().nextInt();
        }

        @Override
        public int nextPositiveInt() {
            return rnd().nextInt() >>> 1;
        }

        @Override
        public int nextInt(int n) {
            return rnd().nextInt(n);
        }

        @Override
        public long nextLong() {
            return rnd().nextLong();
        }

        @Override
        public long nextPositiveLong() {
            return rnd().nextLong() >>> 1;
        }

        @Override
        public long nextLong(long n) {
            return nextPositiveLong() % n;
        }
        
        @Override
        public StringBuilder toString(StringBuilder sb) {
            return super.toString(sb.append("Uniform "));
        }
    }
}
