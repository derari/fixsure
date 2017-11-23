package org.cthul.fixsure;

import org.cthul.fixsure.api.Factory;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 * Creates generators that will always produce the same values.
 */
@FunctionalInterface
public interface Template<T> extends DataSource<T> {
    
    /**
     * Creates a generator.
     * @return generator
     */
    Generator<T> newGenerator();

    /**
     * @return generator
     * @deprecated use #newGenerator
     * @see #newGenerator()
     */
    @Override
    @Deprecated
    default Generator<T> toGenerator() {
        return newGenerator();
    }
    
    @Override
    default FlTemplate<T> fluentData() {
        return () -> newGenerator().fluentData();
    }
    
    /**
     * Returns a template.
     * This method does nothing, it is for convenience when using lambda expression.
     * @param <T>
     * @param template
     * @return template
     */
    @Factory
    static <T> FlTemplate<T> template(FlTemplate<T> template) {
        return template;
    }
}
