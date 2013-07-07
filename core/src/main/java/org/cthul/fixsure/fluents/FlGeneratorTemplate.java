package org.cthul.fixsure.fluents;

import org.cthul.fixsure.GeneratorTemplate;

/**
 *
 */
public interface FlGeneratorTemplate<T> extends GeneratorTemplate<T> {
    
    @Override
    FlGenerator<T> newGenerator();
    
    FlGenerator<T> repeat();
    
}
