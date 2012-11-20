package no.statkart.skif.locker;

import java.io.Serializable;

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

        LockKey lockKey = (LockKey) o;

        if (discriminator != null ? !discriminator.equals(lockKey.discriminator) : lockKey.discriminator != null)
            return false;
        if (keyValue != null ? !keyValue.equals(lockKey.keyValue) : lockKey.keyValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = keyValue != null ? keyValue.hashCode() : 0;
        result = 31 * result + (discriminator != null ? discriminator.hashCode() : 0);
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
