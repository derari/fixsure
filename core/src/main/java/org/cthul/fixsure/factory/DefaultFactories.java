package org.cthul.fixsure.factory;

import java.util.HashMap;
import java.util.Map;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public class DefaultFactories extends FactoryBase implements Factories, FactoriesSetup {
    
    /**
     * Creates a new builder for factories.
     * @return factories setup
     */
    @org.cthul.fixsure.api.Factory
    public static FactoriesSetup newFactoriesSetup() {
        return new DefaultFactories();
    }
    
    private final Map<String, DefaultFactory<?>> factories = new HashMap<>();
    private boolean resetting = false;

    public DefaultFactories() {
    }

//    public DefaultFactories(FactoryBase parent) {
//        super(parent);
//    }

    @Override
    public Factories toFactories() {
        return this;
    }
    
    @Override
    public void reset() {
        if (resetting) return;
        try {
            resetting = true;
            super.reset();
            factories.values().forEach(FactoryBase::reset);
        } finally {
            resetting = false;
        }
    }

    @Override
    public <T> Factory<T> factory(String id) {
        Factory<T> f = (Factory) factories.get(id);
        if (f != null) return f;
        try {
            generator(id);
            throw new IllegalArgumentException(
                    id + " is not a factory, use #generator(String) instead");
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    void addFactory(String id, DefaultFactory<?> factory) {
        factories.put(id, factory);
        putDataSource(id, factory);
    }

    @Override
    public <T> NewFactory<T> newFactory(String id, Class<T> clazz) {
        return new DefaultFactory.New<>(this, id, clazz);
    }

    @Override
    public <T> FlGenerator<T> generator(String id) {
        return super.generator(id);
    }

    @Override
    public FactoriesSetup add(String id, DataSource<?> dataSource) {
        putDataSource(id, dataSource);
        return this;
    }
}
