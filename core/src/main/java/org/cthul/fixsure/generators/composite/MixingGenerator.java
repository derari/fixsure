package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.base.GeneratorWithScalar;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;
import org.cthul.fixsure.generators.primitive.IntegersGenerator;
import org.hamcrest.Factory;

/**
 * Produces values from randomly selected generators.
 */
public class MixingGenerator<T>
                extends GeneratorWithScalar<T>
                implements FlGeneratorTemplate<T> {
    
    @Factory
    public static <T> MixingGenerator<T> mix(Generator<? extends T>... generators) {
        return new MixingGenerator<>(generators);
    }
    
    private final Generator<? extends T>[] generators;
    private Class<?> valueType = void.class;

    public MixingGenerator(Generator<? extends T>[] generators) {
        super(IntegersGenerator.integers(generators.length));
        this.generators = generators;
    }

    public MixingGenerator(Class<T> valueType, Generator<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public MixingGenerator(MixingGenerator<T> src) {
        super(src);
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = GeneratorTools.newGeneratorFromTemplate(this.generators[i]);
        }
        this.valueType = src.valueType;
    }

    @Override
    public T next() {
        return generators[nextScalar()].next();
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) generators);
        }
        return (Class) valueType;
    }

    @Override
    public MixingGenerator<T> newGenerator() {
        return new MixingGenerator<>(this);
    }
    
}
