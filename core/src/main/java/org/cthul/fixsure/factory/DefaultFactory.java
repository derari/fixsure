package org.cthul.fixsure.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.factory.FactoriesSetup.ValueDeclaration;
import org.cthul.fixsure.factory.Factory.FactoryGenerator;
import org.cthul.fixsure.factory.ValueGenerator.ValueMap;
import org.cthul.fixsure.factory.ValueSource.FactoryMap;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 * @param <V>
 */
public class DefaultFactory<V> extends FactoryBase implements Factory<V>, FactoryGenerator<V> {
    
    private final Set<String> recursionGuard = new LinkedHashSet<>();
    private final VM vm = new VM(this);
    private final String key;
    private final Class<V> clazz;
    private final List<BiFunction<? super V, ? super ValueMap, ? extends V>> steps;

    public DefaultFactory(FactoryParent parent, String key, Class<V> clazz) {
        super(parent);
        this.key = key;
        this.clazz = clazz;
        this.steps = new ArrayList<>();
    }

    protected DefaultFactory(DefaultFactory parent) {
        super(parent);
        this.key = parent.key;
        this.clazz = parent.clazz;
        this.steps = parent.steps;
    }

    @Override
    public V create() {
        return next();
    }

    @Override
    public Class<V> getValueType() {
        return clazz;
    }

    @Override
    public DefaultFactory<V> generate() {
        initialize();
        DefaultFactory<V> generator = new DefaultFactory<>(this);
        generator.initialize();
        return generator;
    }
    
    protected DefaultFactory<V> copy(FactoryMap factoryMap) {
        return generate();
    }

    @Override
    protected FactoryMap getFactoryMap() {
        return vm;
    }
    
    protected void addStep(BiFunction<? super V, ? super ValueMap, ? extends V> step) {
        steps.add(step);
    }

    @Override
    public String toString() {
        return key + "(" + super.toString() + ")";
    }

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return sb.append(key).append("(").append(super.toString()).append(")");
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public V next() {
        return next(new VM(this));
    }

    protected V next(ValueMap valueMap) {
        initialize();
        V value = null;
        for (BiFunction<? super V, ? super ValueMap, ? extends V> step: steps) {
            value = step.apply(value, valueMap);
        }
        return value;
    }
    
    @Override
    public Include<V> asInclude() {
        return new Include<V>() {
            @Override
            public void reset() {
                DefaultFactory.this.reset();
            }
            @Override
            public ValueSource<?> attributeSource(String key, boolean useDefault) {
                return DefaultFactory.this.peekSource(key, useDefault);
            }
            @Override
            public ValueGenerator<? extends V> generate(FactoryMap factoryMap) {
                return DefaultFactory.this.generate().asIncludedGenerator();
            }
            @Override
            public Class<V> getValueType() {
                return DefaultFactory.this.getValueType();
            }
            @Override
            public String toString() {
                return DefaultFactory.this.toString();
            }
        };
    }

    @Override
    public ValueGenerator<V> asValueGenerator() {
        return new ValueGenerator<V>() {
            @Override
            public V next(ValueMap valueMap) {
                return DefaultFactory.this.next();
            }
            @Override
            public V next(ValueMap valueMap, Function<ValueMap, ValueMap> transform) {
                return DefaultFactory.this.next(transform.apply(new VM(DefaultFactory.this)));
            }
            @Override
            public ValueGenerator<V> link(FactoryMap factoryMap) {
                return copy(factoryMap).asValueGenerator();
            }
            @Override
            public <V, B> FactoriesSetup.ValueDeclaration<V, B> set(String key, B builder) {
                return DefaultFactory.this.set(key, builder);
            }
            @Override
            public Class<V> getValueType() {
                return DefaultFactory.this.getValueType();
            }
            @Override
            public String toString() {
                return DefaultFactory.this.toString();
            }
        };
    }
    
    public ValueGenerator<V> asIncludedGenerator() {
        return new ValueGenerator<V>() {
            @Override
            public V next(ValueMap valueMap) {
                return DefaultFactory.this.next(valueMap);
            }
            @Override
            public ValueGenerator<V> link(FactoryMap factoryMap) {
                return copy(factoryMap).asIncludedGenerator();
            }
            @Override
            public <V, B> FactoriesSetup.ValueDeclaration<V, B> set(String key, B builder) {
                return DefaultFactory.this.set(key, builder);
            }
            @Override
            public Class<V> getValueType() {
                return DefaultFactory.this.getValueType();
            }
            @Override
            public String toString() {
                return DefaultFactory.this.toString();
            }
        };
    }
    
    @Override
    public FactoryGenerator<V> set(Object... keyValues) {
        for (int i = 0; i+1 < keyValues.length; i += 2) {
            String key = String.valueOf(keyValues[i]);
            Object val = keyValues[i+1];
            if (val instanceof DataSource) {
                set(key).to((DataSource) val);
            } else {
                set(key).to(val);
            }
        }
        return this;
    }
    
    @Override
    public <T> ValueDeclaration<T, FactoryGenerator<V>> set(String key) {
        return set(key, this);
    }

    protected static class VM implements ValueMap, FactoryMap {
        
        private final DefaultFactory owner;
        private final Map<String, Object> values = new HashMap<>();

        public VM(DefaultFactory owner) {
            this.owner = owner;
        }
        
        public void clear() {
            values.clear();
        }

        @Override
        public <T> T get(String key) {
            Object v = values.get(key);
            if (v == null) {
                v = computeValue(key);
                values.put(key, v);
            }
            return (T) v;
        }
        
        protected Set<String> recursionGuard() {
            return owner.recursionGuard;
        }
        
        protected Object computeValue(String key) {
            if (!recursionGuard().add(key)) {
                throw new IllegalArgumentException("recursion: " + key + " --> " + owner.recursionGuard);
            }
            try {
                return peekOwnerValueGenerator(key).next(this);
            } finally {
                recursionGuard().remove(key);
            }
        }

        @Override
        public <T> FlGenerator<T> generator(String key) {
            return ValueGenerator.toGenerator(this.<T>valueGenerator(key), this);
        }

        @Override
        public <T> Factory<T> factory(String key) {
            ValueGenerator<T> gen = valueGenerator(key);
            DefaultFactory<T> fac = new DefaultFactory<>(owner, key, gen.getValueType());
            fac.steps.add((v, vm) -> gen.next(vm));
            return fac;
        }

        @Override
        public <T> ValueGenerator<T> valueGenerator(String key) {
            return ValueMap.super.valueGenerator(key);
        }

        @Override
        public <T> ValueGenerator<T> peekValueGenerator(String key) {
            if (!recursionGuard().add(key)) {
                ValueGenerator<?> gen = owner.peekParentGenerator(key);
                if (gen != null) return (ValueGenerator<T>) gen;
                throw new IllegalArgumentException("recursion: " + key + " --> " + recursionGuard());
            }
            try {
                return peekOwnerValueGenerator(key);
            } finally {
                recursionGuard().remove(key);
            }
        }
        
        protected <T> ValueGenerator<T> peekOwnerValueGenerator(String key) {
            return owner.internPeekGenerator(key);
        }

        @Override
        public void reset() {
            throw new IllegalStateException("Cannot reset internal Factories");
        }
    }
    
    public static class Setup<V> implements FactoriesSetup.FactorySetup<V> {
        
        private final FactoriesSetup factoriesSetup;
        private final DefaultFactory<V> factory;

        public Setup(FactoriesSetup factoriesSetup, FactoryParent factories, String key, Class<V> clazz) {
            this.factoriesSetup = factoriesSetup;
            this.factory = new DefaultFactory<>(factories, key, clazz);
            factoriesSetup.add(key, factory);
        }

        public Setup(FactoryParent factories, String key, Class<V> clazz) {
            this.factoriesSetup = null;
            this.factory = new DefaultFactory<>(factories, key, clazz);
        }

        public DefaultFactory<V> getFactory() {
            return factory;
        }

        @Override
        public FactoriesSetup factoriesSetup() {
            return factoriesSetup;
        }

        @Override
        public Class<V> getValueType() {
            return factory.getValueType();
        }

        @Override
        public FactorySetup<V> applyValues(BiFunction<? super V, ? super ValueMap, ? extends V> function) {
            factory.steps.add(function);
            return this;
        }
        
        @Override
        public FactorySetup<V> assignValues(String key, ValueSource<?> valueSource) {
            factory.putValueSource(key, valueSource);
            return this;
        }

        @Override
        public FactorySetup<V> include(String key, Include<?> include) {
            this.factory.addInclude(key, include);
            return this;
        }

        @Override
        public String toString() {
            return factory.toString();
        }
    }
}
