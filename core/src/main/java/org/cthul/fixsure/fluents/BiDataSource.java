package org.cthul.fixsure.fluents;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 */
public interface BiDataSource<T, U> {
    
    BiGenerator<T, U> toBiGenerator();
    
    FlDataSource<T> onlyFirst();
    
    FlDataSource<U> onlySecond();
    
    FlDataSource<Pair<T,U>> pairs();
    
    BiDataSource<T, U> filter(BiPredicate<? super T, ? super U> predicate);
    
    BiDataSource<T, U> filter1(Predicate<? super T> predicate);
    
    BiDataSource<T, U> filter2(Predicate<? super U> predicate);
    
    <R> FlDataSource<R> map(BiFunction<? super T, ? super U, ? extends R> function);
    
    <R> BiDataSource<R, U> map1(Function<? super T, ? extends R> function);
    
    <R> BiDataSource<T, R> map2(Function<? super U, ? extends R> function);
    
    class Pair<T,U> {
        final T v1;
        final U v2;

        public Pair(T v1, U v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public T v1() {
            return v1;
        }

        public U v2() {
            return v2;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + Objects.hashCode(this.v1);
            hash = 59 * hash + Objects.hashCode(this.v2);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            if (!Objects.equals(this.v1, other.v1)) {
                return false;
            }
            if (!Objects.equals(this.v2, other.v2)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + v1 + "; " + v2 + ")";
        }
    }
}
