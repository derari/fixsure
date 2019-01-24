package org.cthul.fixsure.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cthul.fixsure.factory.Factory.Include;

/**
 * Implements generator lookup
 * 1) own data sources
 * 2) includes' data sources (excluding factories level)
 * 3) parent generator
 */
public abstract class FactoryBase implements FactoryParent {
    
    private final FactoryParent parent;
    private final Map<String, ValueSource<?>> sources = new HashMap<>();
    private final Map<String, ValueGenerator<?>> generators = new HashMap<>();
    private final List<Include<?>> includes = new ArrayList<>();
    private boolean resetting = false;

    public FactoryBase() {
        this.parent = null;
    }

    public FactoryBase(FactoryParent parent) {
        this.parent = parent;
    }
    
    protected abstract Factories getFactories();
    
    protected void reset() {
        if (resetting) return;
        resetting = true;
        try {
            doReset();
        } finally {
            resetting = false;
        }
    }
    
    protected void doReset() {
        generators.clear();
        includes.forEach(Include::reset);
    }
    
    protected void putValueSource(String key, ValueSource<?> valueSource) {
        sources.put(key, valueSource);
        generators.remove(key);
    }
    
    protected void addInclude(String key, Include<?> include) {
        putValueSource(key, include);
        includes.add(include);
    }
    
    @Override
    public Map<String, String> getDescriptionForChild() {
        return getDescription();
    }
    
    protected Map<String, String> getDescription() {
        Map<String, String> map = parent == null ? new HashMap<>() : parent.getDescriptionForChild();
        generators.forEach((key,gen) -> map.put(key, gen.toString()));
        sources.forEach((key,src) -> map.putIfAbsent(key, src.toString()));
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Map<String, String> map = getDescription();
        map.forEach((key,val) -> {
            if (!key.startsWith("@")) {
                sb.append(key).append("=").append(val).append(",");
            }
        });
        if (sb.length() == 0) return "";
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
    
    protected <T> ValueGenerator<T> valueGenerator(String key) {
        ValueGenerator<T> gen = peekGenerator(key);
        if (gen != null) return gen;
        throw new IllegalArgumentException(key);
    }
    
    @Override
    public <T> ValueGenerator<T> peekGenerator(String key) {
        ValueGenerator<?> gen = generators.computeIfAbsent(key, this::newGenerator);
        if (gen == NO_VALUE) return null;
        return (ValueGenerator<T>) gen;
    }
    
    protected ValueGenerator<?> newGenerator(String key) {
        ValueSource<?> src = sources.get(key);
        if (src != null) return src.generate(getFactories());
        // 2) includes' sources
        for (Include<?> inc: includes) {
            src = inc.attributeSource(key, false);
            if (src != null) {
                sources.put(key, src);
                return src.generate(getFactories());
            }
        }
        // 3) parent generator
        if (parent != null) {
            ValueGenerator<?> gen = parent.peekGenerator(key);
            if (gen != null) return gen;
        }
        // 4) default generator
        for (Include<?> inc: includes) {
            src = inc.attributeSource(key, true);
            if (src != null) {
                sources.put(key, src);
                return src.generate(getFactories());
            }
        }
        return NO_VALUE;
    }
    
    protected ValueGenerator<?> peekParentGenerator(String key) {
        return parent.peekGenerator(key);
    }
    
    @Override
    public <T> ValueSource<T> peekSource(String key, boolean useDefault) {
        // 1) own sources
        ValueSource<?> src = sources.get(key);
        if (src != null) return (ValueSource) src;
        // 2) includes' sources
        for (Include<?> inc: includes) {
            src = inc.attributeSource(key, useDefault);
            if (src != null) {
                return (ValueSource) src;
            }
        }
        // 3) parent generator
        if (parent != null) {
            src = parent.peekSource(key, useDefault);
            if (src != null) return (ValueSource) src;
        }
        return null;
    }
    
    protected static final ValueGenerator<?> NO_VALUE = new ValueGenerator<Object>() {
        @Override
        public Object next(Factory.ValueMap valueMap) {
            return null;
        }
        @Override
        public Class<Object> getValueType() {
            return null;
        }
    };
}
