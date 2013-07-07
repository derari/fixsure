package org.cthul.fixsure.base;

import org.cthul.fixsure.Converter;
import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.Values;
import org.cthul.fixsure.fluents.FlFetcher;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlValues;
import org.cthul.fixsure.generators.composite.ConvertingGenerator;
import org.cthul.fixsure.generators.composite.MergingGenerator;
import org.cthul.fixsure.generators.composite.MixingGenerator;
import org.cthul.fixsure.generators.composite.RepeatingGenerator;
import org.cthul.fixsure.generators.composite.RoundRobinGenerator;
import org.cthul.fixsure.generators.reflection.InvocationGenerator;
import org.cthul.fixsure.iterables.EagerValues;
import org.cthul.fixsure.iterables.LazyValues;

/**
 * Base class for {@link Generator}s.
 */
public abstract class GeneratorBase<T> implements FlGenerator<T>{
    
    /** {@inheritDoc} */
    @Override
    public abstract T next();

    /** {@inheritDoc} */
    @Override
    public Class<T> getValueType() {
        return null;
    }
    
    protected Generator<T> publishThis() {
        return this;
    }

    protected GeneratorTemplate<T> publishThisTemplate() {
        return GeneratorTools.asGeneratorTemplate(publishThis());
    }
    
//    @Override
    /**
     * Will cause compilation error when get(int) is defined in FlGenerator.
     * @see FlGenerator
     */
    private void get(int length) {
        throw new UnsupportedOperationException("should not exist");
    }

    /** {@inheritDoc} */
    @Override
    public LazyValues<T> get(Generator<Integer> length) {
        int len = length.next();
        return LazyValues.get(len, publishThis());
    }

    @Override
    public Values<T> get(Fetcher fetcher) {
        return fetcher.of(this);
    }

    @Override
    public FlValues<T> get(FlFetcher fetcher) {
        return fetcher.of(this);
    }

    /** {@inheritDoc} */
    @Override
    public LazyValues<T> unbound() {
        return LazyValues.unbound(publishThis());
    }

    /** {@inheritDoc} */
    @Override
    public EagerValues<T> next(int length) {
        return EagerValues.next(length, publishThis());
    }

    /** {@inheritDoc} */
    @Override
    public EagerValues<T> next(Generator<Integer> length) {
        return next(length.next());
    }

    @Override
    public EagerValues<T> all() {
        return EagerValues.all(this);
    }

    /** {@inheritDoc} */
    @Override
    public <T2> ConvertingGenerator<T2> each(Converter<? super T, T2> converter) {
        return ConvertingGenerator.convert(publishThis(), converter);
    }

    @Override
    public <T2> MergingGenerator<T2> mergeWith(Generator<? extends T2>... generators) {
        return MergingGenerator.merge(thisWith(generators));
    }
    
    @Override
    public <T2> MixingGenerator<T2> mixWith(Generator<? extends T2>... generators) {
        return MixingGenerator.mix(thisWith(generators));
    }

    @Override
    public <T2> RoundRobinGenerator<T2> alternateWith(Generator<? extends T2>... generators) {
        return RoundRobinGenerator.alternate(thisWith(generators));
    }

    protected Generator[] thisWith(Generator[] generators) {
        Generator[] all = new Generator[generators.length+1];
        all[0] = publishThis();
        System.arraycopy(generators, 0, all, 1, generators.length);
        return all;
    }
    
    @Override
    public RepeatingGenerator<T> repeat() {
        return RepeatingGenerator.repeat(publishThisTemplate());
    }

    @Override
    public <T2> InvocationGenerator<T2> invoke(String m) {
        return InvocationGenerator.invoke(this, m);
    }

    @Override
    public <T2> InvocationGenerator<T2> invoke(String m, Object... args) {
        return InvocationGenerator.invoke(this, m, args);
    }

}
