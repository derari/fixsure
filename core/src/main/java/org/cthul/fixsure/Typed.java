package org.cthul.fixsure;

/**
 * Optional interface for {@link DataSource}s to provide 
 * information about their values.
 */
public interface Typed<T> {

    /**
     * Returns the type of values produced/contained by this instance.
     * Can return {@code null} if the type is unknown; 
     * should never return a primitive type.
     * @return value type
     */
    Class<T> getValueType();
 
    @SuppressWarnings("unchecked")
    static <T> Class<T> typeOf(Object typed) {
        if (typed instanceof Typed) {
            return ((Typed) typed).getValueType();
        }
        return null;
    }
    
    static <T> Typed<T> token(String name) {
        return token(name, null);
    }
    
    static <T> Typed<T> token(String name, Class<T> type) {
        return new Typed<T>() {
            @Override
            public Class<T> getValueType() {
                return type;
            }
            @Override
            public String toString() {
                return name;
            }
        };
    }
}
