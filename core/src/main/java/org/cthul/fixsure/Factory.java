package org.cthul.fixsure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Flags methods that create Fixsure objects.
 */
@Target(ElementType.METHOD)
public @interface Factory {
    
}
