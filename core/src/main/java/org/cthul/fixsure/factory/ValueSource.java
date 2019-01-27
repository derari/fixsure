package org.cthul.fixsure.factory;

import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.factory.ValueGenerator.ValueMap;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 * @param <T>
 */
public interface ValueSource<T> extends Typed<T> {

    ValueGenerator<? extends T> generate(FactoryMap factoryMap);

    @Override
    default Class<T> getValueType() {
        return null;
    }
    
    static <T> ValueSource<T> fromGenerator(ValueGenerator<? extends T> gen) {
        if (gen instanceof ValueSource) {
            return (ValueSource<T>) gen;
        }
        return new ValueSource<T>() {
            @Override
            public ValueGenerator<? extends T> generate(FactoryMap factoryMap) {
                return gen;
            }
            @Override
            public Class<T> getValueType() {
                return (Class) gen.getValueType();
            }
            @Override
            public String toString() {
                return gen.toString();
            }
        };
    }
    
    static <T> ValueSource<T> fromFunction(Function<? super ValueMap, ? extends T> function) {
        return ValueGenerator.fromFunction(function);
    }
    
    static <T> ValueSource<T> fromDataSource(DataSource<? extends T> source) {
        return new ValueSource<T>() {
            @Override
            public ValueGenerator<? extends T> generate(FactoryMap factoryMap) {
                return ValueGenerator.fromGenerator(source.toGenerator());
            }
            @Override
            public Class<T> getValueType() {
                return GeneratorTools.typeOf(source);
            }
            @Override
            public String toString() {
                return GeneratorTools.lambdaToString(source, new StringBuilder()).toString();
            }
        };
    }
    
    static <T> ValueSource<T> fromDataSupply(Supplier<? extends DataSource<? extends T>> source) {
        return new ValueSource<T>() {
            @Override
            public ValueGenerator<? extends T> generate(FactoryMap factoryMap) {
                return ValueGenerator.fromGenerator(source.get().toGenerator());
            }
            @Override
            public Class<T> getValueType() {
                return GeneratorTools.typeOf(source);
            }
            @Override
            public String toString() {
                return GeneratorTools.lambdaToString(source, new StringBuilder()).toString();
            }
        };
    }
    
    static <T> ValueSource<T> constant(T value) {
        return ValueGenerator.constant(value);
    }
    
    static <T> ValueSource<T> nextKey(String key) {
        return new ValueSource<T>() {
            @Override
            public ValueGenerator<? extends T> generate(FactoryMap factoryMap) {
                return ValueGenerator.nextKey(factoryMap, key);
            }
            @Override
            public String toString() {
                return "next " + key;
            }
        };
    }
    
    static <T> ValueSource<T> getKey(String key) {
        return ValueGenerator.getKey(key);
    }
    
    interface FactoryMap {
        
        default <T> ValueGenerator<T> valueGenerator(String key) {
            ValueGenerator<T> gen = peekValueGenerator(key);
            if (gen != null) return gen;
            throw new IllegalArgumentException(key);
        }
        
        <T> ValueGenerator peekValueGenerator(String key);
    }
}
