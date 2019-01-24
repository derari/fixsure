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
    
    interface ValueMap extends Factories {
        
        <T> T get(String key);
        
        default int getInt(String key) {
            return i(key);
        }
        
        default String getStr(String key) {
            return str(key);
        }
        
        default boolean getBool(String key) {
            return x(key);
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
        
        default byte b(String key) {
            return this.<Number>get(key).byteValue();
        }

        default char c(String key) {
            return this.<Character>get(key);
        }
        
        default double d(String key) {
            return this.<Number>get(key).doubleValue();
        }
        
        default double f(String key) {
            return this.<Number>get(key).floatValue();
        }
        
        default long l(String key) {
            return this.<Number>get(key).longValue();
        }
        
        default int i(String key) {
            return this.<Number>get(key).intValue();
        }
        
        default boolean x(String key) {
            return this.<Boolean>get(key);
        }
        
        default String str(String key) {
            return Objects.toString(get(key), null);
        }
    }
}
