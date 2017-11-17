package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.fluents.FlFetcher;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.values.LazyValues;

/**
 *
 */
public class LazyFetcher extends FetcherWithScalar {
        
    public static <T> T any(Generator<T> generator) {
        return generator.next();
    }
    
    public static <T> LazyValues<T> unbound(DataSource<T> generator) {
        return LazyValues.unbound(generator);
    }
//    
////    public static <T> LazyValues<T> one(Generator<T> generator) {
//        return Fetchers.one().of(generator);
//    }
//    
////    public static <T> LazyValues<T> two(Generator<T> generator) {
//        return Fetchers.two().of(generator);
//    }
//    
////    public static <T> LazyValues<T> three(Generator<T> generator) {
//        return Fetchers.three().of(generator);
//    }
//    
////    public static <T> LazyValues<T> few(Generator<T> generator) {
//        return Fetchers.few().of(generator);
//    }
//    
////    public static <T> LazyValues<T> some(Generator<T> generator) {
//        return Fetchers.some().of(generator);
//    }
//    
////    public static <T> LazyValues<T> several(Generator<T> generator) {
//        return Fetchers.several().of(generator);
//    }
//    
////    public static <T> LazyValues<T> many(Generator<T> generator) {
//        return Fetchers.many().of(generator);
//    }
    
    public static LazyFetcher get(int length) {
        return new LazyFetcher(length);
    }
    
    public static LazyFetcher get(int min, int max) {
        return new LazyFetcher(min, max);
    }
    
    public LazyFetcher(int length) {
        super(length);
    }

    public LazyFetcher(DataSource<Integer> lengthGenerator) {
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
    public <T> LazyValues<T> of(DataSource<T> generator) {
        return (LazyValues<T>) super.of(generator);
    }

    @Override
    public <T> LazyValues<T> ofEach(DataSource<? extends T>... generators) {
        return (LazyValues<T>) super.<T>ofEach(generators);
    }

    @Override
    protected <T> CombinableValues<T> newValues(int n, DataSource<? extends T> g) {
        return new LazyCombinedValues<>(g, n);
    }
    
    protected static class LazyCombinedValues<T> 
                    extends LazyValues<T>
                    implements CombinableValues<T> {
        public LazyCombinedValues(DataSource<? extends T> values, int n) {
            super(values, n);
        }
        @Override
        public void __addMore(int n, DataSource<? extends T> g) {
            _add(g, n);
        }
        @Override
        public FlValues<T> __asValues() {
            return this;
        }
    }    
    
    public static interface Template extends FlFetcher.Template {
        @Override
        LazyFetcher toItemConsumer();
    }
}
