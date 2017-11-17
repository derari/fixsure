package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.UniformDistribution;

/**
 *
 */
public class Fetchers {
    
    private static final int FEW_LOW = 3;
    private static final int FEW_HIGH = 5;
    private static final int SOME_LOW = 5;
    private static final int SOME_HIGH = 8;
    private static final int SEVERAL_LOW = 8;
    private static final int SEVERAL_HIGH = 16;
    private static final int MANY_LOW = 96;
    private static final int MANY_HIGH = 128;
    
    private static class Instances {
        private static final EagerFetcher ALL =    new EagerFetcher(-1);
        private static final LazyFetcher UNBOUND = new LazyFetcher(-1);
        private static final EagerFetcher ONE =    new EagerFetcher(1);
        private static final EagerFetcher TWO =    new EagerFetcher(2);
        private static final EagerFetcher THREE =  new EagerFetcher(3);
        private static final EagerFetcher.Template FEW =    next(FEW_LOW, FEW_HIGH);
        private static final EagerFetcher.Template SOME =   next(SOME_LOW, SOME_HIGH);
        private static final EagerFetcher.Template SEVERAL = next(SEVERAL_LOW, SEVERAL_HIGH);
        private static final EagerFetcher.Template MANY =   next(MANY_LOW, MANY_HIGH);
    }
    
    public static EagerFetcher all() {
        return Instances.ALL;
    }
    
    public static LazyFetcher cache() {
        return Instances.UNBOUND;
    }
    
    public static EagerFetcher next(int length) {
        return new EagerFetcher(length);
    }
    
    public static EagerFetcher.Template next(int min, int max) {
        return () -> new EagerFetcher(min, max);
    }
    
    public static EagerFetcher.Template next(int min, int max, Distribution distribution) {
        return () -> new EagerFetcher(min, max, distribution);
    }
    
    public static EagerFetcher.Template next(int min, int max, long seed) {
        return next(min, max, UniformDistribution.uniform(seed));
    }
    
    public static EagerFetcher.Template next(DataSource<Integer> lengthGenerator) {
        return () -> new EagerFetcher(lengthGenerator);
    }
    
    public static EagerFetcher next(Generator<Integer> lengthGenerator) {
        return new EagerFetcher(lengthGenerator);
    }
    
    public static EagerFetcher first(int length) {
        return new EagerFetcher(length);
    }
    
    public static EagerFetcher.Template first(int min, int max) {
        return () -> new EagerFetcher(min, max);
    }
    
    public static EagerFetcher.Template first(DataSource<Integer> lengthGenerator) {
        return () -> new EagerFetcher(lengthGenerator);
    }
    
    public static EagerFetcher one() {
        return Instances.ONE;
    }
    
    public static EagerFetcher two() {
        return Instances.TWO;
    }
    
    public static EagerFetcher three() {
        return Instances.THREE;
    }
    
    public static EagerFetcher.Template few() {
        return Instances.FEW;
    }
    
    public static EagerFetcher.Template some() {
        return Instances.SOME;
    }
    
    public static EagerFetcher.Template several() {
        return Instances.SEVERAL;
    }
    
    public static EagerFetcher.Template many() {
        return Instances.MANY;
    }
    
    public static EagerFetcher.Template few(Distribution distribution) {
        return next(FEW_LOW, FEW_HIGH, distribution);
    }
    
    public static EagerFetcher.Template some(Distribution distribution) {
        return next(SOME_LOW, SOME_HIGH, distribution);
    }
    
    public static EagerFetcher.Template several(Distribution distribution) {
        return next(SEVERAL_LOW, SEVERAL_HIGH, distribution);
    }
    
    public static EagerFetcher.Template many(Distribution distribution) {
        return next(MANY_LOW, MANY_HIGH, distribution);
    }
    
    public static EagerFetcher.Template few(long seed) {
        return few(UniformDistribution.uniform(seed));
    }
    
    public static EagerFetcher.Template some(long seed) {
        return some(UniformDistribution.uniform(seed));
    }
    
    public static EagerFetcher.Template several(long seed) {
        return several(UniformDistribution.uniform(seed));
    }
    
    public static EagerFetcher.Template many(long seed) {
        return many(UniformDistribution.uniform(seed));
    }

    public static LazyFetcher any(DataSource<Integer> lengthGenerator) {
        return new LazyFetcher(lengthGenerator);
    }
}
