package org.cthul.fixsure.factory;

import org.cthul.fixsure.Typed;
import org.cthul.fixsure.factory.FactoriesSetup.ValueDeclaration;
import org.cthul.fixsure.fluents.FlGenerator;

/**
 *
 */
public interface Factory<T> extends FlGenerator<T>, Typed<T> {

    @Override
    default T next() {
        return create();
    }
    
    T create();
    
    default T create(Object... keyValues) {
        return generate(keyValues).next();
    }
    
    FactoryGenerator<T> generate();
    
    default FactoryGenerator<T> generate(Object... keyValues) {
        return generate().set(keyValues);
    }
    
    void reset();
    
    interface FactoryGenerator<T> extends FlGenerator<T> {
        
        FactoryGenerator<T> set(Object... keyValues);
        
        <V> ValueDeclaration<V, FactoryGenerator<T>> set(String key);
        
        default <V> ValueDeclaration<V, FactoryGenerator<T>> set(Typed<V> token) {
            return set(token.toString());
        }
        
        ValueGenerator<T> asValueGenerator();
        
        Include<T> asInclude();
    }
    
    interface Include<T> extends ValueSource<T> {

        ValueSource<?> attributeSource(String key, boolean useDefault);
        
        void reset();
    }
}
