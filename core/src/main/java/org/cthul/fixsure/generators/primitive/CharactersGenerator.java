package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.Distribution;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorWithScalar;
import org.hamcrest.Factory;

/**
 *
 * @author Arian Treffer
 */
public abstract class CharactersGenerator 
                extends GeneratorWithScalar<Character>
                implements GeneratorTemplate<Character> {
    
    private static final CharactersGenerator DEFAULT = new InRange(' ', '~');
    
    @Factory
    public static CharactersGenerator characters() {
        return DEFAULT;
    }
    
    @Factory
    public static CharactersGenerator characters(String source) {
        return new FromString(source);
    }
    
    @Factory
    public static CharactersGenerator characters(String source, Distribution distribution) {
        return new FromString(source, distribution);
    }
    
    @Factory
    public static CharactersGenerator characters(char min, char max) {
        return new InRange(min, max);
    }
    
    @Factory
    public static CharactersGenerator characters(char min, char max, Distribution distribution) {
        return new InRange(min, max, distribution);
    }
    
    @Factory
    public static CharactersGenerator characters(Generator<Integer> range) {
        return new InRange(range);
    }
    
    public CharactersGenerator(int scalar) {
        super(scalar);
    }

    public CharactersGenerator(Generator<Integer> scalarGenerator) {
        super(scalarGenerator);
    }

    public CharactersGenerator(int scalar, Distribution distribution) {
        super(scalar, distribution);
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

    @Override
    public abstract CharactersGenerator newGenerator();
    
    public static class FromString extends CharactersGenerator {
        
        private final String source;

        public FromString(String source) {
            super(source.length());
            this.source = source;
        }
        
        public FromString(String source, Distribution distribution) {
            super(source.length(), distribution);
            this.source = source;
        }

        @Override
        public char nextValue() {
            return source.charAt(nextScalar());
        }

        @Override
        public FromString newGenerator() {
            return this;
        }
        
    }
    
    public static class InRange extends CharactersGenerator {

        public InRange(char min, char max) {
            super(IntegersGenerator.integers(min, max+1));
        }
        
        public InRange(char min, char max, Distribution distribution) {
            super(IntegersGenerator.integers(min, max+1, distribution));
        }
        
        public InRange(Generator<Integer> range) {
            super(range);
        }

        @Override
        public char nextValue() {
            return (char) nextScalar();
        }

        @Override
        public InRange newGenerator() {
            return this;
        }
        
    }
    
}
