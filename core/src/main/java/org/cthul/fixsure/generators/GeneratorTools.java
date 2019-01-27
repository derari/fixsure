package org.cthul.fixsure.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.cthul.fixsure.*;
import org.cthul.fixsure.api.Stringify;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.fetchers.EagerFetcher;
import org.cthul.fixsure.fetchers.Fetchers;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlSequence;
import org.cthul.fixsure.generators.value.ConstantValue;
import org.cthul.objects.Types;

/**
 *
 */
public class GeneratorTools {

    private GeneratorTools() {
    }

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
            return ((CopyableGenerator) generator).copy();
        }
        if (generator instanceof Template) {
            return ((Template) generator).newGenerator();
        }
        throw new UnsupportedOperationException(
                "Not copyable: " + generator);
    }
    
    public static StringBuilder lambdaToString(Object o, StringBuilder sb) {
        if (o == null) {
            return sb.append("null");
        }
        String str = o.getClass().getName();
        if (str.contains("$$Lambda$")) {
            int slash = str.indexOf('/');
            if (slash < 0) slash = str.length();
            int dot = str.lastIndexOf('.', slash) + 1;
            if (dot < 0) dot = 0;
            return sb.append(str, dot, slash);
        } else {
            return Stringify.toString(o, sb);
        }
    }
    
    public static StringBuilder toAscii(long value, StringBuilder sb) {
        char[] values = new char[10];
        long bit1 = value & 1;
        value >>>= 1;
        values[9] = (char) ('!' + 2 * (value % 47) + bit1);
        value /= 47;
        for (int i = 0; i < 9; i++) {
            values[8-i] = (char) ('!' + value % 94);
            value /= 94;
        }
        sb.append(values);
        return sb;
    }
    
    public static StringBuilder printList(Object first, Collection<?> list, StringBuilder sb) {
        Stringify.toString(first, sb);
        if (list == null || list.isEmpty()) return sb;
        return printList(list, sb.append(','), 1);
    }
    
    public static StringBuilder printList(Collection<?> list, StringBuilder sb) {
        return printList(list, sb, 2);
    }
    
    public static StringBuilder printList(Collection<?> list, StringBuilder sb, int max) {
        int n = -1;
        Iterator<?> it = list.iterator();
        while (++n < max && it.hasNext()) {
            if (n > 0) sb.append(',');
            Stringify.toString(it.next(), sb);
        }
        int len = sb.length();
        while (it.hasNext()) {
            sb.append(',');
            Stringify.toString(it.next(), sb);
            if (sb.length() - len > 12) {
                sb.setLength(len);
                return sb.append(",").append(list.size()-max).append(" more...");
            }
        }
        return sb;
    }
    
    public static synchronized Consumers cacheConsumers(FlGenerator<?> generator) {
        return CONSUMERS.computeIfAbsent(generator, k -> new Consumers(k.randomSeedHint()));
    }
    
    public static long getRandomSeedHint(Generator<?> generator) {
        if (generator instanceof FlGenerator) {
            return ((FlGenerator<?>) generator).randomSeedHint();
        }
        return GENERATOR_SEED;
    }
    
    public static long getRandomSeedHint(Sequence<?> sequence) {
        if (sequence instanceof FlSequence) {
            return ((FlSequence<?>) sequence).randomSeedHint();
        }
        return SEQUENCE_SEED ^ sequence.length();
    }
    
    private static final long GENERATOR_SEED = toSeed(GeneratorTools.class) ^ toSeed(Generator.class);
    private static final long SEQUENCE_SEED = toSeed(GeneratorTools.class) ^ toSeed(Sequence.class);
    private static final Map<FlGenerator<?>, Consumers> CONSUMERS = new WeakHashMap<>();
    
    public static class Consumers {
        
        private final long seed;
        private EagerFetcher few = null;
        private EagerFetcher some = null;
        private EagerFetcher several = null;
        private EagerFetcher many = null;

        public Consumers(long seed) {
            this.seed = seed;
        }

        public EagerFetcher getFew() {
            if (few == null) {
                few = Fetchers.few(seed).toFetcher();
            }
            return few;
        }

        public EagerFetcher getSome() {
            if (some == null) {
                some = Fetchers.some(seed).toFetcher();
            }
            return some;
        }

        public EagerFetcher getSeveral() {
            if (several == null) {
                several = Fetchers.several(seed).toFetcher();
            }
            return several;
        }

        public EagerFetcher getMany() {
            if (many == null) {
                many = Fetchers.many(seed).toFetcher();
            }
            return many;
        }
    }
}
