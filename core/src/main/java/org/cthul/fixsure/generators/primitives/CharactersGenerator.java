package org.cthul.fixsure.generators.primitives;

import java.util.Locale;
import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.api.Factory;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.distributions.DistributionRandomizer;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorWithScalar;

/**
 * Generates random characters.
 */
public abstract class CharactersGenerator 
                extends GeneratorWithScalar<Character>
                implements CopyableGenerator<Character> {
    
    /**
     * Generates random characters from space ({@code ' '} to tilde ({@code '~'}).
     * @return random characters
     */
    @Factory
    public static FlTemplate<Character> characters() {
        return () -> new InRange(' ', '~');
    }
    
    /**
     * Generates random characters from a string.
     * @param source
     * @return random characters
     */
    @Factory
    public static FlTemplate<Character> characters(String source) {
        return () -> new FromString(source);
    }
    
    /**
     * Generates random characters from a string.
     * @param source
     * @param distribution
     * @return random characters
     */
    @Factory
    public static FlTemplate<Character> characters(String source, Distribution distribution) {
        return () -> new FromString(source, distribution);
    }
    
    /**
     * Generates random characters with in the given range, inclusive.
     * @param min
     * @param max
     * @return random characters
     */
    @Factory
    public static FlTemplate<Character> characters(char min, char max) {
        return () -> new InRange(min, max);
    }
    
    /**
     * Generates random characters with in the given range, inclusive.
     * @param min
     * @param max
     * @param distribution
     * @return random characters
     */
    @Factory
    public static FlTemplate<Character> characters(char min, char max, Distribution distribution) {
        return () -> new InRange(min, max, distribution);
    }
    
    /**
     * Converts integers to characters
     * @param range
     * @return characters
     */
    public static FlTemplate<Character> characters(DataSource<Integer> range) {
        return () -> new InRange(range.toGenerator());
    }
    
    public CharactersGenerator(int scalar) {
        super(scalar);
    }

    public CharactersGenerator(DataSource<Integer> scalarGenerator) {
        super(scalarGenerator);
    }

    public CharactersGenerator(int scalar, Distribution distribution, long seed) {
        super(scalar, distribution, seed);
    }

    protected CharactersGenerator(CharactersGenerator src) {
        super(src);
    }

    public abstract char nextValue();

    @Override
    public Character next() {
        return nextValue();
    }

    @Override
    public Class<Character> getValueType() {
        return Character.class;
    }
    
    public static class FromString extends CharactersGenerator {
        
        private static final long CLASS_SEED = DistributionRandomizer.toSeed(FromString.class);
        
        private final String source;

        public FromString(String source) {
            super(source.length());
            this.source = source;
        }
        
        public FromString(String source, Distribution distribution) {
            super(source.length(), distribution, source.length() ^ CLASS_SEED);
            this.source = source;
        }

        public FromString(FromString src) {
            super(src);
            this.source = src.source;
        }

        @Override
        public char nextValue() {
            return source.charAt(nextScalar());
        }

        @Override
        public FromString copy() {
            return new FromString(this);
        }
    }
    
    public static class InRange extends CharactersGenerator {
        
        private static final long CLASS_SEED = DistributionRandomizer.toSeed(InRange.class);
        
        private static long seed(char min, char max) {
            return CLASS_SEED ^ ((long) min << 32) ^ max;
        }

        public InRange(char min, char max) {
            super(RandomIntegersGenerator.integers(min, max+1).random(seed(min,max)));
        }
        
        public InRange(char min, char max, Distribution distribution) {
            super(RandomIntegersGenerator.integers(min, max+1).random(distribution, seed(min,max)));
        }
        
        public InRange(Generator<Integer> range) {
            super(range);
        }

        public InRange(InRange src) {
            super(src);
        }

        @Override
        public char nextValue() {
            return (char) nextScalar();
        }

        @Override
        public InRange copy() {
            return new InRange(this);
        }
    }
}
