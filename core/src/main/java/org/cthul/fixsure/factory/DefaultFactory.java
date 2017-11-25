package org.cthul.fixsure.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.*;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.factory.FactoriesSetup.FactorySetup;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public class DefaultFactory<R> extends DefaultBuilderBase<R, FactoriesSetup.FactorySetup<R>>
                implements FactoriesSetup.FactorySetup<R>,
                           Factory<R> {

    @SuppressWarnings("LeakingThisInConstructor")
    public DefaultFactory(DefaultFactories owner, FactoryBase parent, String id, Class<R> clazz, Function<? super ValueMap, ? extends R> newBuilder, Map<String, ValueEntry> valueMap) {
        super(owner, parent, id, clazz, newBuilder, valueMap);
        owner.addFactory(id, this);
    }

    @Override
    public FactoriesSetup factoriesSetup() {
        return getFactories();
    }

    @Override
    public <T> ValueDeclaration<T, ? extends FactorySetup<R>> assign(String key) {
        return addValue(key, new FactoryValueDeclaration<>(key));
    }
    
    @Override
    public R create() {
        return generate().next();
//        return create(new DefaultValueMap(getValueMap(), this));
    }
    
    @Override
    public FactoryGenerator<R> generate() {
        return new Generator();
    }
    
    protected class Generator extends FactoryBase implements FactoryGenerator<R> {

        private final Map<String, Object> values = new HashMap<>();
        private Map<String, ValueEntry> valueMap = getValueMap();
        
        public Generator() {
            super(DefaultFactory.this);
        }
        
        @Override
        public FactoryGenerator<R> set(Object... args) {
            for (int i = 0; i+1 < args.length; i += 2) {
                String key = String.valueOf(args[i]);
                Object val = args[i+1];
//                if (key.contains(".")) {
                    if (val instanceof DataSource) {
                        set(key).to((DataSource) val);
                    } else {
                        set(key).to(val);
                    }
//                } else {
//                    if (val instanceof DataSource) {
//                        putDataSource(key, (DataSource) val);
//                    } else {
//                        values.put(key, val);
//                    }
//                }
            }
            return this;
        }

        @Override
        protected FlGenerator<?> lookupGenerator(String id) {
            ValueEntry ve = valueMap.get(id);
            if (ve != null) {
                String gid = ve.getGeneratorId();
                if (gid != null && !gid.equals(id)) {
                    return generator(gid);
                }
            }
            return super.lookupGenerator(id);
        }

        @Override
        public R next() {
            DefaultValueMap vm = new DefaultValueMap(valueMap, getFactories(), this);
            vm.set(values);
            return create(vm);
        }

        @Override
        public <V> ValueDeclaration<V, FactoryGenerator<R>> set(String key) {
            int dot = key.indexOf('.');
            if (dot > 0) {
                String factory = key.substring(0, dot);
                String subKey = key.substring(dot+1);
                FlGenerator<?> gen = generator(factory);
                if (gen instanceof Factory) {
                    FactoryGenerator<?> fgen = ((Factory) gen).generate();
                    set(factory).toNext(fgen);
                    return setNested(fgen, subKey);
                }
                if (gen instanceof FactoryGenerator) {
                    return setNested((FactoryGenerator) gen, subKey);
                }
                throw new IllegalArgumentException(factory + " is not a factory key");
            }
            return setLocal(key);
        }
            
        protected <V> ValueDeclaration<V, FactoryGenerator<R>> setLocal(String key) {
            class ValueOverride implements ValueEntry, ValueDeclaration<V, FactoryGenerator<R>> {
                private Function<ValueMap, ? extends V> valueFunction = null;
                private String generatorId;
                @Override
                public FactoryGenerator<R> to(Function<ValueMap, ? extends V> valueFunction) {
                    replace();
                    this.valueFunction = valueFunction;
                    return Generator.this;
                }
                @Override
                public FactoryGenerator<R> to(DataSource<? extends V> dataSource) {
                    putDataSource(key, dataSource);
                    this.generatorId = key;
                    return to(vm -> vm.next(key));
//                    replace();
//                    return Generator.this;
                }
                @Override
                public FactoryGenerator<R> to(V value) {
                    values.put(key, value);
                    return Generator.this;
                }
                @Override
                public Object get(ValueMap valueMap) {
                    return valueFunction.apply(valueMap);
                }
                @Override
                public String getGeneratorId() {
                    return generatorId;
                }
                @Override
                public String toString() {
                    return "!!" + key;
                }
                private void replace() {
                    if (valueMap == getValueMap()) {
                        valueMap = new HashMap<>(valueMap);
                    }
                    valueMap.put(key, this);
                }
            }
            return new ValueOverride();
        }
        
        protected <V> ValueDeclaration<V, FactoryGenerator<R>> setNested(FactoryGenerator<?> nested, String key) {
            ValueDeclaration<V, ?> nestedValue = nested.set(key);
            return new ValueDeclaration<V, FactoryGenerator<R>>() {
                @Override
                public FactoryGenerator<R> to(Function<ValueMap, ? extends V> valueFunction) {
                    nestedValue.to(valueFunction);
                    return Generator.this;
                }
                @Override
                public FactoryGenerator<R> to(DataSource<? extends V> dataSource) {
                    nestedValue.to(dataSource);
                    return Generator.this;
                }
                @Override
                public FactoryGenerator<R> toNext(String key) {
                    nestedValue.toNext(key);
                    return Generator.this;
                }
            };
        }

        @Override
        public String toString() {
            return "Generate " + DefaultFactory.this;
        }
    }

    protected class FactoryValueDeclaration<T> extends DefValueDeclaration<T> implements FactoriesSetup.FactoryValueSetup<R, T> {

        public FactoryValueDeclaration(String id) {
            super(id);
        }

        @Override
        public FactorySetup<R> factorySetup() {
            return DefaultFactory.this;
        }
    }
    
    protected static class New<R> implements FactoriesSetup.NewFactory<R> {

        private final DefaultFactories owner;
        private final String id;
        private final Class<R> clazz;
        private final List<Consumer<BuilderSetup<?,?>>> assignments = new ArrayList<>();

        public New(DefaultFactories owner, String id, Class<R> clazz) {
            this.owner = owner;
            this.id = id;
            this.clazz = clazz;
        }

        @Override
        public Class<R> getValueType() {
            return clazz;
        }

        @Override
        public <B> BuilderSetup<B, R> builder(Function<? super ValueMap, ? extends B> newBuilder) {
            Builder b = new Builder<>(owner, id, clazz, null, newBuilder);
            assignments.forEach(c -> c.accept(b));
            return b;
        }

        @Override
        public <T> ValueDeclaration<T, FactoriesSetup.NewFactory<R>> assign(String key) {
            return new ValueDeclaration<T, NewFactory<R>>() {
                @Override
                public NewFactory<R> to(Function<ValueMap, ? extends T> valueFunction) {
                    assignments.add(b -> b.assign(key).to(valueFunction));
                    return New.this;
                }
                @Override
                public NewFactory<R> to(DataSource<? extends T> dataSource) {
                    assignments.add(b -> b.assign(key).to(dataSource));
                    return New.this;
                }
            };
        }
    }
   
    protected static class Builder<B, R> 
                    extends DefaultBuilderBase<B, FactoriesSetup.BuilderSetup<B, R>>
                    implements FactoriesSetup.BuilderSetup<B, R> {
        
        private final Class<R> instanceClass;

        public Builder(DefaultFactories factories, String id, Class<R> instanceClass, Class<B> clazz, Function<? super ValueMap, ? extends B> newBuilder) {
            super(factories, factories, id, clazz, newBuilder, new HashMap<>());
            this.instanceClass = instanceClass;
        }

        @Override
        public <T> ValueDeclaration<T, ? extends BuilderSetup<B, R>> assign(String key) {
            return addValue(key, new BuilderValueDeclaration(key));
        }

        @Override
        public FactorySetup<R> build(BiFunction<? super B, ? super ValueMap, ? extends R> buildFunction) {
            class NewInstance implements Function<ValueMap, R> {
                @Override
                public R apply(ValueMap vm) {
                    B builder = create(vm);
                    return buildFunction.apply(builder, vm);
                }
            }
            return new DefaultFactory<>(getFactories(), this, getId(), instanceClass, new NewInstance(), getValueMap());
        }

        @Override
        public String toString() {
            Class<?> c = getValueType();
            return "new " + super.toString() + (c == null ? "" : " from " + c.getSimpleName());
        }
        
        protected class BuilderValueDeclaration<T> extends DefValueDeclaration<T> implements FactoriesSetup.BuilderValueSetup<B, R, T> {

            public BuilderValueDeclaration(String id) {
                super(id);
            }

            @Override
            public FactoriesSetup.BuilderSetup<B, R> builderSetup() {
                return Builder.this;
            }
        }
    }
    
    
    protected static <T> BiConsumer<Object,T> defaultSetter(Class<?> clazz, String id) {
        for (Field f: clazz.getDeclaredFields()) {
            if (f.getName().equals(id)) {
                return fieldSetter(f);
            }
        }
        Class<?> sup = clazz.getSuperclass();
        if (sup != null) {
            return defaultSetter(sup, id);
        }
        throw new IllegalArgumentException(id);
    }
    
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
    
    protected static class NewInstance<T> implements Supplier<T> {
        
        private final Constructor<T> constructor;

        public NewInstance(Class<T> clazz) {
            try {
                this.constructor = clazz.getConstructor();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public T get() {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
