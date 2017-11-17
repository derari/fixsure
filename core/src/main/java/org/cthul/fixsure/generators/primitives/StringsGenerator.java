package org.cthul.fixsure.generators.primitives;

import org.cthul.fixsure.DataSource;
import org.cthul.fixsure.Factory;
import org.cthul.fixsure.Generator;
import org.cthul.fixsure.fluents.FlTemplate;
import org.cthul.fixsure.generators.CopyableGenerator;
import org.cthul.fixsure.generators.GeneratorTools;
import org.cthul.fixsure.generators.GeneratorWithScalar;

/**
 * Generates random strings.
 */
public class StringsGenerator 
                extends GeneratorWithScalar<String>
                implements CopyableGenerator<String> {
    
    /**
     * Generates random strings.
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings() {
        return () -> new StringsGenerator();
    }
    
    /**
     * Generates random strings.
     * @param length
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(int length) {
        return () -> new StringsGenerator(length);
    }
    
    /**
     * Generates random strings.
     * @param characters
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(DataSource<Character> characters) {
        return () -> new StringsGenerator(characters);
    }
    
    /**
     * Generates random strings.
     * @param characters
     * @param length
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(DataSource<Character> characters, int length) {
        return () -> new StringsGenerator(characters, length);
    }
    
    /**
     * Generates random strings.
     * @param characters
     * @param length
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(DataSource<Character> characters, Generator<Integer> length) {
        return () -> new StringsGenerator(characters, length);
    }
    
    /**
     * Generates random strings.
     * @param min
     * @param max
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(char min, char max) {
        return () -> new StringsGenerator(CharactersGenerator.characters(min, max));
    }
    
    /**
     * Generates random strings.
     * @param min
     * @param max
     * @param length
     * @return random strings
     */
    @Factory
    public static FlTemplate<String> strings(char min, char max, int length) {
        return () -> new StringsGenerator(CharactersGenerator.characters(min, max), length);
    }
    
    protected static final int DEFAULT_LENGTH = 16;
    
    private final Generator<Character> characters;

    public StringsGenerator(DataSource<Character> characters) {
        this(characters, DEFAULT_LENGTH);
    }
    
    public StringsGenerator(DataSource<Character> characters, int length) {
        super(length != -1 ? length : DEFAULT_LENGTH);
        this.characters = characters != null ? characters.toGenerator() :
                            CharactersGenerator.characters().newGenerator();
    }

    public StringsGenerator(DataSource<Character> characters, DataSource<Integer> length) {
        super(length);
        this.characters = characters != null ? characters.toGenerator() :
                            CharactersGenerator.characters().newGenerator();
    }

    public StringsGenerator(int length) {
        this(CharactersGenerator.characters(), length);
    }

    public StringsGenerator() {
        this(null, DEFAULT_LENGTH);
    }

    public StringsGenerator(StringsGenerator src) {
        super(src);
        this.characters = GeneratorTools.copyGenerator(src.characters);
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
    public StringsGenerator copy() {
        return new StringsGenerator(this);
    }
}
