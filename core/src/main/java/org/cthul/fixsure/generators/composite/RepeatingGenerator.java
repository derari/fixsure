package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorBase;
import org.cthul.fixsure.base.GeneratorTools;
import org.hamcrest.Factory;

/**
 * Converts a list of finite generators into an inifinte generator.
 */
public class RepeatingGenerator<T> 
                extends GeneratorBase<T> 
                implements GeneratorTemplate<T> {
    
    @Factory
    public static <T> RepeatingGenerator<T> repeat(GeneratorTemplate<? extends T>... templates) {
        return new RepeatingGenerator<>(templates);
    }
    
    private final GeneratorTemplate<? extends T>[] templates;
    private int nextIndex = 0;
    private Generator<? extends T> current = null;
    private Class<?> valueType = void.class;

    public RepeatingGenerator(GeneratorTemplate<? extends T>[] templates) {
        this.templates = templates.clone();
    }

    public RepeatingGenerator(RepeatingGenerator<T> src) {
        this(src.templates);
        this.nextIndex = src.nextIndex;
        if (src.current != null) {
            this.current = GeneratorTools.newGeneratorFromTemplate(src.current);
        }
    }

    @Override
    public T next() {
        if (current != null) {
            try {
                return current.next();
            } catch (GeneratorException e) { }
        }
        GeneratorException lastException = null;
        for (int i = 0; i < templates.length; i++) {
            current = templates[nextIndex].newGenerator();
            nextIndex = (nextIndex + 1) % templates.length;
            try {
                return current.next();
            } catch (GeneratorException e) {
                lastException = e;
            }
        }
        throw new GeneratorException("No generator provided items", lastException);
    }

    @Override
    public RepeatingGenerator<T> newGenerator() {
        return new RepeatingGenerator<>(this);
    }

    @Override
    public Class<T> getValueType() {
        if (valueType == void.class) {
            valueType = GeneratorTools.commonTypeOf((Object[]) templates);
        }
        return (Class) valueType;
    }
    
}
