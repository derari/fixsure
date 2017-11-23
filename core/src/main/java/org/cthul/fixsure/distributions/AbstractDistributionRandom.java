package org.cthul.fixsure.distributions;

import java.util.Random;
import org.cthul.fixsure.fluents.FlDistribution;

/**
 * Base class for {@link RandomNumbers}s.
 */
public abstract class AbstractDistributionRandom implements FlDistribution.FlRandom {
    
    private final Random myRandom;
//    private boolean dirty = false;
//    private long resetCount = -1;

    public AbstractDistributionRandom(long seed) {
        myRandom = new Random(seed);
    }
//    
//    protected AbstractDistributionRandom(AbstractDistributionRandom source) {
//        source.checkReset();
//        this.classSeed = source.classSeed;
//        this.randomizedSeed = source.randomizedSeed;
//        this.resetCounter = source.resetCounter;
//        this.resetCount = source.resetCount;
//        if (source.dirty) {
//            this.dirty = true;
//            this.myRandom.setSeed(getSeed(source.myRandom));
//        } else {
//            myRandom.setSeed(source.initialSeed());
//        }
//    }
//    
//    protected long initialSeed() {
//        if (randomizedSeed == null) {
//            return classSeed;
//        }
//        return classSeed ^ randomizedSeed.getAsLong();
//    }
//    
//    protected void checkReset() {
//        if (resetCounter == null) return;
//        long reset = resetCounter.getAsLong();
//        if (reset != resetCount) {
//            resetCount = reset;
//            myRandom.setSeed(initialSeed());
//            dirty = false;
//        }
//    }

    /**
     * The {@link Random} that should be used for generating values.
     * @return random
     */
    protected Random rnd() {
//        checkReset();
//        dirty = true;
        return myRandom;
    }

    /** {@inheritDoc} */
    @Override
    public double nextValue() {
        return nextValue(rnd());
    }
    
    protected abstract double nextValue(Random rnd);

//    public abstract FlDistribution.FlRandom copy();
//    
//    protected static long getSeed(Random rnd) {
//        if (F_SEED_EX != null) {
//            throw new RuntimeException(F_SEED_EX);
//        }
//        try {
//            AtomicLong seed = (AtomicLong) F_RANDOM_SEED.get(rnd);
//            return seed.get() ^ MULT;
//        } catch (ReflectiveOperationException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//    
//    private static final long MULT = 0x5DEECE66DL;
//    private static final Field F_RANDOM_SEED;
//    private static final ReflectiveOperationException F_SEED_EX;
//    
//    static {
//        Field fSeed = null;
//        ReflectiveOperationException ex = null;
//        try {
//            fSeed = Random.class.getDeclaredField("seed");
//            fSeed.setAccessible(true);
//        } catch (ReflectiveOperationException rox) {
//            ex = rox;
//        }
//        F_RANDOM_SEED = fSeed;
//        F_SEED_EX = ex;
//    }
}
