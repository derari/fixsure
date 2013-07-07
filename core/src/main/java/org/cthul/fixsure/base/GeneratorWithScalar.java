package org.cthul.fixsure.base;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.generators.primitive.IntegersGenerator;

/**
 * A {@link Generator} that requires a scalar value (e.g., to generate 
 * strings with a certain length).
 * <p>
 * The value can be specified as a constant, or as an integer generator.
 */
public abstract class GeneratorWithScalar<T> extends GeneratorBase<T> {
    
    private final int scalar;
    private final Generator<Integer> scalarGenerator;

    public GeneratorWithScalar(int scalar) {
        this.scalar = scalar;
        this.scalarGenerator = null;
    }

    public GeneratorWithScalar(Generator<Integer> scalarGenerator) {
        this.scalar = -1;
        this.scalarGenerator = scalarGenerator;
    }
    
    public GeneratorWithScalar(int scalar, Distribution distribution) {
        this(IntegersGenerator.integers(scalar, distribution));
    }

    public GeneratorWithScalar(GeneratorWithScalar src) {
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
