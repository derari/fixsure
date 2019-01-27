package org.cthul.fixsure.factory;

import java.util.function.*;
import org.cthul.fixsure.*;
import static org.cthul.fixsure.factory.DefaultFactories.anonymousKey;
import static org.cthul.fixsure.factory.DefaultFactories.defaultSetter;
import static org.cthul.fixsure.factory.DefaultFactories.uniqueIdStr;
import org.cthul.fixsure.factory.DefaultNewFactory.NewInstance;
import org.cthul.fixsure.factory.Factory.Include;
import org.cthul.fixsure.factory.ValueGenerator.ValueMap;
import org.cthul.fixsure.fluents.BiDataSource;
import org.cthul.fixsure.fluents.BiDataSource.Pair;

/**
 * Builds a {@link Factories} instance.
 */
public interface FactoriesSetup {

    /**
     * Adds a data source.
     * The data can be accessed by the given key.
     * If the factories are reset, {@link DataSource#toGenerator() toGenerator()}
     * will be called again to obtain a new generator.
     * Thus it is recommended to pass a {@link Template} instead of a 
     * {@link Generator}, but not required.
     * @param key
     * @param dataSource
     * @return this
     */
    FactoriesSetup add(String key, DataSource<?> dataSource);
    
    /**
     * Adds a supplier as a data source.
     * @param key
     * @param dataSupplier
     * @return this
     * @see #add(java.lang.String, org.cthul.fixsure.DataSource) 
     */
    @SuppressWarnings("unchecked")
    default FactoriesSetup add(String key, Supplier<? extends DataSource<?>> dataSupplier) {
        DataSource<?> ds = () -> (Generator) dataSupplier.get().toGenerator();
        return add(key, ds);
    }

    /**
     * Adds a data source.
     * The data can be accessed by the given key.
     * If the factories are reset, {@link DataSource#toGenerator() toGenerator()}
     * will be called again to obtain a new generator.
     * Thus it is recommended to pass a {@link Template} instead of a 
     * {@link Generator}, but not required.
     * @param <T>
     * @param token
     * @param dataSource
     * @return this
     */
    default <T> FactoriesSetup add(Typed<T> token, DataSource<? extends T> dataSource) {
        return add(token.toString(), dataSource);
    }
    
    /**
     * Adds a supplier as a data source.
     * @param <T>
     * @param token
     * @param dataSupplier
     * @return this
     * @see #add(java.lang.String, org.cthul.fixsure.DataSource) 
     */
    default <T> FactoriesSetup add(Typed<T> token, Supplier<? extends DataSource<? extends T>> dataSupplier) {
        return add(token.toString(), dataSupplier);
    }
    
    FactoriesSetup add(String key, Factory<?> factory);
    
    /**
     * Builds a new factory for instances of {@code clazz}
     * @param <T>
     * @param key
     * @param clazz
     * @return new factory setup
     */
    <T> NewFactory<T> newFactory(String key, Class<T> clazz);
    
    /**
     * Builds a new factory for instances of {@code clazz}
     * @param <T>
     * @param clazz
     * @return new factory setup
     */
    default <T> NewFactory<T> newFactory(Class<T> clazz) {
        return newFactory(clazz.getName(), clazz);
    }
    
    /**
     * Builds a new factory for instances of {@code clazz}
     * @param <T>
     * @param token
     * @param clazz
     * @return new factory setup
     */
    default <T> NewFactory<T> newFactory(Typed<T> token, Class<T> clazz) {
        return newFactory(token.toString(), clazz);
    }
    
    /**
     * Builds a new factory for instances of {@code clazz}
     * @param <T>
     * @param token
     * @return new factory setup
     */
    default <T> NewFactory<T> newFactory(Typed<T> token) {
        return newFactory(token.toString(), token.getValueType());
    }
    
    default <T> NewBuilder<T,?> newFactory(String key) {
        return newFactory(key, (Class<T>) null);
    }
    
    /**
     * Returns the {@link Factories} instance.
     * @return factories
     */
    Factories toFactories();
    
    /**
     * Base interface for setting up a value mapping.
     * @param <B>
     * @param <This> 
     * @see BuilderSetupBase
     * @see NewBuilder
     */
    interface ValueSetupBase<B,This extends ValueSetupBase<B,This>> extends Typed<B> {
        
        /**
         * Assigns a value to the value map, but does not modify the object.
         * @param <T>
         * @param key
         * @return value declaration
         */
        default <T> ValueDeclaration<T, ? extends This> assign(String key) {
            return new ValueDeclaration<T, This>() {
                @Override
                public This toValuesOf(ValueSource<? extends T> valueSource) {
                    return ValueSetupBase.this.assignValues(key, valueSource);
                }
                @Override
                public This include(Include<? extends T> include) {
                    return ValueSetupBase.this.include(key, include);
                }
            };
        }
        
        /**
         * Assigns two values from a {@link BiDataSource} to the value map.
         * @param id1
         * @param id2
         * @param dataSource
         * @return this
         */
        default This assign(String id1, String id2, BiDataSource<?, ?> dataSource) {
            return assign(id1, id2).to(dataSource.pairs());
        }
        
        /**
         * Assigns two values from a pair to the value map.
         * @param id1
         * @param id2
         * @return value declaration
         */
        default ValueDeclaration<Pair<?,?>, ? extends This> assign(String id1, String id2) {
            String pairId = "(" + id1 + ";" + id2 + ")@" + uniqueIdStr();
            return assign(id1).to(vm -> vm.<Pair>get(pairId).v1())
                    .assign(id2).to(vm -> vm.<Pair>get(pairId).v2())
                    .assign(pairId);
        }
        
        /**
         * Assigns a value to the value map, but does not modify the object.
         * @param <T>
         * @param token
         * @return value declaration
         */
        default <T> ValueDeclaration<T, ? extends This> assign(Typed<T> token) {
            return assign(token.toString());
        }
        
        /**
         * Assigns two values from a {@link BiDataSource} to the value map.
         * @param <T>
         * @param <U>
         * @param token1
         * @param token2
         * @param dataSource
         * @return this
         */
        default <T,U> This assign(Typed<T> token1, Typed<U> token2, BiDataSource<? extends T, ? extends U> dataSource) {
            return assign(token1.toString(), token2.toString(), dataSource);
        }
        
        /**
         * Assigns two values from a pair to the value map.
         * @param <T>
         * @param <U>
         * @param token1
         * @param token2
         * @return value declaration
         */
        @SuppressWarnings("unchecked")
        default <T,U> ValueDeclaration<Pair<T,U>, ? extends This> assign(Typed<T> token1, Typed<U> token2) {
            return (ValueDeclaration) assign(token1.toString(), token2.toString());
        }
        
        This assignValues(String key, ValueSource<?> valueSource);
        
        This include(String key, Include<?> include);
        
        default This include(String key, Factory<?> factory) {
            return include(key, factory.generate().asInclude());
        }
        
        default This include(String key, String factoryKey) {
            return include(key, (Include<?>) null);
        }
    }
    
    /**
     * Base interface for setting up a {@link BuilderSetup builder} or
     * {@link FactorySetup factory}.
     * See {@link FactorySetup} for details
     * @param <B>
     * @param <This> 
     * @see FactoriesSetup
     */
    interface BuilderSetupBase<B,This extends BuilderSetupBase<B,This>> 
                        extends ValueSetupBase<B, This> {
        
        /**
         * Applies values and replaces the object under construction with the
         * function result.
         * @param function
         * @return this
         */
        This applyValues(BiFunction<? super B, ? super ValueMap, ? extends B> function);
        
        /**
         * Sets the given field via reflection.
         * @param key
         * @return value declaration
         */
        default ValueDeclaration<Object, This> set(String key) {
            return set(key, defaultSetter(getValueType(), key));
        }
        
        /**
         * Sets a value using a setter.
         * @param <T>
         * @param key
         * @param setter
         * @return value declaration
         */
        @SuppressWarnings("unchecked")
        default <T> ValueDeclaration<T, This> set(String key, BiConsumer<? super B, ? super T> setter) {
            // in case of NewFactory: self != this
            This self = applyValues((b, v) -> { setter.accept(b, v.<T>get(key)); return b;});
            return (ValueDeclaration) self.assign(key);
        }
        
        /**
         * Sets the given field via reflection.
         * @param <T>
         * @param token
         * @return value declaration
         */
        @SuppressWarnings("unchecked")
        default <T> ValueDeclaration<T, This> set(Typed<T> token) {
            return (ValueDeclaration) set(token.toString());
        }
        
        /**
         * Sets a value using a setter.
         * @param <T>
         * @param token
         * @param setter
         * @return value declaration
         */
        default <T> ValueDeclaration<T, This> set(Typed<T> token, BiConsumer<? super B, ? super T> setter) {
            return set(token.toString(), setter);
        }
        
        /**
         * Applies a value and replaces the object under construction with the
         * function result.
         * @param <T>
         * @param key
         * @param function
         * @return value declaration
         */
        @SuppressWarnings("unchecked")
        default <T> ValueDeclaration<T, This> apply(String key, BiFunction<? super B, T, ? extends B> function) {
            // in case of NewFactory: self != this
            This self = applyValues((b, v) -> function.apply(b, v.<T>get(key)));
            return (ValueDeclaration) self.assign(key);
        }
        
        /**
         * Applies a value and replaces the object under construction with the
         * function result.
         * @param <T>
         * @param token
         * @param function
         * @return value declaration
         */
        default <T> ValueDeclaration<T, This> apply(Typed<T> token, BiFunction<? super B, T, ? extends B> function) {
            return apply(token.toString(), function);
        }
        
        /**
         * Sets an anonymous value using a setter.
         * @param <T>
         * @param setter
         * @return value declaration
         */
        default <T> ValueDeclaration<T, This> set(BiConsumer<? super B, T> setter) {
            return set(anonymousKey(), setter);
        }
        
        /**
         * Applies an anonymous value and replaces the object under construction 
         * with the function result.
         * @param <T>
         * @param setter
         * @return value declaration
         */
        default <T> ValueDeclaration<T, This> apply(BiFunction<? super B, T, ? extends B> setter) {
            return apply(anonymousKey(), setter);
        }
        
        /**
         * Passes the constructed object to a consumer.
         * @param action
         * @return this
         */
        default This then(Consumer<? super B> action) {
            return applyValues((b, v) -> { action.accept(b); return b;});
        }
        
        /**
         * Passes the constructed object and the value map to a consumer.
         * @param action
         * @return this
         */
        default This then(BiConsumer<? super B, ? super ValueMap> action) {
            return applyValues((b, v) -> { action.accept(b, v); return b;});
        }
        
        /**
         * Replaces the object under construction with the function result.
         * @param action
         * @return this
         */
        default This thenApply(Function<? super B, ? extends B> action) {
            return applyValues((b, v) -> action.apply(b));
        }
        
        /**
         * Replaces the object under construction with the function result.
         * @param action
         * @return this
         */
        default This thenApply(BiFunction<? super B, ? super ValueMap, ? extends B> action) {
            return applyValues(action);
        }
    }
    
    /**
     * Sets up a builder for the actual object.
     * One of the {@code build} methods must be called to complete this factory.
     * @param <B>
     * @param <R> 
     */
    interface BuilderSetup<B, R> extends BuilderSetupBase<B, BuilderSetup<B,R>> {
        
        /**
         * Creates the actual object.
         * Additional steps can be performed on the object by using 
         * the {@link FactorySetup} returned by this call.
         * @param buildFunction
         * @return factory setup
         */
        default FactorySetup<R> build(Function<? super B, ? extends R> buildFunction) {
            return build((b, vm) -> buildFunction.apply(b));
        }
        
        /**
         * Creates the actual object.
         * Additional steps can be performed on the object by using 
         * the {@link FactorySetup} returned by this call.
         * @param buildFunction
         * @return factory setup
         */
        FactorySetup<R> build(BiFunction<? super B, ? super ValueMap, ? extends R> buildFunction);

        @Override
        default <T> BuilderValueSetup<B,R,T> assign(String key) {
            return new BuilderValueSetup<B, R, T>() {
                @Override
                public BuilderSetup<B, R> builderSetup() {
                    return BuilderSetup.this;
                }
                @Override
                public BuilderSetup<B, R> toValuesOf(ValueSource<? extends T> valueSource) {
                    return BuilderSetup.this.assignValues(key, valueSource);
                }
                @Override
                public BuilderSetup<B, R> include(Include<? extends T> include) {
                    return BuilderSetup.this.include(key, include);
                }
            };
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default BuilderValueSetup<B,R,Object> set(String key) {
            return (BuilderValueSetup) BuilderSetupBase.super.set(key);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> BuilderValueSetup<B,R,T> set(String key, BiConsumer<? super B, ? super T> setter) {
            return (BuilderValueSetup) BuilderSetupBase.super.set(key, setter);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> BuilderValueSetup<B,R,T> apply(String key, BiFunction<? super B, T, ? extends B> setter) {
            return (BuilderValueSetup) BuilderSetupBase.super.apply(key, setter);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> BuilderValueSetup<B,R,T> set(Typed<T> token) {
            return (BuilderValueSetup) BuilderSetupBase.super.set(token);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> BuilderValueSetup<B,R,T> set(Typed<T> token, BiConsumer<? super B, ? super T> setter) {
            return (BuilderValueSetup) BuilderSetupBase.super.set(token, setter);
        }
        
        @Override
        default <T> BuilderValueSetup<B,R,T> apply(Typed<T> token, BiFunction<? super B, T, ? extends B> setter) {
            return (BuilderValueSetup) BuilderSetupBase.super.apply(token, setter);
        }
    }
        
    /**
     * Configures a factory.
     * <p>
     * 
     * A factory will apply steps to initialize an object.
     * Each step is associated with a value.
     * When a step is named, the {@link ValueMap} will contain its value.
     * Steps are always applied in order, but the computation of values can
     * occur lazily in any order.
     * @param <R> 
     */
    interface FactorySetup<R> extends BuilderSetupBase<R, FactorySetup<R>>, FactoriesSetup {
        
        FactoriesSetup factoriesSetup();
        
        @Override
        default FactoriesSetup add(String key, DataSource<?> dataSource) {
            return factoriesSetup().add(key, dataSource);
        }

        @Override
        default FactoriesSetup add(String key, Factory<?> factory) {
            return factoriesSetup().add(key, factory);
        }
        
        @Override
        default <T> NewFactory<T> newFactory(String key, Class<T> clazz) {
            return factoriesSetup().newFactory(key, clazz);
        }
        
        @Override
        default Factories toFactories() {
            return factoriesSetup().toFactories();
        }

        @Override
        default <T> ValueDeclaration<T, ? extends FactorySetup<R>> assign(String key) {
            // don't change the return type of `assign` methods, 
            // the extended is only needed for `set` methods
            return new FactoryValueSetup<R, T>() {
                @Override
                public FactorySetup<R> factorySetup() {
                    return FactorySetup.this;
                }
                @Override
                public FactorySetup<R> toValuesOf(ValueSource<? extends T> valueSource) {
                    return FactorySetup.this.assignValues(key, valueSource);
                }
                @Override
                public FactorySetup<R> include(Include<? extends T> include) {
                    return FactorySetup.this.include(key, include);
                }
            };
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default FactoryValueSetup<R,Object> set(String key) {
            return (FactoryValueSetup) BuilderSetupBase.super.set(key);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> FactoryValueSetup<R,T> set(String key, BiConsumer<? super R, ? super T> setter) {
            return (FactoryValueSetup) BuilderSetupBase.super.set(key, setter);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> FactoryValueSetup<R,T> set(Typed<T> token) {
            return (FactoryValueSetup) BuilderSetupBase.super.set(token);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> FactoryValueSetup<R,T> set(Typed<T> token, BiConsumer<? super R, ? super T> setter) {
            return (FactoryValueSetup) BuilderSetupBase.super.set(token, setter);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> FactoryValueSetup<R,T> apply(String key, BiFunction<? super R, T, ? extends R> setter) {
            return (FactoryValueSetup) BuilderSetupBase.super.apply(key, setter);
        }
        
        @Override
        @SuppressWarnings("unchecked")
        default <T> FactoryValueSetup<R,T> apply(Typed<T> token, BiFunction<? super R, T, ? extends R> setter) {
            return (FactoryValueSetup) BuilderSetupBase.super.apply(token, setter);
        }
    }
    
    /**
     * Prepares a factory that requires a builder.
     * @param <R>
     * @param <This> 
     */
    interface NewBuilder<R, This extends ValueSetupBase<R, This>> extends ValueSetupBase<R, This> {
        
        /**
         * The factory will get new instances from the supplier.
         * @param builder
         * @return factory setup
         */
        default FactorySetup<R> build(Supplier<? extends R> builder) {
            return build(vm -> builder.get());
        }
        
        /**
         * The factory will get builder instances from the supplier.
         * @param <B>
         * @param newBuilder
         * @return builder setup
         */
        default <B> BuilderSetup<B,R> with(Supplier<? extends B> newBuilder) {
            return builder(vm -> newBuilder.get());
        }
        
        /**
         * The factory will obtain new instances from the function.
         * @param builder
         * @return factory setup
         */
        default FactorySetup<R> build(Function<? super ValueMap, ? extends R> builder) {
            return builder(builder).build(Function.identity());
        }
        
        /**
         * The factory will obtain builder instances from the factory
         * @param <B>
         * @param newBuilder
         * @return builder setup
         */
        <B> BuilderSetup<B,R> builder(Function<? super ValueMap, ? extends B> newBuilder);
        
        FactorySetup<R> extend(Factory<R> factory);
        
        FactorySetup<R> extend(String key);
        
        default FactorySetup<R> extend(Class<R> clazz) {
            return extend(clazz.getName());
        }
    }
    
    /**
     * Represents a newly declared factory.
     * Use one of the {@code with} methods to use a builder,
     * one of the {@code build} methods to define how new instances are create,
     * or add values directly (then new instances will be created from the 
     * default constructor).
     * @param <R> 
     */
    interface NewFactory<R> extends FactorySetup<R>, NewBuilder<R, FactorySetup<R>> {
        
        @Override
        default FactoriesSetup factoriesSetup() {
            return useDefaultConstructor().factoriesSetup();
        }
        
        default FactorySetup<R> useDefaultConstructor() {
            return build(new NewInstance<>(getValueType()));
        }

        @Override
        default FactorySetup<R> applyValues(BiFunction<? super R, ? super ValueMap, ? extends R> function) {
            return useDefaultConstructor().applyValues(function);
        }

        @Override
        @SuppressWarnings("unchecked")
        default <T> ValueDeclaration<T, NewFactory<R>> assign(String key) {
            return (ValueDeclaration) FactorySetup.super.assign(key);
        }

        @Override
        @SuppressWarnings("unchecked")
        default <T> ValueDeclaration<T, NewFactory<R>> assign(Typed<T> token) {
            return (ValueDeclaration) FactorySetup.super.assign(token);
        }

        @Override
        @SuppressWarnings("unchecked")
        default ValueDeclaration<Pair<?, ?>, NewFactory<R>> assign(String id1, String id2) {
            return (ValueDeclaration) FactorySetup.super.assign(id1, id2);
        }

        @Override
        NewFactory<R> assignValues(String key, ValueSource<?> valueSource);

        @Override
        @SuppressWarnings("unchecked")
        default NewFactory<R> assign(String id1, String id2, BiDataSource<?, ?> dataSource) {
            return (NewFactory) FactorySetup.super.assign(id1, id2, dataSource);
        }

        @Override
        @SuppressWarnings("unchecked")
        default <T,U> ValueDeclaration<Pair<T, U>, NewFactory<R>> assign(Typed<T> token1, Typed<U> token2) {
            return (ValueDeclaration) assign(token1.toString(), token2.toString());
        }

        @Override
        default <T,U> NewFactory<R> assign(Typed<T> token1, Typed<U> token2, BiDataSource<? extends T, ? extends U> dataSource) {
            return assign(token1.toString(), token2.toString(), dataSource);
        }

        @Override
        NewFactory<R> include(String key, Include<?> include);

        @Override
        default NewFactory<R> include(String key, Factory<?> factory) {
            return (NewFactory) FactorySetup.super.include(key, factory);
        }

        @Override
        @SuppressWarnings("unchecked")
        default NewFactory<R> include(String key, String factoryKey) {
            return (NewFactory) FactorySetup.super.include(key, factoryKey);
        }
    }
    
    /**
     * Defines how to compute values for the {@link ValueMap}.
     * @param <T>
     * @param <BuilderSetup> 
     */
    interface ValueDeclaration<T, BuilderSetup> {
        
        BuilderSetup toValuesOf(ValueSource<? extends T> valueSource);
        
        /**
         * Use the function to obtain the value.
         * @param valueGenerator
         * @return parent setup
         */
        default BuilderSetup toValues(ValueGenerator<? extends T> valueGenerator) {
            return toValuesOf(ValueSource.fromGenerator(valueGenerator));
        }
        
        /**
         * Use the function to obtain the value.
         * @param valueFunction
         * @return parent setup
         */
        default BuilderSetup to(Function<? super ValueMap, ? extends T> valueFunction) {
            return toValuesOf(ValueSource.fromFunction(valueFunction));
        }
        
        /**
         * Use the data source to obtain values.
         * The generator will be reused until the factory is reset.
         * @param dataSource
         * @return parent setup
         */
        default BuilderSetup to(DataSource<? extends T> dataSource) {
            return toValuesOf(ValueSource.fromDataSource(dataSource));
        }
        
        /**
         * Use a supplier as a data source to obtain values.
         * @param dataSupplier
         * @return parent setup
         */
        default BuilderSetup to(Supplier<? extends DataSource<? extends T>> dataSupplier) {
            return toValuesOf(ValueSource.fromDataSupply(dataSupplier));
        }
        
        /**
         * Alway uses the given value.
         * @param value
         * @return parent setup
         */
        default BuilderSetup to(T value) {
            return toValuesOf(ValueSource.constant(value));
        }
        
        /**
         * Use the next value returned by the referenced generator or factory.
         * @param key
         * @return parent setup
         */
        default BuilderSetup toNext(String key) {
            return toValuesOf(ValueSource.nextKey(key));
        }
        
        /**
         * Use the next value returned by the referenced generator or factory.
         * @param clazz
         * @return parent setup
         */
        default BuilderSetup toNext(Class<?> clazz) {
            return toNext(clazz.getName());
        }
        
        /**
         * Use the next value returned by the given generator.
         * @param generator
         * @return parent setup
         */
        default BuilderSetup toNext(Generator<? extends T> generator) {
            return to(generator);
        }
        
        /**
         * Use the next value by the given sequence.
         * When the factories are reset, the sequence counter will reset too.
         * @param sequence
         * @return parent setup
         */
        default BuilderSetup toNext(LongFunction<? extends T> sequence) {
            return to(Sequence.sequence(Sequence.L_UNBOUNDED, sequence));
        }
        
        /**
         * Use the given value from the {@link ValueMap}
         * @param key
         * @return parent setup
         */
        default BuilderSetup toValueOf(String key) {
            return toValuesOf(ValueSource.getKey(key));
        }
        
        BuilderSetup include(Include<? extends T> include);
        
        default BuilderSetup include(Factory<? extends T> factory) {
            return include(factory.generate().asInclude());
        }
        
        default BuilderSetup include(String key) {
            return include((Include<T>) null);
        }
    }
    
    /**
     * An optional value declaration of a {@link BuilderSetup}.
     * If no value is given (ie., if a {@link BuilderSetup} method is called 
     * on this object), the generator of the respective key will be used.
     * @param <B>
     * @param <R>
     * @param <T> 
     */
    interface BuilderValueSetup<B,R,T> 
                    extends ValueDeclaration<T, BuilderSetup<B,R>>, 
                            BuilderSetup<B,R> {
        
        BuilderSetup<B,R> builderSetup();

        @Override
        default FactorySetup<R> build(BiFunction<? super B, ? super ValueMap, ? extends R> buildFunction) {
            return builderSetup().build(buildFunction);
        }

        @Override
        public default BuilderSetup<B, R> applyValues(BiFunction<? super B, ? super ValueMap, ? extends B> function) {
            return builderSetup().applyValues(function);
        }

        @Override
        public default BuilderSetup<B, R> assignValues(String key, ValueSource<?> valueSource) {
            return builderSetup().assignValues(key, valueSource);
        }

        @Override
        default BuilderSetup<B, R> include(String key, Include<?> include) {
            return builderSetup().include(key, include);
        }

        @Override
        default Class<B> getValueType() {
            return builderSetup().getValueType();
        }
    }
    
    /**
     * An optional value declaration of a {@link FactorySetup}.
     * If no value is given (ie., if a {@link FactorySetup} method is called 
     * on this object), the generator of the respective key will be used.
     * @param <R>
     * @param <T> 
     */
    interface FactoryValueSetup<R, T> 
                    extends ValueDeclaration<T, FactorySetup<R>>, 
                            FactorySetup<R> {

        FactorySetup<R> factorySetup();

        @Override
        default FactoriesSetup factoriesSetup() {
            return factorySetup().factoriesSetup();
        }

        @Override
        default <T> ValueDeclaration<T, ? extends FactorySetup<R>> assign(String key) {
            return factorySetup().assign(key);
        }
        
        @Override
        default FactorySetup<R> applyValues(BiFunction<? super R, ? super ValueMap, ? extends R> function) {
            return factorySetup().applyValues(function);
        }

        @Override
        default Class<R> getValueType() {
            return factorySetup().getValueType();
        }

        @Override
        default FactorySetup<R> assignValues(String key, ValueSource<?> valueSource) {
            return factorySetup().assignValues(key, valueSource);
        }

        @Override
        default FactorySetup<R> include(String key, Include<?> include) {
            return factorySetup().include(key, include);
        }
    }
}
