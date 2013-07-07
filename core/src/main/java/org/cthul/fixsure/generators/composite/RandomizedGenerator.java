package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Sequence;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.base.GeneratorWithDistribution;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 *
 */
public class RandomizedGenerator<T> 
                extends GeneratorWithDistribution<T>
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> RandomizedGenerator<T> random(Sequence<T> seq) {
        return new RandomizedGenerator<>(seq);
    }
    
    private final Sequence<T> source;
    private final long l;

    public RandomizedGenerator(Sequence<T> source) {
        this.source = source;
        long len = source.length();
        if (len < 0) len = Long.MAX_VALUE;
        l = len;
    }
    
    protected RandomizedGenerator(RandomizedGenerator<T> src) {
        super(src);
        this.source = src.source;
        this.l = src.l;
    }

    @Override
    public T next() {
        return source.value(distribution.nextLong(l));
    }
    
    @Override
    public Class<T> getValueType() {
        return GeneratorTools.typeOf(source);
    }

    @Override
    public RandomizedGenerator<T> newGenerator() {
        return new RandomizedGenerator<>(this);
    }
    
}
