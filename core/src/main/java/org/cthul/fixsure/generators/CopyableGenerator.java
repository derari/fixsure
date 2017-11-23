package org.cthul.fixsure.generators;

import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public interface CopyableGenerator<T> extends FlGenerator<T> {
    
    CopyableGenerator<T> copy();
    
//    default FlTemplate<T> asTemplate() {
//        return () -> Generator.generate(copy());
//    }

    @Override
    default FlTemplate<T> snapshot() {
        CopyableGenerator<T> proto = copy();
        return () -> proto.copy();
    }
}
