package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.CopyableGenerator;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;
import org.cthul.fixsure.Template;

/**
 * Converts a list of finite generators into an inifinte generator.
 */
public class RepeatingGenerator<T> implements CopyableGenerator<T> {
    
//    public static <T> RepeatingGenerator<T> repeat(Template<? extends T>... templates) {
//        return new RepeatingGenerator<T>(templates);
//    }
    
    public static <T> RepeatingGenerator<T> repeat(DataSource<? extends T>... sources) {
        Template<? extends T>[] templates = new Template[sources.length];
        for (int i = 0; i < sources.length; i++) {
            templates[i] = sources[i].fluentData().snapshot();
        }
        return new RepeatingGenerator<T>(templates);
    }
    
    private final Template<? extends T>[] templates;
    private int nextIndex = 0;
    private Generator<? extends T> current = null;
    private Class<?> valueType = void.class;

    public RepeatingGenerator(Template<? extends T>[] templates) {
        this.templates = templates.clone();
    }

    protected RepeatingGenerator(RepeatingGenerator<T> src) {
        this(src.templates);
        this.nextIndex = src.nextIndex;
        if (src.current != null) {
            this.current = copyGenerator(src.current);
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
    public RepeatingGenerator<T> copy() {
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
