package org.cthul.fixsure.base;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Fetcher;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.generators.primitive.IntegersGenerator;

/**
 * A {@link Fetcher} that requires a scalar value.
 * <p>
 * The value can be specified as a constant, a range, or as an integer generator.
 */
public abstract class FetcherWithScalar extends FetcherBase {
    
    private final int scalar;
    private final Generator<Integer> scalarGenerator;

    public FetcherWithScalar(int scalar) {
        this.scalar = scalar;
        this.scalarGenerator = null;
    }

    public FetcherWithScalar(Generator<Integer> scalarGenerator) {
        this.scalar = -1;
        this.scalarGenerator = scalarGenerator;
    }
    
    public FetcherWithScalar(int scalar, Distribution distribution) {
        this(IntegersGenerator.integers(scalar, distribution));
    }
    
    public FetcherWithScalar(int min, int max) {
        this(IntegersGenerator.integers(min, max+1));
    }
    
    public FetcherWithScalar(int min, int max, Distribution distribution) {
        this(IntegersGenerator.integers(min, max+1, distribution));
    }

    protected FetcherWithScalar(FetcherWithScalar src) {
        this.scalar = src.scalar;
        this.scalarGenerator = src.scalarGenerator;
    }
    
    protected int nextScalar() {
        if (scalarGenerator != null) {
            return scalarGenerator.next();
        } else {
            return scalar;
        }
    }
    
}
