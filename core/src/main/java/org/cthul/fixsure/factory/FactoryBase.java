package org.cthul.fixsure.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cthul.fixsure.factory.Factory.Include;
import org.cthul.fixsure.factory.ValueSource.FactoryMap;

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
    private Map<String, ValueSource<?>> nestedSources = null;
    private Map<String, Include<?>> nestedIncludes = null;
    private boolean initialized = false;

    public FactoryBase() {
        this.parent = null;
    }

    public FactoryBase(FactoryParent parent) {
        this.parent = parent;
    }
    
    protected abstract FactoryMap getFactoryMap();
    
    protected void reset() {
        if (resetting) return;
        resetting = true;
        try {
            initialized = false;
            doReset();
        } finally {
            resetting = false;
        }
    }
    
    protected void initialize() {
        if (initialized) return;
        initialized = true;
        if (nestedSources != null) {
            nestedSources.forEach((key, vs) -> applyNestedSet(key, null).toValuesOf(vs));
        }
        if (nestedIncludes != null) {
            nestedIncludes.forEach((key, inc) -> applyNestedSet(key, null).include(inc));
        }
    }
    
    protected void doReset() {
        generators.clear();
        includes.forEach(Include::reset);
    }
    
    protected void putValueSource(String key, ValueSource<?> valueSource) {
        if (key.contains(".")) {
            nestedSet(key, null).toValuesOf(valueSource);
        }
        sources.put(key, valueSource);
        generators.remove(key);
    }
    
    protected void addInclude(String key, Include<?> include) {
        if (key.contains(".")) {
            nestedSet(key, null).include(include);
        }
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
        ValueGenerator<T> gen = internPeekGenerator(key);
        if (gen != null) return gen;
        throw new IllegalArgumentException(key);
    }
    
    @Override
    public <T> ValueGenerator<T> peekGenerator(String key) {
        ValueGenerator<?> gen = generators.computeIfAbsent(key, this::newGenerator);
        if (gen == NO_VALUE) return null;
        return (ValueGenerator<T>) gen;
    }
    
    protected <T> ValueGenerator<T> internPeekGenerator(String key) {
        return peekGenerator(key);
    }
    
    protected ValueGenerator<?> newGenerator(String key) {
        ValueSource<?> src = sources.get(key);
        if (src != null) return src.generate(getFactoryMap());
        // 2) includes' sources
        src = getSourceFromInclude(key);
        if (src != null) return src.generate(getFactoryMap());
        // 3) parent generator
        if (parent != null) {
            ValueGenerator<?> gen = parent.peekGenerator(key);
            if (gen != null) return gen.link(getFactoryMap());
        }
        // 4) default generator
        for (Include<?> inc: includes) {
            src = inc.attributeSource(key, true);
            if (src != null) {
                sources.put(key, src);
                return src.generate(getFactoryMap());
            }
        }
        return NO_VALUE;
    }
    
    protected ValueSource<?> getSourceFromInclude(String key) {
        for (Include<?> inc: includes) {
            ValueSource<?> src = inc.attributeSource(key, false);
            if (src != null) {
                sources.put(key, src);
                return src;
            }
        }
        return null;
    }
    
    protected <T> ValueGenerator<T> peekParentGenerator(String key) {
        if (parent == null) return null;
        return parent.peekGenerator(key);
    }
    
    @Override
    public <T> ValueSource<T> peekSource(String key, boolean useDefault) {
        // 1) own sources
        ValueSource<?> src = sources.get(key);
        if (src != null) return (ValueSource) src;
        // 2) includes' sources
        src = getSourceFromInclude(key);
        if (src != null) return (ValueSource) src;
        // 3) parent generator
        if (parent != null) {
            src = parent.peekSource(key, useDefault);
            if (src != null) return (ValueSource) src;
        }
        return null;
    }

    protected <T, B> FactoriesSetup.ValueDeclaration<T, B> set(String key, B builder) {
        if (key.contains(".")) {
            return nestedSet(key, builder);
        }
        return new FactoriesSetup.ValueDeclaration<T, B>() {
            @Override
            public B toValuesOf(ValueSource<? extends T> valueSource) {
                putValueSource(key, valueSource);
                return builder;
            }

            @Override
            public B include(Include<? extends T> include) {
                addInclude(key, include);
                return builder;
            }
        };
    }
    
    protected <T, B> FactoriesSetup.ValueDeclaration<T, B> nestedSet(String key, B builder) {
        if (initialized) {
            return applyNestedSet(key, builder);
        }
        return new FactoriesSetup.ValueDeclaration<T, B>() {
            @Override
            public B toValuesOf(ValueSource<? extends T> valueSource) {
                if (nestedSources == null) nestedSources = new HashMap<>();
                nestedSources.put(key, valueSource);
                return builder;
            }
            @Override
            public B include(Include<? extends T> include) {
                if (nestedIncludes == null) nestedIncludes = new HashMap<>();
                nestedIncludes.put(key, include);
                return builder;
            }
        };
    }
    
    protected <T, B> FactoriesSetup.ValueDeclaration<T, B> applyNestedSet(String key, B builder) {
        int i = key.indexOf('.');
        return valueGenerator(key.substring(0, i)).set(key.substring(i+1), builder);
    }
    
    protected static final ValueGenerator<?> NO_VALUE = ValueGenerator.constant(null);
}
