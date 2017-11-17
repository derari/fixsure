package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Sequence;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 * Randomly selects elements from a sequence.
 */
public class RandomizedSequenceGenerator<T> 
                extends GeneratorWithDistribution<T>
                implements CopyableGenerator<T> {
    
    private static final long CLASS_SEED = toSeed(RandomizedSequenceGenerator.class);
    
    public static <T> RandomizedSequenceGenerator<T> random(Sequence<T> seq) {
        return new RandomizedSequenceGenerator<>(seq);
    }
    
    private final Sequence<T> source;
    private final long l;

    public RandomizedSequenceGenerator(Sequence<T> source) {
        this(source, null);
    }

    public RandomizedSequenceGenerator(Sequence<T> source, Distribution distribution) {
        this(source, distribution, CLASS_SEED ^ source.length());
    }

    public RandomizedSequenceGenerator(Sequence<T> source, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        this.source = source;
        this.l = maxLen(source);
    }

    protected RandomizedSequenceGenerator(RandomizedSequenceGenerator src) {
        super(src);
        this.source = src.source;
        this.l = src.l;
    }

    @Override
    public T next() {
        return source.value(rnd().nextLong(l));
    }
    
    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }

    @Override
    public RandomizedSequenceGenerator<T> copy() {
        return new RandomizedSequenceGenerator<>(this);
    }
    
    private static long maxLen(Sequence<?> source) {
        return source.isUnbounded() ? Long.MAX_VALUE : source.length();
    }
}
