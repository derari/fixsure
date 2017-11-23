package org.cthul.fixsure.generators.composite;

import java.util.function.Function;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 *
 */
public class MappingGenerator<T> implements CopyableGenerator<T> {
    
    public static <In, Out> MappingGenerator<Out> map(DataSource<In> src, Function<? super In, ? extends Out> function) {
        return new MappingGenerator<>(src, function);
    }

    private final Convert<?, T> cnv;
    private Class<?> valueType = void.class;
    
    public <Src> MappingGenerator(DataSource<Src> src, Function<? super Src, ? extends T> function) {
        this.cnv = new Convert<>(src, function);
    }
    
    protected MappingGenerator(MappingGenerator<T> src) {
        this.cnv = src.cnv.newFromTemplate();
    }

    @Override
    public T next() {
        return cnv.next();
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = cnv.getValueType();
        }
        return (Class) valueType;
    }

    @Override
    public MappingGenerator<T> copy() {
        return new MappingGenerator<>(this);
    }

    @Override
    public long randomSeedHint() {
        return GeneratorTools.getRandomSeedHint(cnv.src) * 3 ^ 
                DistributionRandomizer.toSeed(MappingGenerator.class);
    }
    
    private static class Convert<Src, Out> {
        private final Generator<Src> src;
        private final Function<? super Src, ? extends Out> function;
        public Convert(DataSource<Src> src, Function<? super Src, ? extends Out> function) {
            this.src = src.toGenerator();
            this.function = function;
        }
        public Out next() {
            return function.apply(src.next());
        }
        public Convert<Src, Out> newFromTemplate() {
            return new Convert<>(copyGenerator(src), function);
        }
        public Class<?> getValueType() {
            return Typed.typeOf(function);
        }
    }
    
}
