package org.cthul.fixsure.fetchers;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.generators.primitives.RandomIntegersGenerator;

/**
 * A {@link Fetcher} that requires a scalar value.
 * <p>
 * The value can be specified as a constant, a range, or as an integer generator.
 */
public abstract class FetcherWithScalar extends AbstractFetcher {
    
    private final int scalar;
    private final Generator<Integer> scalarGenerator;

    public FetcherWithScalar(int scalar) {
        this.scalar = scalar;
        this.scalarGenerator = null;
    }

    public FetcherWithScalar(DataSource<Integer> scalarGenerator) {
        this.scalar = -1;
        this.scalarGenerator = scalarGenerator.toGenerator();
    }
    
    public FetcherWithScalar(int scalar, Distribution distribution) {
        this(RandomIntegersGenerator.integers(scalar, distribution));
    }
    
    public FetcherWithScalar(int min, int max) {
        this(RandomIntegersGenerator.integers(min, max+1));
    }
    
    public FetcherWithScalar(int min, int max, Distribution distribution) {
        this(RandomIntegersGenerator.integers(min, max+1, distribution));
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

    @Override
    public StringBuilder toString(StringBuilder sb) {
        if (scalarGenerator != null) {
            return scalarGenerator.toString(sb);
        } else {
            return sb.append(scalar);
        }
    }
}
