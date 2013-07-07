package org.cthul.fixsure.fluents;

import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;

/**
 *
 */
public interface FlFetcher extends Fetcher {
    
    @Override
    <T> FlValues<T> of(Generator<T> generator);
    
    @Override
    <T> FlValues<T> ofEach(Generator<? extends T>... generators);
}
