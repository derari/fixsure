package org.cthul.fixsure.data;

import java.util.Locale;
import java.util.function.Function;
import org.cthul.fixsure.factory.ValueGenerator;
import org.cthul.fixsure.factory.ValueGenerator.ValueMap;
import org.cthul.strings.Formatter;
import org.cthul.strings.format.FormatArgs;
import org.cthul.strings.format.FormatterConfiguration;

/**
 *
 */
public class StringFormatter {
    
    private static final StringFormatter INSTANCE = new StringFormatter(FormatterConfiguration.getDefault().forLocale(Locale.ENGLISH));

    public static FormatterConfiguration getConfiguration() {
        return INSTANCE.config;
    }
    
    public static StringFormatter getInstance() {
        return INSTANCE;
    }
    
    /**
     * 
     * @param formatString
     * @return 
     * @see #formatString(java.lang.String, java.lang.Object...) 
     */
    public static Function<ValueMap, String> formatString(String formatString) {
        return getInstance().format(formatString);
    }
    
    /**
     * Creates a function that formats values from a {@link ValueMap}.
     * <p>
     * Values from the map can be referenced with {@code %:KEY$FORMAT}, where 
     * KEY is the value key and FORMAT is the format specification. See 
     * {@link Formatter} and {@link java.util.Formatter} for more documentation.
     * <p>
     * Example:
     * <pre><code>
     * Factories factories = Factories.build()
     *     .newFactory("Greeting")
     *         .assign("number").to(integers(1,6))
     *         .assign("greeting").to(StringFormatter.formatString("%1$s Number %:number$Ir", "Hello"))
     *         .build(vm -> vm.str("greeting"))
     *     .toFactories();
     * 
     * factories.create("Greeting"); // returns "Hello Number IV"
     * </code></pre>
     * 
     * @param formatString
     * @param args
     * @return formatting function
     * @see Formatter
     * @see java.util.Formatter
     */
    public static Function<ValueMap, String> formatString(String formatString, Object... args) {
        return getInstance().format(formatString, args);
    }
    
    private final FormatterConfiguration config;

    /**
     * Creates a new instance using the given format configuration.
     * @param config 
     */
    public StringFormatter(FormatterConfiguration config) {
        this.config = config;
    }

    /**
     * 
     * @param formatString
     * @return 
     * @see #formatString(java.lang.String, java.lang.Object...) 
     */
    public Function<ValueMap, String> format(String formatString) {
        return format(formatString, (Object[]) null);
    }

    /**
     * 
     * @param formatString
     * @param args
     * @return 
     * @see #formatString(java.lang.String, java.lang.Object...) 
     */
    public Function<ValueMap, String> format(String formatString, Object... args) {
        return new Function<ValueMap, String>() {
            @Override
            public String apply(ValueMap valueMap) {
                return new Formatter(config)
                        .format(formatString, new Args(args, valueMap))
                        .toString();
            }
            @Override
            public String toString() {
                return formatString;
            }
        };
    }
    
    protected static class Args implements FormatArgs {

        private final Object[] args;
        private final ValueGenerator.ValueMap values;

        public Args(Object[] args, ValueGenerator.ValueMap values) {
            this.args = args;
            this.values = values;
        }
        
        @Override
        public Object get(int i) {
            if (args == null || i < 0 || args.length < i) {
                throw new IndexOutOfBoundsException("" + i);
            }
            return args[i];
        }

        @Override
        public Object get(char c) {
            throw new IllegalArgumentException("" + c);
        }

        @Override
        public Object get(String s) {
            return values.get(s);
        }
    }
}
