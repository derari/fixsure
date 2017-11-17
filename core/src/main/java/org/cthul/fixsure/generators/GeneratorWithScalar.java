package org.cthul.fixsure.generators;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.fluents.FlGenerator;
import org.cthul.fixsure.generators.primitives.IntegersGenerator;

/**
 * A {@link Generator} that requires a scalar value (e.g., to generate 
 * strings with a certain length).
 * <p>
 * The value can be specified as a constant, or as an integer generator.
 */
public abstract class GeneratorWithScalar<T> implements FlGenerator<T> {
    
    private final int scalar;
    private final Generator<Integer> scalarGenerator;

    public GeneratorWithScalar(int scalar) {
        this.scalar = scalar;
        this.scalarGenerator = null;
    }

    public GeneratorWithScalar(DataSource<Integer> scalarGenerator) {
        this.scalar = -1;
        this.scalarGenerator = scalarGenerator.toGenerator();
    }
    
    public GeneratorWithScalar(int scalar, Distribution distribution) {
        this(IntegersGenerator.integers(scalar, distribution));
    }

    protected GeneratorWithScalar(GeneratorWithScalar src) {
        this.scalar = src.scalar;
        this.scalarGenerator = src.scalarGenerator;
    }
    
    /**
     * Returns next scalar.
     * @return scalar
     */
    protected int nextScalar() {
        if (scalarGenerator != null) {
            return scalarGenerator.next();
        } else {
            return scalar;
        }
    }
}
