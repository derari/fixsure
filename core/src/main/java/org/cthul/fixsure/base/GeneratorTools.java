package org.cthul.fixsure.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.generators.value.ConstantGenerator;
import org.cthul.objects.Types;

/**
 *
 */
public class GeneratorTools {

    public static <T> Class<T> typeOf(Object o) {
        if (o instanceof Typed) {
            return ((Typed<T>) o).getValueType();
        }
        return null;
    }
    
    public static <T> Class<T> commonTypeOf(Object... generators) {
        List<Class<?>> types = collectTypesOf(generators);
        if (types.isEmpty()) return null;
        types = Types.lowestCommonSuperClasses(types);
        if (types.size() == 1) {
            return (Class) types.get(0);
        }
        return null;
    }
    
    private static List<Class<?>> collectTypesOf(Object[] generators) {
        final List<Class<?>> result = new ArrayList<>(generators.length);
        for (Object g: generators) {
            Class<?> t = typeOf(g);
            if (t != null) result.add(t);
        }
        return result;
    }
    
    public static Class[] typesOf(Object[] generators) {
        final Class[] result = new Class[generators.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = typeOf(generators[i]);
        }
        return result;
    }
    
    public static List<Class> typelistOf(Object[] generators) {
        return Arrays.asList(typesOf(generators));
    }
    
    public static Generator[] valuesAsGenerators(Object... values) {
        final Generator[] result = new Generator[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = valueAsGenerator(values[i]);
        }
        return result;
    }
    
    public static <T> Generator<T> valueAsGenerator(Object value) {
        if (value instanceof Generator) {
            return (Generator) value;
        }
        if (value instanceof GeneratorTemplate) {
            return ((GeneratorTemplate) value).newGenerator();
        }
        return (Generator) ConstantGenerator.constant(value);
    }
    
    public static <T> GeneratorTemplate<T> asGeneratorTemplate(Object template) {
        if (template instanceof GeneratorTemplate) {
            return (GeneratorTemplate) template;
        }
        throw new UnsupportedOperationException(
                "Not a generator template: " + template);
    }
    
    public static <T> Generator<T> newGeneratorFromTemplate(Object template) {
        GeneratorTemplate<T> t = asGeneratorTemplate(template);
        return t.newGenerator();
    }
    
}
