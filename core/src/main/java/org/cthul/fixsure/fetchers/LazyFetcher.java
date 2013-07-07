package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.FetcherWithScalar;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.iterables.LazyValues;
import org.hamcrest.Factory;

/**
 *
 */
public class LazyFetcher extends FetcherWithScalar {

    private static class Instances {
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
    public static <T> T any(Generator<T> generator) {
        return generator.next();
    }
    
    public static <T> LazyValues<T> unbound(Generator<T> generator) {
        return LazyValues.unbound(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> one(Generator<T> generator) {
        return Instances.ONE.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> two(Generator<T> generator) {
        return Instances.TWO.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> three(Generator<T> generator) {
        return Instances.THREE.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> few(Generator<T> generator) {
        return Instances.FEW.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> some(Generator<T> generator) {
        return Instances.SOME.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> several(Generator<T> generator) {
        return Instances.SEVERAL.of(generator);
    }
    
    @Factory
    public static <T> LazyValues<T> many(Generator<T> generator) {
        return Instances.MANY.of(generator);
    }
    
    @Factory
    public static LazyFetcher get(int length) {
        return new LazyFetcher(length);
    }
    
    @Factory
    public static LazyFetcher get(int min, int max) {
        return new LazyFetcher(min, max);
    }
    
    @Factory
    public static LazyFetcher get(Generator<Integer> lengthGenerator) {
        return new LazyFetcher(lengthGenerator);
    }
    
    public LazyFetcher(int length) {
        super(length);
    }

    public LazyFetcher(Generator<Integer> lengthGenerator) {
        super(lengthGenerator);
    }

    public LazyFetcher(int length, Distribution distribution) {
        super(length, distribution);
    }

    public LazyFetcher(int min, int max) {
        super(min, max);
    }

    public LazyFetcher(int min, int max, Distribution distribution) {
        super(min, max, distribution);
    }

    public LazyFetcher(FetcherWithScalar src) {
        super(src);
    }
    
    @Override
    public int nextLength() {
        return nextScalar();
    }

    @Override
    public <T> LazyValues<T> of(Generator<T> generator) {
        return (LazyValues<T>) super.of(generator);
    }

    @Override
    public <T> LazyValues<T> ofEach(Generator<? extends T>... generators) {
        return (LazyValues<T>) super.ofEach(generators);
    }

    @Override
    protected <T> CombinableValues<T> newValues(int n, Generator<? extends T> g) {
        return new LazyCombinedValues<>(g, n);
    }
    
    protected static class LazyCombinedValues<T> 
                    extends LazyValues<T>
                    implements CombinableValues<T> {
        public LazyCombinedValues(Generator<? extends T> values, int n) {
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
