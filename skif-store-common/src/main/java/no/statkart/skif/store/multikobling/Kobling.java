package no.statkart.skif.store.multikobling;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kobling<R,V> implements Serializable {
    private static final long serialVersionUID = 1L;

    private R rolle;

    public Kobling() {
    }

    public Kobling(R rolle, V value) {
        this.rolle = rolle;
        setValue(value);
    }

    public R getRolle() {
        return rolle;
    }

    public void setRolle(R rolle) {
        this.rolle = rolle;
    }

    protected abstract V getValue();

    protected abstract void setValue(V value);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kobling kobling = (Kobling) o;

        return Objects.equals(rolle, kobling.rolle) && Objects.equals(getValue(), kobling.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolle, getValue());
    }
}
