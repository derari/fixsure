package org.cthul.fixsure.generators.composite;

import java.util.Arrays;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;
import org.cthul.fixsure.generators.GeneratorWithScalar;
import org.cthul.fixsure.generators.primitives.RandomIntegersGenerator;

/**
 * Produces values from randomly selected generators.
 */
public class MixingGenerator<T>
                extends GeneratorWithScalar<T>
                implements CopyableGenerator<T> {
    
    public static <T> MixingGenerator<T> mix(DataSource<? extends T>... sources) {
        return new MixingGenerator<>(sources);
    }
    
    private final Generator<? extends T>[] generators;
    private Class<?> valueType = void.class;

    public MixingGenerator(DataSource<? extends T>[] sources) {
        super(RandomIntegersGenerator.integers(sources.length));
        this.generators = DataSource.toGenerators((DataSource[]) sources);
    }

    public MixingGenerator(Class<T> valueType, Generator<? extends T>[] generators) {
        this(generators);
        this.valueType = valueType;
    }

    public MixingGenerator(MixingGenerator<T> src) {
        super(src);
        this.generators = src.generators.clone();
        for (int i = 0; i < this.generators.length; i++) {
            this.generators[i] = copyGenerator(this.generators[i]);
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
    public MixingGenerator<T> copy() {
        return new MixingGenerator<>(this);
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
        super.toString(sb.append("Mix(")).append(':');
        return GeneratorTools.printList(Arrays.asList(generators), sb).append(')');
    }
}
