package org.cthul.fixsure.factory;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import static org.cthul.fixsure.factory.DefaultFactories.uniqueIdStr;
import org.cthul.fixsure.factory.Factory.Include;
import org.cthul.fixsure.factory.ValueGenerator.ValueMap;

/**
 *
 * @param <R>
 */
public class DefaultNewFactory<R> implements FactoriesSetup.NewFactory<R> {
    
    private final DefaultFactories.Setup factories;
    private final DefaultFactory.Setup<R> factorySetup;

    public DefaultNewFactory(DefaultFactories.Setup setup, String key, Class<R> clazz) {
        this.factories = setup;
        this.factorySetup = new DefaultFactory.Setup<>(factories, factories.toFactories(), key, clazz);
    }

    @Override
    public FactoriesSetup factoriesSetup() {
        return factories;
    }

    @Override
    public Class<R> getValueType() {
        return factorySetup.getValueType();
    }

    @Override
    public NewFactory<R> assignValues(String key, ValueSource<?> valueSource) {
        factorySetup.assignValues(key, valueSource);
        return this;
    }

    @Override
    public NewFactory<R> include(String key, Include<?> include) {
        factorySetup.include(key, include);
        return this;
    }

    @Override
    public <B> BuilderSetup<B, R> builder(Function<? super ValueMap, ? extends B> newBuilder) {
        String key = factorySetup.toString() + "/builder@" + uniqueIdStr();
        return new Builder<>(key, newBuilder);
    }

    @Override
    public FactorySetup<R> build(Supplier<? extends R> builder) {
        factorySetup.applyValues((v,vm) -> builder.get());
        return factorySetup;
    }
    
    @Override
    public FactorySetup<R> build(Function<? super ValueMap, ? extends R> builder) {
        factorySetup.applyValues((v,vm) -> builder.apply(vm));
        return factorySetup;
    }

    @Override
    public FactorySetup<R> extend(Factory<R> factory) {
        return extend("@extend", factory);
    }

    @Override
    public FactorySetup<R> extend(String key) {
        Factory<?> f = factories.toFactories().factory(key);
        return extend("@extend/" + key, f);
    }
    
    public FactorySetup<R> extend(String key, Factory<?> factory) {
        String uniqKey = key + "@" + uniqueIdStr();
        factorySetup.assign(uniqKey).include(factory);
        factorySetup.applyValues((v, vm) -> vm.get(uniqKey));
        return factorySetup;
    }
    
    class Builder<B> implements BuilderSetup<B, R> {
        
        private final String key;
        private final DefaultFactory.Setup<B> builderSetup;

        public Builder(String key, Function<? super ValueMap, ? extends B> newBuilder) {
            this.key = key;
            builderSetup = new DefaultFactory.Setup<>(factories.toFactories(), key, null);
            builderSetup.applyValues((v,vm) -> newBuilder.apply(vm));
        }

        @Override
        public FactorySetup<R> build(BiFunction<? super B, ? super ValueMap, ? extends R> buildFunction) {
            String theKey = key;
            factorySetup.include(theKey, builderSetup.getFactory());
            factorySetup.applyValues((v, vm) -> buildFunction.apply(vm.get(theKey), vm));
            return factorySetup;
        }

        @Override
        public Class<B> getValueType() {
            return builderSetup.getValueType();
        }

        @Override
        public BuilderSetup<B, R> applyValues(BiFunction<? super B, ? super ValueMap, ? extends B> function) {
            builderSetup.applyValues(function);
            return this;
        }

        @Override
        public BuilderSetup<B, R> assignValues(String key, ValueSource<?> valueSource) {
            builderSetup.assignValues(key, valueSource);
            return this;
        }

        @Override
        public BuilderSetup<B, R> include(String key, Include<?> include) {
            builderSetup.include(key, include);
            return this;
        }
    }
    
    protected static class NewInstance<T> implements Supplier<T> {
        
        private final Constructor<T> constructor;

        public NewInstance(Class<T> clazz) {
            try {
                this.constructor = clazz.getConstructor();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public T get() {
            try {
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
