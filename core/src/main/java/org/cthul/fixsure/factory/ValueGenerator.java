package org.cthul.fixsure.factory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.Typed;
import org.cthul.fixsure.factory.FactoriesSetup.ValueDeclaration;
import org.cthul.fixsure.factory.ValueSource.FactoryMap;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.AnonymousGenerator;
import org.cthul.fixsure.generators.GeneratorTools;

/**
 *
 */
public interface ValueGenerator<T> extends Typed<T> {

    T next(ValueMap valueMap);

    default ValueGenerator<T> link(FactoryMap factoryMap) {
        return this;
    }
    
    /**
     * If the value generator chooses to ignore the provided {@code valueMap},
     * it should pass its own value map to {@code transform}.
     * @param valueMap
     * @param transform
     * @return value
     */
    default T next(ValueMap valueMap, Function<ValueMap,ValueMap> transform) {
        return next(valueMap);
    }
    
    default<V, B> ValueDeclaration<V, B> set(String key, B builder) {
        throw new IllegalArgumentException(key);
    }

    @Override
    default Class<T> getValueType() {
        return null;
    }
    
    static <T> Src<T> fromFunction(Function<? super ValueMap, ? extends T> function) {
        return new Src<T>() {
            @Override
            public T next(ValueMap valueMap) {
                return function.apply(valueMap);
            }
            @Override
            public Class<T> getValueType() {
                return null;
            }
            @Override
            public String toString() {
                return GeneratorTools.lambdaToString(function, new StringBuilder()).toString();
            }
        };
    }
    
    @SuppressWarnings("unchecked")
    static <T> ValueGenerator<T> fromGenerator(Generator<? extends T> generator) {
        if (generator instanceof VG2G) {
            return ((VG2G) generator).gen;
        }
        return new G2VG<>(generator);
    }
    
    @SuppressWarnings("unchecked")
    static <T> FlGenerator<T> toGenerator(ValueGenerator<T> generator, ValueMap valueMap) {
        if (generator instanceof G2VG) {
            return ((G2VG) generator).generator.fluentData();
        }
        return new VG2G<>(generator, valueMap);
    }
    
    static <T> Src<T> constant(T value) {
        return new Src<T>() {
            @Override
            public T next(ValueMap valueMap) {
                return value;
            }
            @Override
            @SuppressWarnings("unchecked")
            public Class<T> getValueType() {
                return value == null ? null : (Class) value.getClass();
            }
            @Override
            public String toString() {
                return String.valueOf(value);
            }
        };
    }
    
    static <T> ValueGenerator<T> nextKey(FactoryMap factoryMap, String key) {
        class Next<T> extends DefaultFactory<T> {
            final FactoryMap factoryMap;
            final ValueGenerator<T> actual;
            ValueMap valueMap;
            public Next(FactoryMap factoryMap) {
                super(null, key, null);
                this.factoryMap = factoryMap;
                this.actual = factoryMap.valueGenerator(key);
            }
            public Next(FactoryMap factoryMap, DefaultFactory parent) {
                super(parent);
                this.factoryMap = factoryMap;
                this.actual = factoryMap.valueGenerator(key);
            }
            @Override
            protected DefaultFactory<T> copy(FactoryMap factoryMap) {
                return new Next(factoryMap, this);
            }
            @Override
            public T next() {
                return next((ValueMap) null);
            }
            @Override
            protected T next(ValueMap valueMap) {
                this.valueMap = valueMap;
                VM vm = new VM(this);
                return actual.next(vm, vm2 -> {
                    this.valueMap = vm2;
                    return vm;
                });
            }
            @Override
            public <T> ValueGenerator<T> peekGenerator(String key) {
                ValueGenerator<T> gen = super.peekGenerator(key);
                if (gen != null) return gen;
                if (valueMap != null) {
                    return valueMap.peekValueGenerator(key);
                } else {
                    return factoryMap.peekValueGenerator(key);
                }
            }
            @Override
            protected <T, B> ValueDeclaration<T, B> set(String key, B builder) {
                return super.set(key, builder);
            }
        }
        return new Next(factoryMap).asValueGenerator();
    }
    
    static <T> Src<T> getKey(String key) {
        return new Src<T>() {
            @Override
            public T next(ValueMap valueMap) {
                return valueMap.get(key);
            }
            @Override
            public Class<T> getValueType() {
                return null;
            }
        };
    }
    
    interface Src<T> extends ValueGenerator<T>, ValueSource<T> {
        @Override
        default ValueGenerator<? extends T> generate(FactoryMap factoryMap) {
            return this;
        }
        @Override
        default Class<T> getValueType() {
            return null;
        }
    }
    
    class G2VG<T> implements Src {
        final Generator<? extends T> generator;
        public G2VG(Generator<? extends T> generator) {
            this.generator = generator;
        }
        @Override
        public T next(ValueMap valueMap) {
            return generator.next();
        }
        @Override
        public Class<T> getValueType() {
            return GeneratorTools.typeOf(generator);
        }
        @Override
        public String toString() {
            return GeneratorTools.lambdaToString(generator, new StringBuilder()).toString();
        }
    }
    
    class VG2G<T> extends AnonymousGenerator<T> {
        private final ValueGenerator<T> gen;
        private final ValueMap vm;

        public VG2G(ValueGenerator<T> gen, ValueMap vm) {
            this.gen = gen;
            this.vm = vm;
        }

        @Override
        public T next() {
            return gen.next(vm);
        }

        @Override
        public StringBuilder toString(StringBuilder sb) {
            return sb.append(gen);
        }
    }
    
    interface ValueMap extends Factories {
        
        default <T> ValueGenerator<T> valueGenerator(String key) {
            ValueGenerator<T> gen = peekValueGenerator(key);
            if (gen != null) return gen;
            throw new IllegalArgumentException(key);
        }
        
        <T> ValueGenerator<T> peekValueGenerator(String key);
        
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
