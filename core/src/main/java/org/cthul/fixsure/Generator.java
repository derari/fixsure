package org.cthul.fixsure;

import java.util.function.Supplier;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.CopyableGenerator;
import static org.cthul.fixsure.generators.GeneratorTools.copyGenerator;

/**
 * Endlessly produces values.
 * <p>
 * If the generator cannot continue, throws {@link GeneratorException}.
 * @param <T> value type
 */
@FunctionalInterface
public interface Generator<T> extends Supplier<T>, DataSource<T> {
    
    /**
     * Produces next value.
     * @return next value
     * @throws GeneratorException if no more values can be produced
     */
    T next();

    /**
     * @return next
     * @deprecated use #next()
     * @see #next()
     */
    @Override 
    @Deprecated
    default T get() {
        return next();
    }

    /**
     * @return this
     * @deprecated redundant operation
     */
    @Override
    @Deprecated
    default Generator<T> toGenerator() {
        return this;
    }
    
    @Override
    default FlGenerator<T> fluentData() {
        return Generator.generate(this);
    }
    
    /**
     * Converts a supplier into a generator.
     * @param <T>
     * @param supplier
     * @return generator
     */
    @Factory
    static <T> FlGenerator<T> generate(Supplier<T> supplier) {
        if (supplier instanceof FlGenerator) {
            return (FlGenerator<T>) supplier;
        }
        if (supplier instanceof Typed) {
            return generate(((Typed) supplier).getValueType(), supplier);
        }
        return supplier::get;
    }
    
    /**
     * Converts a supplier into a typed generator.
     * @param <T>
     * @param clazz
     * @param supplier
     * @return generator
     */
    @Factory
    static <T> FlGenerator<T> generate(Class<T> clazz, Supplier<T> supplier) {
        Generator<T> actual;
        if (supplier instanceof Generator && clazz == Typed.typeOf(supplier)) {
            actual = (Generator) supplier;
        } else {
            actual = null;
        }
        class TypedGenerator implements CopyableGenerator<T>, Typed<T> {
            @Override
            public T next() {
                return supplier.get();
            }
            @Override
            public Class<T> getValueType() {
                return clazz;
            }
            @Override
            public Supplier<T> copy() {
                return generate(clazz, copyGenerator(supplier));
            }
            @Override
            public Generator<T> toGenerator() {
                return actual != null ? actual : this;
            }
        }
        return new TypedGenerator();
    }
}
