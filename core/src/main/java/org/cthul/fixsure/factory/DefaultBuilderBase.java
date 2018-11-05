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
import org.cthul.fixsure.factory.Factory.ValueMap;
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
    private final String instanceKey;
    private final Map<String, ValueEntry> valueMap;
    private final List<BiFunction<? super V, ? super ValueMap, ? extends V>> steps = new ArrayList<>();

    public DefaultBuilderBase(DefaultFactories owner, FactoryBase parent, String id, Class<V> clazz, String instanceKey, Map<String, ValueEntry> valueMap) {
        super(parent);
        this.factories = owner;
        this.id = id;
        this.clazz = clazz;
        this.valueMap = valueMap;
        this.instanceKey = instanceKey;
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
    
    protected <VD extends DefValueDeclaration<?>> VD addValue(String key, VD valueDeclaration) {
        valueMap.put(key, valueDeclaration);
        return valueDeclaration;
    }

    @Override
    public abstract <T> FactoriesSetup.ValueDeclaration<T, ? extends This> assign(String key);

    @Override
    public This applyValues(BiFunction<? super V, ? super ValueMap, ? extends V> function) {
        steps.add(function);
        return (This) this;
    }

    @Override
    public Class<V> getValueType() {
        return clazz;
    }

    @Override
    public String toString() {
        return id;
    }

//    protected V newInstance(Factory.ValueMap vm) {
//        return newInstance.apply(vm);
//    }
    
    protected V create(DefaultValueMap vm) {
        V instance = vm.get(instanceKey);
        for (BiFunction<? super V, ? super ValueMap, ? extends V> step: steps) {
            instance = step.apply(instance, vm);
        }
        vm.values.put(instanceKey, instance);
        return instance;
    }
    
    protected class DefValueDeclaration<T> implements FactoriesSetup.ValueDeclaration<T,This>, ValueEntry {
        
        private final String id;
        private Function<? super Factory.ValueMap, ? extends T> valueFunction = null;
        private String factoryId = null;

        public DefValueDeclaration(String id) {
            Objects.requireNonNull(id, "id");
            this.id = id;
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
        public This to(Function<? super Factory.ValueMap, ? extends T> valueFunction) {
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
//            return (T) values.computeIfAbsent(key, k -> {
//                if (!recursionGuard.add(k)) {
//                    throw new IllegalArgumentException("recursion: " + recursionGuard);
//                }
//                try {
//                    ValueEntry val = valueMap.get(k);
//                    if (val != null) { // && val.getGeneratorId() == null
//                        return val.get(this);
//                    }
//                    return generator(k).next();
//                } finally {
//                    recursionGuard.remove(k);
//                }
//            });
            Object v = values.get(key);
            if (v == null) {
                v = computeValue(key);
                values.put(key, v);
            }
            return (T) v;
        }
        
        protected Object computeValue(String key) {
            if (!recursionGuard.add(key)) {
                throw new IllegalArgumentException("recursion: " + recursionGuard);
            }
            try {
                ValueEntry val = valueMap.get(key);
                if (val != null) { // && val.getGeneratorId() == null
                    return val.get(this);
                }
                return generator(key).next();
            } finally {
                recursionGuard.remove(key);
            }
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
