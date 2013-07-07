package org.cthul.fixsure.base;

import org.cthul.fixsure.Converter;

/**
 * Base class for {@link Converter}s.
 */
public abstract class ConverterBase<In, Out> implements Converter<In, Out> {

    /** {@inheritDoc} */
    @Override
    public abstract Out convert(In value);
    
}
