package org.cthul.fixsure.distributions;

import java.util.Random;

public final class DistributionRandom {
    
    private static final DistributionRandom globalRnd = new DistributionRandom();
    private static ThreadLocal<DistributionRandom> localRnd = null;
    private static long STRING_SEED = "DistributionRandom".hashCode() << (long) 32;
    
    public static Random rnd() {
        if (localRnd != null) {
            DistributionRandom r = localRnd.get();
            if (r != null) return r.getRandom();
        }
        return globalRnd.getRandom();
    }
    
    public static void setSeed(long seed) {
        globalRnd._setSeed(seed);
    }
    
    public static void setSeed(String seed) {
        setSeed(STRING_SEED | seed.hashCode());
    }
    
    public static void setSeed(String seed1, String seed2) {
        setSeed((seed1.hashCode() << (long) 32) | seed2.hashCode());
    }
    
    public static void setSeed(Class<?> seed) {
        setSeed(seed.getName(), seed.getSimpleName());
    }

    public static void resetSeed() {
        globalRnd._resetSeed();
    }

    private final Random rnd = new Random();
    private long lastSeed;

    public DistributionRandom() {
        this(new Random().nextLong());
    }

    public DistributionRandom(long lastSeed) {
        _setSeed(lastSeed);
    }

    public Random getRandom() {
        return rnd;
    }

    public void _setSeed(long seed) {
        this.lastSeed = seed;
        rnd.setSeed(seed);
    }
    
    public void _resetSeed() {
        rnd.setSeed(lastSeed);
    }
    
    
}
