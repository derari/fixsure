package org.cthul.fixsure.generators.composite;

import java.util.Arrays;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
public class RoundRobinGenerator<T> implements CopyableGenerator<T> {
    
    public static <T> RoundRobinGenerator<T> rotate(DataSource<? extends T>... generators) {
        return new RoundRobinGenerator<>(generators);
    }
    
    public static <T> RoundRobinGenerator<T> alternate(DataSource<? extends T>... generators) {
        return new RoundRobinGenerator<>(generators);
    }
    
    private final Generator<? extends T>[] generators;
    private Class<?> valueType = void.class;
    private int n = 0;

    public RoundRobinGenerator(DataSource<? extends T>[] generators) {
        this.generators = DataSource.toGenerators((DataSource[]) generators);
    }

    public RoundRobinGenerator(Class<T> valueType, DataSource<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public RoundRobinGenerator(RoundRobinGenerator<T> src) {
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = GeneratorTools.copyGenerator(this.generators[i]);
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
    public RoundRobinGenerator<T> copy() {
        return new RoundRobinGenerator<>(this);
    }

    @Override
    public long randomSeedHint() {
        long seed = DistributionRandomizer.toSeed(getClass());
        for (Generator<?> g: generators) {
            seed ^= GeneratorTools.getRandomSeedHint(g);
        }
        return seed;
    }
    
    @Override
    public StringBuilder toString(StringBuilder sb) {
        return GeneratorTools.printList(Arrays.asList(generators), sb.append("Alternate(")).append(')');
    }
}
