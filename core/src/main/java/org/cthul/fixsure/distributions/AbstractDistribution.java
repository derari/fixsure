package org.cthul.fixsure.distributions;

import java.util.function.LongSupplier;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.fluents.FlDistribution;

/**
 * Base class for implementations of {@link Distribution}.
 */
public abstract class AbstractDistribution implements FlDistribution {
    
    private final LongSupplier seedSupplier;

    public AbstractDistribution(LongSupplier seedSupplier) {
        this.seedSupplier = seedSupplier;
    }

    public AbstractDistribution(long seed) {
        this.seedSupplier = () -> seed;
    }

    public AbstractDistribution(long seed, LongSupplier seedSupplier) {
        this.seedSupplier = () -> seed ^ seedSupplier.getAsLong();
    }

    public AbstractDistribution(AbstractDistribution source) {
        this.seedSupplier = source.seedSupplier;
    }
    
    protected long getSeed() {
        return seedSupplier.getAsLong();
    }

    @Override
    public FlRandom toRandomNumbers(long seedHint) {
        return newRandom(seedHint ^ getSeed());
    }
    
    protected abstract FlRandom newRandom(long seed);
}