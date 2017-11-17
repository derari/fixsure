package org.cthul.fixsure.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.factory.FactoriesSetup.BuilderSetupBase;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public abstract class DefaultBuilderBase<V, This extends BuilderSetupBase<V,This>>
                extends FactoryBase
                implements BuilderSetupBase<V, This> {
    
    private final DefaultFactories factories;
    private final String id;
    private final Class<V> clazz;
    private final Function<? super Factory.ValueMap, ? extends V> newInstance;
    private final Map<String, ValueEntry> valueMap;
    private final List<DefValueDeclaration<?>> steps = new ArrayList<>();

    public DefaultBuilderBase(DefaultFactories owner, FactoryBase parent, String id, Class<V> clazz, Function<? super Factory.ValueMap, ? extends V> newInstance, Map<String, ValueEntry> valueMap) {
        super(parent);
        this.factories = owner;
        this.id = id;
        this.clazz = clazz;
        this.valueMap = valueMap;
        this.newInstance = newInstance;
    }

    protected DefaultFactories getFactories() {
        return factories;
    }

    protected String getId() {
        return id;
    }
    
    protected Map<String, ValueEntry> getValueMap() {
        return valueMap;
    }
    
    protected <VD extends DefValueDeclaration<?>> VD addStep(String key, VD valueDeclaration) {
        valueMap.put(key, valueDeclaration);
        steps.add(valueDeclaration);
        return valueDeclaration;
    }

    @Override
    public abstract <T> FactoriesSetup.ValueDeclaration<T, This> apply(String key, BiFunction<? super V, T, ? extends V> setter);

    @Override
    public Class<V> getValueType() {
        return clazz;
    }

    @Override
    public String toString() {
        return id;
    }

    protected V newInstance(Factory.ValueMap vm) {
        return newInstance.apply(vm);
    }
    
    public V create(Factory.ValueMap vm) {
        V instance = newInstance(vm);
        for (DefValueDeclaration<?> value: steps) {
            instance = value.set(instance, vm);
        }
        return instance;
    }
    
    protected class DefValueDeclaration<T> implements FactoriesSetup.ValueDeclaration<T,This>, ValueEntry {
        
        private final String id;
        private final BiFunction<? super V, T, ? extends V> setter;
        private Function<Factory.ValueMap, ? extends T> valueFunction = null;
        private String factoryId = null;

        public DefValueDeclaration(String id, BiFunction<? super V, T, ? extends V> setter) {
            Objects.requireNonNull(id, "id");
            Objects.requireNonNull(setter, "setter");
            this.id = id;
            this.setter = setter;
        }

        public V set(V builder, Factory.ValueMap valueMap) {
            return setter.apply(builder, valueMap.get(id));
        }

        @Override
        public T get(Factory.ValueMap valueMap) {
            if (valueFunction == null) {
                factoryId = id;
                valueFunction = vm -> vm.next(id);
            }
            return valueFunction.apply(valueMap);
        }

        @Override
        public This to(DataSource<? extends T> dataSource) {
            String srcId = toString() + "@" + DefaultFactory.uniqueIdStr();
            factories.add(srcId, dataSource);
//            factoryId = srcId;
            return to(vm -> vm.<T>generator(srcId).next());
        }

        @Override
        public This toNext(String key) {
            factoryId = key;
            return FactoriesSetup.ValueDeclaration.super.toNext(key);
        }

        @Override
        public This to(Function<Factory.ValueMap, ? extends T> valueFunction) {
            this.valueFunction = valueFunction;
            return (This) DefaultBuilderBase.this;
        }

        @Override
        public String getGeneratorId() {
            if (valueFunction == null) return id;
            return factoryId;
        }

        @Override
        public String toString() {
            return DefaultBuilderBase.this.toString() + "#" + id;
        }
    }
    
    protected static interface ValueEntry {
                
        Object get(Factory.ValueMap valueMap);
        
        default String getGeneratorId() {
            return null;
        }
    }
    
    protected static class DefaultValueMap extends FactoryBase implements Factory.ValueMap {

        private final Factories factories;
        private final Map<String, ValueEntry> valueMap;
        private final Map<String, Object> values = new HashMap<>();
        private final Set<String> recursionGuard = new LinkedHashSet<>();
        
        public DefaultValueMap(Map<String, ValueEntry> valueMap, DefaultBuilderBase<?,?> parent) {
            this(valueMap, parent.getFactories(), parent);
        }
        
        public DefaultValueMap(Map<String, ValueEntry> valueMap, DefaultFactories factories, FactoryBase parent) {
            super(parent);
            this.factories = factories;
            this.valueMap = valueMap;
        }
        
        public void set(Map<String, Object> values) {
            this.values.putAll(values);
        }

        @Override
        public <T> T get(String key) {
            return (T) values.computeIfAbsent(key, k -> {
                if (!recursionGuard.add(k)) {
                    throw new IllegalArgumentException("recursion: " + recursionGuard);
                }
                try {
                    ValueEntry val = valueMap.get(k);
                    if (val != null) { // && val.getGeneratorId() == null
                        return val.get(this);
                    }
                    return generator(k).next();
                } finally {
                    recursionGuard.remove(k);
                }
            });
        }

        @Override
        public <T> FlGenerator<T> generator(String id) {
            return super.generator(id);
        }

        @Override
        public <T> Factory<T> factory(String id) {
            return factories.factory(id);
        }

        @Override
        public void reset() {
            Factory.ValueMap.super.reset();
        }

        @Override
        public String toString() {
            Map<String, Object> data = new LinkedHashMap<>(values);
            valueMap.forEach((s,v) -> data.putIfAbsent(s, "?"));
            return "" + data;
        }
    }
}
