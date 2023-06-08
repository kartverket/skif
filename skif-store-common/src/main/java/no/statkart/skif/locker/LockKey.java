package no.statkart.skif.locker;

import java.io.Serializable;
import java.util.Objects;

/**
 * Interface for LockKey. Brukes for låsing mot database
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class LockKey<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public final T keyValue;
    public final String discriminator;

    public LockKey(String discriminator, T keyValue) {
        this.discriminator = discriminator;
        this.keyValue = keyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LockKey<?> lockKey = (LockKey<?>) o;
        return Objects.equals(discriminator, lockKey.discriminator)
                && Objects.equals(keyValue, lockKey.keyValue)
                ;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(keyValue);
        result = 31 * result + Objects.hashCode(discriminator);
        return result;
    }

    @Override
    public String toString() {
        return "LockKey{" +
                "discriminator='" + discriminator + '\'' +
                ", keyValue=" + keyValue +
                '}';
    }
}
