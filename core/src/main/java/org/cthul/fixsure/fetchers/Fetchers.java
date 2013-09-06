package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.Generator;
import org.hamcrest.Factory;

/**
 *
 */
public class Fetchers {
    
    private static class Instances {
        private static final EagerFetcher ALL =    new EagerFetcher(-1);
        private static final LazyFetcher UNBOUND = new LazyFetcher(-1);
        private static final LazyFetcher ONE =    new LazyFetcher(1);
        private static final LazyFetcher TWO =    new LazyFetcher(2);
        private static final LazyFetcher THREE =  new LazyFetcher(3);
        private static final LazyFetcher FEW =    new LazyFetcher(3, 4);
        private static final LazyFetcher SOME =   new LazyFetcher(5, 7);
        private static final LazyFetcher SEVERAL = new LazyFetcher(8, 16);
        private static final LazyFetcher MANY =   new LazyFetcher(96, 128);
    }
    
    @Factory
    public static EagerFetcher all() {
        return Instances.ALL;
    }
    
    @Factory
    public static EagerFetcher next(int length) {
        return new EagerFetcher(length);
    }
    
    @Factory
    public static EagerFetcher next(int min, int max) {
        return new EagerFetcher(min, max);
    }
    
    @Factory
    public static EagerFetcher next(Generator<Integer> lengthGenerator) {
        return new EagerFetcher(lengthGenerator);
    }
    
    @Factory
    public static EagerFetcher first(int length) {
        return new EagerFetcher(length);
    }
    
    @Factory
    public static EagerFetcher first(int min, int max) {
        return new EagerFetcher(min, max);
    }
    
    @Factory
    public static EagerFetcher first(Generator<Integer> lengthGenerator) {
        return new EagerFetcher(lengthGenerator);
    }
    
    @Factory
    public static LazyFetcher unbound() {
        return Instances.UNBOUND;
    }
    
    @Factory
    public static LazyFetcher one() {
        return Instances.ONE;
    }
    
    @Factory
    public static LazyFetcher two() {
        return Instances.TWO;
    }
    
    @Factory
    public static LazyFetcher three() {
        return Instances.THREE;
    }
    
    @Factory
    public static LazyFetcher few() {
        return Instances.FEW;
    }
    
    @Factory
    public static LazyFetcher some() {
        return Instances.SOME;
    }
    
    @Factory
    public static LazyFetcher several() {
        return Instances.SEVERAL;
    }
    
    @Factory
    public static LazyFetcher many() {
        return Instances.MANY;
    }

    @Factory
    public static LazyFetcher get(Generator<Integer> lengthGenerator) {
        return new LazyFetcher(lengthGenerator);
    }
}
