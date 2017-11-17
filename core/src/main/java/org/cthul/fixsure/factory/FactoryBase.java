package org.cthul.fixsure.factory;

import java.util.HashMap;
import java.util.Map;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public abstract class FactoryBase {
    
    private final FactoryBase parent;
    private final Map<String, DataSource<?>> dataSources = new HashMap<>();
    private final Map<String, FlGenerator<?>> generators = new HashMap<>();

    public FactoryBase() {
        this.parent = null;
    }
    
    public FactoryBase(FactoryBase parent) {
        this.parent = parent;
    }
    
    protected void reset() {
        if (parent != null) parent.reset();
        generators.clear();
    }
    
    protected <T> FlGenerator<T> generator(String id) {
        FlGenerator<T> gen = peekGenerator(id);
        if (gen != null) return gen;
        throw new IllegalArgumentException(id);
    }
    
    protected <T> FlGenerator<T> peekGenerator(String id) {
        FlGenerator<?> gen = generators.computeIfAbsent(id, this::newGenerator);
        if (gen == NO_VALUE) return null;
        return (FlGenerator<T>) gen;
    }
    
    protected FlGenerator<?> newGenerator(String id) {
        DataSource<?> src = dataSources.get(id);
        if (src != null) return src.toGenerator().fluentData();
        return lookupGenerator(id);
    }
    
    protected FlGenerator<?> lookupGenerator(String id) {
        if (parent != null) return parent.peekGenerator(id);
        return NO_VALUE;
    }
    
    protected void putDataSource(String id, DataSource<?> dataSource) {
        dataSources.put(id, dataSource);
        generators.remove(id);
    }
    
    private static final FlGenerator<?> NO_VALUE = () -> "NO VALUE";
}
