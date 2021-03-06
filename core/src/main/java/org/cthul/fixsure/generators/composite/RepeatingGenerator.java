package org.cthul.fixsure.generators.composite;

import java.util.Arrays;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import org.cthul.fixsure.Template;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

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
        return new RepeatingGenerator<>(templates);
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

    @Override
    public long randomSeedHint() {
        return GeneratorTools.getRandomSeedHint(current) * 3 ^
                DistributionRandomizer.toSeed(getClass());
    }
    
    @Override
    public StringBuilder toString(StringBuilder sb) {
        if (templates.length == 1) {
            return templates[0].toString(sb).append(".repeat()");
        }
        Object[] all = templates;
        if (current != null) {
            Arrays.copyOf(templates, templates.length, Object[].class);
            int i = (nextIndex + templates.length - 1) % templates.length;
            all[i] = current;
        }
        return GeneratorTools.printList(Arrays.asList(all), sb.append("Repeat(")).append(')');
    }
}
