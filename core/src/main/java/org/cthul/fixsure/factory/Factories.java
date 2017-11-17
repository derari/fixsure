package org.cthul.fixsure.factory;

import org.cthul.fixsure.Typed;
import org.cthul.fixsure.factory.Factory.FactoryGenerator;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public interface Factories {
    
    static FactoriesSetup build() {
        return DefaultFactories.newFactoriesSetup();
    }
    
    <T> FlGenerator<T> generator(String id);
    
    default <T> T next(String id) {
        return this.<T>generator(id).next();
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
    
    <T> Factory<T> factory(String id);
    
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
    
    default <T> T create(String id) {
        return this.<T>factory(id).create();
    }
    
    default <T> T create(String id, Object... keyValues) {
        return this.<T>factory(id).create(keyValues);
    }
    
    default <T> FactoryGenerator<T> generate(String id) {
        return this.<T>factory(id).generate();
    }
    
    default <T> FactoryGenerator<T> generate(String id, Object... keyValues) {
        return this.<T>factory(id).generate(keyValues);
    }
    
    void reset();
}
