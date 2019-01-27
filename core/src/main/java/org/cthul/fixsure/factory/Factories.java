package org.cthul.fixsure.factory;

import org.cthul.fixsure.Typed;
import org.cthul.fixsure.factory.Factory.FactoryGenerator;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public interface Factories {
    
    static FactoriesSetup build() {
        return new DefaultFactories.Setup();
    }
    
    <T> FlGenerator<T> generator(String key);
    
    default <T> T next(String key) {
        return this.<T>generator(key).next();
    }
    
    default <T> FlGenerator<T> generator(Typed<T> token) {
        return generator(token.toString());
    }
    
    default <T> T next(Typed<T> token) {
        return next(token.toString());
    }
    
    default <T> Factory<T> factory(Class<T> clazz) {
        return factory(clazz.getName());
    }
    
    default <T> Factory<T> factory(Typed<T> token) {
        return factory(token.toString());
    }
    
    <T> Factory<T> factory(String key);
    
    default <T> T create(Typed<T> token) {
        return factory(token).create();
    }
    
    default <T> T create(Typed<T> token, Object... keyValues) {
        return factory(token).create(keyValues);
    }
    
    default <T> FactoryGenerator<T> generate(Typed<T> token) {
        return factory(token).generate();
    }
    
    default <T> FactoryGenerator<T> generate(Typed<T> token, Object... keyValues) {
        return factory(token).generate(keyValues);
    }
    
    default <T> T create(Class<T> clazz) {
        return factory(clazz).create();
    }
    
    default <T> T create(Class<T> clazz, Object... keyValues) {
        return factory(clazz).create(keyValues);
    }
    
    default <T> FactoryGenerator<T> generate(Class<T> clazz) {
        return factory(clazz).generate();
    }
    
    default <T> FactoryGenerator<T> generate(Class<T> clazz, Object... keyValues) {
        return factory(clazz).generate(keyValues);
    }
    
    default <T> T create(String key) {
        return this.<T>factory(key).create();
    }
    
    default <T> T create(String key, Object... keyValues) {
        return this.<T>factory(key).create(keyValues);
    }
    
    default <T> FactoryGenerator<T> generate(String key) {
        return this.<T>factory(key).generate();
    }
    
    default <T> FactoryGenerator<T> generate(String key, Object... keyValues) {
        return this.<T>factory(key).generate(keyValues);
    }
    
    void reset();
}
