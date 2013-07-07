package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Sequence;

/**
 *
 */
public interface FlSequence<T> extends Sequence<T>, FlGenerator<T> {
    
    <T2> FlSequence<T2> alternateWith(Sequence<? extends T2>... sequences);
    
    FlGenerator<T> shuffle();
    
    FlGenerator<T> random();
    
}
