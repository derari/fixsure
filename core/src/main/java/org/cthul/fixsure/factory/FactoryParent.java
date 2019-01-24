package org.cthul.fixsure.factory;

import java.util.Map;

public interface FactoryParent {
    
    Map<String, String> getDescriptionForChild();
    
    <T> ValueGenerator<T> peekGenerator(String key);
    
    <T> ValueSource<T> peekSource(String key, boolean useDefault);
}
