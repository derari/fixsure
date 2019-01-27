package org.cthul.fixsure.factory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public class DefaultFactories implements Factories, FactoryParent {
    
    @org.cthul.fixsure.api.Factory
    public static FactoriesSetup newFactoriesSetup() {
        return new Setup();
    }

    private final Map<String, DataSource<?>> sources = new HashMap<>();
    private final Map<String, Factory<?>> factories = new HashMap<>();
    private final Map<String, FlGenerator<?>> generators = new HashMap<>();

    public DefaultFactories() {
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> FlGenerator<T> generator(String key) {
        FlGenerator<T> gen = (FlGenerator) generators.computeIfAbsent(key, this::newGenerator);
        if (gen != null) return gen;
        throw new IllegalArgumentException(key);
    }
    
    protected FlGenerator<?> newGenerator(String key) {
        DataSource<?> src = sources.get(key);
        if (src == null) return null;
        return src.toGenerator().fluentData();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Factory<T> factory(String key) {
        Factory<T> fac = (Factory) factories.get(key);
        if (fac != null) return fac;
        throw new IllegalArgumentException(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ValueGenerator<T> peekGenerator(String key) {
        Factory<T> factory = (Factory) factories.get(key);
        if (factory != null) return factory.generate().asValueGenerator();
        FlGenerator<T> gen = (FlGenerator) generators.computeIfAbsent(key, this::newGenerator);
        if (gen != null) return ValueGenerator.fromGenerator(gen);
        return null;
    }

    @Override
    public <T> ValueSource<T> peekSource(String key, boolean useDefault) {
        if (!useDefault) return null;
        if (peekGenerator(key) == null) return null;
        return f -> peekGenerator(key);
    }

    @Override
    public Map<String, String> getDescriptionForChild() {
        return new HashMap<>();
    }

    @Override
    public void reset() {
        generators.clear();
        factories.values().forEach(Factory::reset);
    }
    
    public static class Setup implements FactoriesSetup {

        private final DefaultFactories factories;

        public Setup() {
            factories = new DefaultFactories();
        }
        
        @Override
        public FactoriesSetup add(String key, DataSource<?> dataSource) {
            factories.sources.put(key, dataSource);
            return this;
        }

        @Override
        public FactoriesSetup add(String key, Factory<?> factory) {
            factories.sources.put(key, factory);
            factories.factories.put(key, factory);
            return this;
        }

        @Override
        public <T> NewFactory<T> newFactory(String key, Class<T> clazz) {
            return new DefaultNewFactory<>(this, key, clazz);
        }

        @Override
        public DefaultFactories toFactories() {
            return factories;
        }
    }

    private static final AtomicLong UNIQUE = new AtomicLong(0);
    
    static long uniqueId() {
        return UNIQUE.getAndIncrement();
    }
    
    static String uniqueIdStr() {
        return Long.toHexString(uniqueId());
    }
    
    static String anonymousKey() {
        return "<anonymous>@" + uniqueIdStr();
    }
    
    protected static <T> BiConsumer<Object,T> defaultSetter(Class<?> clazz, String key) {
        for (Field f: clazz.getDeclaredFields()) {
            if (f.getName().equals(key)) {
                return fieldSetter(f);
            }
        }
        Class<?> sup = clazz.getSuperclass();
        if (sup != null) {
            return defaultSetter(sup, key);
        }
        throw new IllegalArgumentException(key);
    }
    
//    public static class DefaultSetterSource<T> implements BiFunction<T, T, T> {
//        
//        private final Class<?> clazz;
//        private final String key;
//        private final ValueSetupBase setup;
//
//        public DefaultSetterSource(Class<?> clazz, String key, ValueSetupBase setup) {
//            this.clazz = clazz;
//            this.key = key;
//            this.setup = setup;
//        }
//
//        @Override
//        public Factory.ValueGenerator<? extends T> generate(Factories factories) {
//            FactoriesSetup.FactorySetup s;
//            s.ap
//        }
//    }
    
    private static <T> BiConsumer<Object,T> fieldSetter(Field f) {
        return (object, value) -> {
            try {
                f.setAccessible(true);
                f.set(object, value);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
