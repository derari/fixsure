package org.cthul.fixsure.generators;

import java.util.function.Supplier;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public interface CopyableGenerator<T> extends FlGenerator<T> {
    
    Supplier<T> copy();
    
    default FlTemplate<T> asTemplate() {
        return () -> Generator.generate(copy());
    }

    @Override
    default FlTemplate<T> snapshot() {
        CopyableGenerator<T> proto = (CopyableGenerator<T>) asTemplate().newGenerator();
        return proto.asTemplate();
    }
}
