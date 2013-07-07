package org.cthul.fixsure.generators.primitive;

import org.cthul.fixsure.Generator;
import org.cthul.fixsure.GeneratorTemplate;
import org.cthul.fixsure.base.GeneratorWithScalar;
import org.hamcrest.Factory;

/**
 *
 */
public class StringsGenerator 
                extends GeneratorWithScalar<String>
                implements GeneratorTemplate<String> {
    
    private static final StringsGenerator DEFAULT = new StringsGenerator();
    
    @Factory
    public static StringsGenerator strings() {
        return DEFAULT;
    }
    
    @Factory
    public static StringsGenerator strings(int length) {
        return new StringsGenerator(length);
    }
    
    @Factory
    public static StringsGenerator strings(Generator<Character> characters) {
        return new StringsGenerator(characters);
    }
    
    @Factory
    public static StringsGenerator strings(Generator<Character> characters, int length) {
        return new StringsGenerator(characters, length);
    }
    
    @Factory
    public static StringsGenerator strings(Generator<Character> characters, Generator<Integer> length) {
        return new StringsGenerator(characters, length);
    }
    
    @Factory
    public static StringsGenerator strings(char min, char max) {
        return new StringsGenerator(CharactersGenerator.characters(min, max));
    }
    
    @Factory
    public static StringsGenerator strings(char min, char max, int length) {
        return new StringsGenerator(CharactersGenerator.characters(min, max), length);
    }
    
    protected static final int DEFAULT_LENGTH = 16;
    
    private final Generator<Character> characters;

    public StringsGenerator(Generator<Character> characters) {
        this(characters, DEFAULT_LENGTH);
    }
    
    public StringsGenerator(Generator<Character> characters, int length) {
        super(length != -1 ? length : DEFAULT_LENGTH);
        this.characters = characters != null ? characters :
                            CharactersGenerator.characters();
    }

    public StringsGenerator(Generator<Character> characters, Generator<Integer> length) {
        super(length);
        this.characters = characters != null ? characters :
                            CharactersGenerator.characters();
    }

    public StringsGenerator(int length) {
        this(CharactersGenerator.characters(), length);
    }

    public StringsGenerator() {
        this(null, DEFAULT_LENGTH);
    }

    @Override
    public String next() {
        final int len = nextScalar();
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(characters.next());
        }
        return sb.toString();
    };

    @Override
    public Class<String> getValueType() {
        return String.class;
    }    

    @Override
    public StringsGenerator newGenerator() {
        return this;
    }
    
}
