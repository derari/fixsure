package org.cthul.fixsure.generators.composite;

import java.util.function.Function;
import org.cthul.fixsure.*;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 *
 */
public class FlatMappingGenerator<T> implements CopyableGenerator<T> {
    
    public static <In, Out> FlatMappingGenerator<Out> map(DataSource<In> src, Function<? super In, ? extends DataSource<Out>> function) {
        return new FlatMappingGenerator<>(src, function);
    }

    private final Convert<?, T> cnv;
    private Class<?> valueType = void.class;
    
    public <Src> FlatMappingGenerator(DataSource<Src> src, Function<? super Src, ? extends DataSource<T>> function) {
        this.cnv = new Convert<>(src, function);
    }
    
    protected FlatMappingGenerator(FlatMappingGenerator<T> src) {
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
    public FlatMappingGenerator<T> copy() {
        return new FlatMappingGenerator<>(this);
    }

    @Override
    public long randomSeedHint() {
        return GeneratorTools.getRandomSeedHint(cnv.src) * 3 ^ 
                DistributionRandomizer.toSeed(FlatMappingGenerator.class);
    }
    
    private static class Convert<Src, Out> {
        private final Generator<Src> src;
        private final Function<? super Src, ? extends DataSource<Out>> function;
        private Generator<Out> current = null;
        public Convert(DataSource<Src> src, Function<? super Src, ? extends DataSource<Out>> function) {
            this.src = src.toGenerator();
            this.function = function;
        }
        public Out next() {
            while (true) {
                if (current == null) {
                    current = function.apply(src.next()).toGenerator();
                }
                try {
                    return current.next();
                } catch (GeneratorException e) {
                    current = null;
                }
            }
        }
        public Convert<Src, Out> newFromTemplate() {
            return new Convert<>(copyGenerator(src), function);
        }
        public Class<?> getValueType() {
            if (current == null) {
                current = function.apply(src.next()).toGenerator();
            }
            return Typed.typeOf(current);
        }
    }
    
}
