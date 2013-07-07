package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Converter;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlGeneratorTemplate;

/**
 *
 */
public class ConvertingGenerator<T> 
                extends GeneratorBase<T>
                implements FlGeneratorTemplate<T> {
    
    public static <In, Out> ConvertingGenerator<Out> convert(Generator<In> src, Converter<? super In, Out> conv) {
        return new ConvertingGenerator<>(src, conv);
    }

    private final Convert<?, T> cnv;
    private Class<?> valueType = void.class;
    
    public <Src> ConvertingGenerator(Generator<Src> src, Converter<? super Src, T> conv) {
        this.cnv = new Convert<>(src, conv);
    }
    
    protected ConvertingGenerator(ConvertingGenerator<T> src) {
        this(src.cnv);
    }
    
    private <Src> ConvertingGenerator(Convert<Src, T> convert) {
        this.cnv = new Convert<>(convert.newFromTemplate(), convert.cnv);
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
    public FlGenerator<T> newGenerator() {
        return new ConvertingGenerator<>(this);
    }
    
    private static class Convert<Src, Out> {
        private final Generator<Src> src;
        private final Converter<? super Src, Out> cnv;
        public Convert(Generator<Src> src, Converter<? super Src, Out> conv) {
            this.src = src;
            this.cnv = conv;
        }
        public Out next() {
            return cnv.convert(src.next());
        }
        public Generator<Src> newFromTemplate() {
            return GeneratorTools.newGeneratorFromTemplate(src);
        }
        public Class<?> getValueType() {
            return GeneratorTools.typeOf(cnv);
        }
    }
    
}
