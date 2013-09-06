package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.hamcrest.Factory;

/**
 *
 */
public class RoundRobinGenerator<T>
                extends GeneratorBase<T> 
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> RoundRobinGenerator<T> rotate(Generator<? extends T>... generators) {
        return new RoundRobinGenerator<>(generators);
    }
    
    @Factory
    public static <T> RoundRobinGenerator<T> alternate(Generator<? extends T>... generators) {
        return new RoundRobinGenerator<>(generators);
    }
    
    private final Generator<? extends T>[] generators;
    private Class<?> valueType = void.class;
    private int n = 0;

    public RoundRobinGenerator(Generator<? extends T>[] generators) {
        this.generators = generators;
    }

    public RoundRobinGenerator(Class<T> valueType, Generator<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public RoundRobinGenerator(RoundRobinGenerator<T> src) {
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = GeneratorTools.newGeneratorFromTemplate(this.generators[i]);
        }
        this.valueType = src.valueType;
        this.n = src.n;
    }

    @Override
    public T next() {
        return generators[n++].next();
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) generators);
        }
        return (Class) valueType;
    }

    @Override
    public RoundRobinGenerator<T> newGenerator() {
        return new RoundRobinGenerator<>(this);
    }
    
}
