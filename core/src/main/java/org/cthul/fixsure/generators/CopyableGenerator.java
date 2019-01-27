package org.cthul.fixsure.generators;

import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public interface CopyableGenerator<T> extends FlGenerator<T> {
    
    CopyableGenerator<T> copy();
    
    @Override
    default FlTemplate<T> snapshot() {
        CopyableGenerator<T> prototype = copy();
        return prototype::copy;
    }
}
