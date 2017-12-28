package org.cthul.fixsure.distributions;

import java.util.function.LongSupplier;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.fluents.FlDistribution;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 * Base class for implementations of {@link Distribution}.
 */
public abstract class AbstractDistribution extends AbstractStringify implements FlDistribution.Template {
    
    private final LongSupplier seedSupplier;
    private final String seedStr;

    public AbstractDistribution(LongSupplier seedSupplier) {
        this.seedSupplier = seedSupplier;
        this.seedStr = null;
    }

    public AbstractDistribution(long seed) {
        this.seedSupplier = () -> seed;
        this.seedStr = GeneratorTools.toAscii(seed, new StringBuilder()).toString();
    }

    public AbstractDistribution(long seed, LongSupplier seedSupplier) {
        this.seedSupplier = () -> seed ^ seedSupplier.getAsLong();
        StringBuilder sb = new StringBuilder();
        GeneratorTools.lambdaToString(seedSupplier, sb);
        sb.append('^');
        GeneratorTools.toAscii(seed, sb);
        this.seedStr = sb.toString();
    }

    public AbstractDistribution(AbstractDistribution source) {
        this.seedSupplier = source.seedSupplier;
        this.seedStr = source.seedStr;
    }
    
    protected long getSeed() {
        return seedSupplier.getAsLong();
    }

    @Override
    public FlRandom toRandomNumbers(long seedHint) {
        return newRandom(seedHint ^ getSeed());
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(seedStr);
    }
    
    protected abstract FlRandom newRandom(long seed);
}