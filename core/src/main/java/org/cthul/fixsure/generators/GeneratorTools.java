package org.cthul.fixsure.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.cthul.fixsure.*;
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
