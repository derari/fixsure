package org.cthul.fixsure.generators.composite;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorException;
import static org.cthul.fixsure.distributions.DistributionRandomizer.toSeed;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.GeneratorWithDistribution;

/**
 *
 */
public class ShufflingGenerator<T> 
        extends GeneratorWithDistribution<T> 
        implements CopyableGenerator<T> {
    
    private final static long CLASS_SEED = toSeed(ShufflingGenerator.class);
    
    public static <T> ShufflingGenerator<T> shuffle(DataSource<T> source) {
        return new ShufflingGenerator<>(source, 64);
    }
    
    public static <T> ShufflingGenerator<T> shuffle(DataSource<T> source, long seed) {
        return new ShufflingGenerator<>(source, 64, null, seed);
    }
    
    private final Generator<T> source;
    private final Object[] hold;
    private int end = -1;

    public ShufflingGenerator(DataSource<T> source, int hold) {
        this(source, hold, null);
    }

    public ShufflingGenerator(DataSource<T> source, int hold, Distribution distribution) {
        this(source.toGenerator(), hold, distribution);
    }

    private ShufflingGenerator(Generator<T> source, int hold, Distribution distribution) {
        super(distribution, CLASS_SEED ^ GeneratorTools.getRandomSeedHint(source));
        this.source = source;
        this.hold = new Object[hold];
    }

    public ShufflingGenerator(DataSource<T> source, int hold, Distribution distribution, long seedHint) {
        super(distribution, seedHint);
        this.source = source.toGenerator();
        this.hold = new Object[hold];
    }

    protected ShufflingGenerator(ShufflingGenerator src) {
        super(src);
        this.source = src.source;
        this.hold = src.hold;
        this.end = src.end;
    }

    @Override
    public T next() {
        while (end < 0) {
            try {
                T value = source.next();
                int i = rnd().nextInt(hold.length);
                T next = (T) hold[i];
                hold[i] = value;
                if (next != null) return next;
            } catch (GeneratorException e) {
                end = 0;
            }
        }
        for (;end < hold.length; end++) {
            T next = (T) hold[end];
            if (next != null) {
                hold[end] = null;
                return next;
            }
        }
        throw new GeneratorException();
    }

    @Override
    public ShufflingGenerator<T> copy() {
        return new ShufflingGenerator<>(this);
    }
    
    @Override
    public StringBuilder toString(StringBuilder sb) {
        source.toString(sb).append(".shuffle(");
        return super.toString(sb).append(')');
    }
}
