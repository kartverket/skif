package no.statkart.skif.store.multikobling;

import no.statkart.skif.store.BubbleId;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class Kobling<R,V> implements Serializable {
    public R rolle;
    public V value;

    public Kobling() {
    }

    public Kobling(R rolle, V value) {
        this.rolle = rolle;
        this.value = value;
    }

    private String getRolle() {
        return rolle.toString();
    }

    public void setRolle(R rolle) {
        this.rolle = rolle;
    }

    protected V getValue() {
        return value;
    }
    protected void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kobling kobling = (Kobling) o;

        if (rolle != null ? !rolle.equals(kobling.rolle) : kobling.rolle != null) return false;
        if (value != null ? !value.equals(kobling.value) : kobling.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = rolle != null ? rolle.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
