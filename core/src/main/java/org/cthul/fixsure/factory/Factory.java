package org.cthul.fixsure.factory;

import java.util.Objects;
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
    
    interface FactoryGenerator<T> extends FlGenerator<T> {
        
        FactoryGenerator<T> set(Object... keyValues);
        
        <V> ValueDeclaration<V, FactoryGenerator<T>> set(String key);
    }
    
    interface ValueMap extends Factories {
        
        <T> T get(String key);
        
        default int getInt(String key) {
            return this.<Number>get(key).intValue();
        }
        
        default String getStr(String key) {
            return Objects.toString(get(key), null);
        }
        
        default boolean getBool(String key) {
            return get(key);
        }
        
        default <T> T get(Typed<T> token) {
            return get(token.toString());
        }
        
        default int getInt(Typed<? extends Number> token) {
            return getInt(token.toString());
        }
        
        default String getStr(Typed<?> token) {
            return getStr(token.toString());
        }
        
        default boolean getBool(Typed<Boolean> token) {
            return getBool(token.toString());
        }

        @Override
        default void reset() {
            throw new UnsupportedOperationException();
        }
    }
}
