package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.FetcherWithScalar;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.iterables.EagerValues;
import org.hamcrest.Factory;

/**
 *
 */
public class EagerFetcher extends FetcherWithScalar {

    private static class Instances {
        private static final EagerFetcher ALL =    new EagerFetcher(-1);
    }
    
    private static class MoreInstances {
        private static final EagerFetcher ONE =    new EagerFetcher(1);
        private static final EagerFetcher TWO =    new EagerFetcher(2);
        private static final EagerFetcher THREE =  new EagerFetcher(3);
        private static final EagerFetcher FEW =    new EagerFetcher(3, 4);
        private static final EagerFetcher SOME =   new EagerFetcher(5, 7);
        private static final EagerFetcher SEVERAL = new EagerFetcher(8, 16);
        private static final EagerFetcher MANY =   new EagerFetcher(96, 128);
    }
    
    @Factory
    public static EagerFetcher all() {
        return Instances.ALL;
    }
    
    public static EagerFetcher one() {
        return MoreInstances.ONE;
    }
    
    public static EagerFetcher two() {
        return MoreInstances.TWO;
    }
    
    public static EagerFetcher three() {
        return MoreInstances.THREE;
    }
    
    public static EagerFetcher few() {
        return MoreInstances.FEW;
    }
    
    public static EagerFetcher some() {
        return MoreInstances.SOME;
    }
    
    public static EagerFetcher several() {
        return MoreInstances.SEVERAL;
    }
    
    public static EagerFetcher many() {
        return MoreInstances.MANY;
    }
        
    public static <T> T any(Generator<T> generator) {
        return generator.next();
    }
    
    public static <T> EagerValues<T> all(Generator<T> generator) {
        return EagerValues.all(generator);
    }
    
    public static <T> EagerValues<T> one(Generator<T> generator) {
        return MoreInstances.ONE.of(generator);
    }
    
    public static <T> EagerValues<T> two(Generator<T> generator) {
        return MoreInstances.TWO.of(generator);
    }
    
    public static <T> EagerValues<T> three(Generator<T> generator) {
        return MoreInstances.THREE.of(generator);
    }
    
    public static <T> EagerValues<T> few(Generator<T> generator) {
        return MoreInstances.FEW.of(generator);
    }
    
    public static <T> EagerValues<T> some(Generator<T> generator) {
        return MoreInstances.SOME.of(generator);
    }
    
    public static <T> EagerValues<T> several(Generator<T> generator) {
        return MoreInstances.SEVERAL.of(generator);
    }
    
    public static <T> EagerValues<T> many(Generator<T> generator) {
        return MoreInstances.MANY.of(generator);
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
    
    public EagerFetcher(int length) {
        super(length);
    }

    public EagerFetcher(Generator<Integer> lengthGenerator) {
        super(lengthGenerator);
    }

    public EagerFetcher(int length, Distribution distribution) {
        super(length, distribution);
    }

    public EagerFetcher(int min, int max) {
        super(min, max);
    }

    public EagerFetcher(int min, int max, Distribution distribution) {
        super(min, max, distribution);
    }

    public EagerFetcher(FetcherWithScalar src) {
        super(src);
    }

    @Override
    public int nextLength() {
        return nextScalar();
    }

    @Override
    public <T> EagerValues<T> of(Generator<T> generator) {
        return (EagerValues<T>) super.of(generator);
    }

    @Override
    public <T> EagerValues<T> ofEach(Generator<? extends T>... generators) {
        return (EagerValues<T>) super.ofEach(generators);
    }

    @Override
    protected <T> CombinableValues<T> newValues(int n, Generator<? extends T> g) {
        return new EagerCombinedValues<>(g, n);
    }
    
    protected static class EagerCombinedValues<T> 
                    extends EagerValues<T>
                    implements CombinableValues<T> {
        public EagerCombinedValues(Generator<? extends T> values, int n) {
            super(values, n);
        }
        @Override
        public void __addMore(int n, Generator<? extends T> g) {
            _add(g, n);
        }
        @Override
        public FlValues<T> __asValues() {
            return this;
        }
    }    
}
