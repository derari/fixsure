package org.cthul.fixsure.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cthul.fixsure.*;
import org.cthul.fixsure.generators.value.ConstantValue;
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
        types = Types.lowestCommonSuperclasses(types);
        if (types.size() == 1) {
            return (Class) types.get(0);
        }
        return null;
    }
    
    private static List<Class<?>> collectTypesOf(Object... generators) {
        final List<Class<?>> result = new ArrayList<>(generators.length);
        for (Object g: generators) {
            Class<?> t = typeOf(g);
            if (t != null) result.add(t);
        }
        return result;
    }
    
    public static Class[] typesOf(Object... generators) {
        final Class[] result = new Class[generators.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = typeOf(generators[i]);
        }
        return result;
    }
    
    public static List<Class> typelistOf(Object... generators) {
        return Arrays.asList(typesOf(generators));
    }
    
    public static Generator[] asGenerators(Object... values) {
        final Generator[] result = new Generator[values.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = asGenerator(values[i]);
        }
        return result;
    }
    
    public static <T> Generator<T> asGenerator(Object value) {
        if (value instanceof DataSource) {
            return ((DataSource) value).toGenerator();
        }
        return (Generator) ConstantValue.constant(value);
    }
    
    public static <T> Generator<T> copyGenerator(Object generator) {
        if (generator instanceof CopyableGenerator) {
            return ((CopyableGenerator) generator).asTemplate().newGenerator();
        }
        if (generator instanceof Template) {
            return ((Template) generator).newGenerator();
        }
        throw new UnsupportedOperationException(
                "Not copyable: " + generator);
    }    
}
