package org.cthul.fixsure.distributions;

import java.util.Random;
import java.util.function.LongSupplier;

public final class DistributionRandomizer {
    
    private static final DistributionRandomizer GLOBAL = new DistributionRandomizer(toSeed(DistributionRandomizer.class));

    public static DistributionRandomizer getGlobal() {
        return GLOBAL;
    }
    
    public static void setSeed(long seed) {
        GLOBAL._setSeed(seed);
    }
    
    public static void setSeed(String seed) {
        setSeed("", seed);
    }
    
    public static void setSeed(String seed1, String seed2) {
        setSeed(toSeed(seed1, seed2));
    }
    
    public static void setSeed(Class<?> seed) {
        setSeed(toSeed(seed));
    }
    
    public static long toSeed(String seed1, String seed2) {
        long hc1 = seed1.hashCode();
        return (hc1 << 32) | seed2.hashCode();
    }
    
    public static long toSeed(Class<?> seed) {
        return toSeed(seed.getName(), seed.getSimpleName());
    }

    private long lastSeed;
    private final LongSupplier seedSupplier = () -> lastSeed;

    public DistributionRandomizer() {
        this(new Random().nextLong());
    }

    public DistributionRandomizer(long lastSeed) {
        _setSeed(lastSeed);
    }

    public void _setSeed(long seed) {
        this.lastSeed = seed;
    }

    public LongSupplier getSeedSupplier() {
        return seedSupplier;
    }
}
