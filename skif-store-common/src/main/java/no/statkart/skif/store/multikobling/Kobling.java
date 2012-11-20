package no.statkart.skif.store.multikobling;

import no.statkart.skif.store.BubbleId;

import java.io.Serializable;

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

        if (rolle != null ? !rolle.equals(kobling.rolle) : kobling.rolle != null) return false;
        V value = getValue();
        if (value != null ? !value.equals(kobling.getValue()) : kobling.getValue() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rolle != null ? rolle.hashCode() : 0;
        V value = getValue();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
