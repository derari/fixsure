package org.cthul.fixsure.generators;

import org.cthul.fixsure.api.AbstractStringify;
import org.cthul.fixsure.fluents.FlTemplate;

/**
 *
 */
public abstract class AnonymousTemplate<T> extends AbstractStringify implements FlTemplate<T> {

    @Override
    public StringBuilder toString(StringBuilder sb) {
        return FlTemplate.super.toString(sb);
    }
}
