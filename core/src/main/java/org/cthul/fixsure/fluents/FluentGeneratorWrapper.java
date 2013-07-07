package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorBase;
import org.hamcrest.Factory;

/**
 * Wraps a {@link Generator} to implement the {@linkplain FlGenerator fluent interface}
 * @param <T> valuet type
 */
public class FluentGeneratorWrapper<T> extends GeneratorBase<T> {
    
    @Factory
    public static <T> FlGenerator<T> fluent(Generator<T> generator) {
        if (generator instanceof FlGenerator) {
            return (FlGenerator<T>) generator;
        }
        return new FluentGeneratorWrapper<>(generator);
    }

    private final Generator<T> g;

    public FluentGeneratorWrapper(Generator<T> g) {
        this.g = g;
    }

    @Override
    public T next() {
        return g.next();
    }    

    @Override
    protected Generator<T> publishThis() {
        return g;
    }
    
}
