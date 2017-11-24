package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.api.Factory;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.AnonymousTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 * Generates random integers in a given range.
 */
public class RandomIntegersGenerator 
                extends GeneratorWithDistribution<Integer>
                implements CopyableGenerator<Integer> {
    
    private static final long CLASS_SEED = toSeed(RandomIntegersGenerator.class);
    protected static final int DEFAULT_LOW = 0;
    protected static final int DEFAULT_HIGH = 1 << 17;    
    private static Template TEMPLATE = new Template(null, DEFAULT_LOW, DEFAULT_HIGH, 1, null);
    
    /**
     * Generates random integers.
     * @return random integers
     */
    @Factory
    public static Template integers() {
        return TEMPLATE;
    }
    
    /**
     * Generates random integers.
     * @param end upper bound, exclusive
     * @return random integers
     */
    @Factory
    public static Template integers(int end) {
        return TEMPLATE.lessThan(end);
    }
    
    /**
     * Generates random integers.
     * @param end upper bound, exclusive
     * @param distribution
     * @return random integers
     */
    @Factory
    public static FlTemplate<Integer> integers(int end, Distribution distribution) {
        return TEMPLATE.lessThan(end).random(distribution);
    }
    
    /**
     * Generates random integers.
     * @param start
     * @param end upper bound, exclusive
     * @return random integers
     */
    @Factory
    public static Template integers(int start, int end) {
        return TEMPLATE.between(start, end);
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
        return TEMPLATE.between(start, end).random(distribution);
    }
    
    private final int base;
    private final int len;
    private final int mult;
    
    public RandomIntegersGenerator() {
        this(DEFAULT_LOW, DEFAULT_HIGH);
    }
    
    public RandomIntegersGenerator(int end) {
        this(DEFAULT_LOW, end);
    }

    public RandomIntegersGenerator(int end, Distribution distribution) {
        this(DEFAULT_LOW, end, distribution);
    }

    public RandomIntegersGenerator(int end, Distribution distribution, long seedHint) {
        this(DEFAULT_LOW, end, distribution, seedHint);
    }
    
    public RandomIntegersGenerator(int start, int end) {
        this(start, end, null);
    }

    public RandomIntegersGenerator(int start, int end, Distribution distribution) {
        this(start, end, distribution, CLASS_SEED ^ start ^ ((long) end << 32));
    }

    public RandomIntegersGenerator(int start, int end, Distribution distribution, long seedHint) {
        this(start, end, 1, distribution, seedHint);
    }

    public RandomIntegersGenerator(int start, int end, int step, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        step = Math.abs(step);
        if (start == end) {
            throw new IllegalArgumentException(
                    "Empty range " + start + " - " + end);
        } if (step == 0) {
            throw new IllegalArgumentException("Step must not be zero");
        } else if (start < end) {
            this.base = start;
            this.len = (end - start) / step;
            this.mult = step;
        } else {
            this.base = end;
            this.len = (start - end) / step;
            this.mult = -step;
        }
    }
    
    public RandomIntegersGenerator(RandomIntegersGenerator src) {    
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
    public RandomIntegersGenerator copy() {
        return new RandomIntegersGenerator(this);
    }

    @Override
    public long randomSeedHint() {
        return CLASS_SEED ^ (len * mult) ^ ((long) base << 32);
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        sb.append('{').append(base).append('-').append(base+len);
        if (Math.abs(mult) != 1) sb.append(';').append(Math.abs(mult));
        return super.toString(sb.append("}[")).append(']');
    }
    
    public static class Template extends AnonymousTemplate<Integer> {

        private final Distribution distribution;
        private final int start, end, step;
        private final Long seed;

        public Template(Distribution distribution, int start, int end, int step, Long seed) {
            this.distribution = distribution;
            this.start = start;
            this.end = end;
            this.step = step;
            this.seed = seed;
        }

        @Override
        public Class<Integer> getValueType() {
            return Integer.class;
        }

        @Override
        public FlGenerator<Integer> newGenerator() {
            long s = seed != null ? seed : CLASS_SEED ^ (end*step) ^ ((long) start << 32);
            return new RandomIntegersGenerator(start, end, step, distribution, s);
        }
        
        public Template from(int start) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public Template to(int max) {
            return new Template(distribution, start, max+1, step, seed);
        }
        
        public Template lessThan(int end) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public Template between(int start, int end) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public Template step(int step) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public FlTemplate<Integer> random(Distribution distribution) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public FlTemplate<Integer> random(long seed) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public FlTemplate<Integer> random(Distribution distribution, long seed) {
            return new Template(distribution, start, end, step, seed);
        }
        
        public ConsecutiveIntegersSequence ordered() {
            return new ConsecutiveIntegersSequence(start, step, end);
        }
    }
}
