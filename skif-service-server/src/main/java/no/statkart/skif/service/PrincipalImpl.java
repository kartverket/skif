package no.statkart.skif.service;

import java.io.Serializable;
import java.security.Principal;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class PrincipalImpl implements Principal, Serializable {
    private String name;

    public PrincipalImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrincipalImpl principal = (PrincipalImpl) o;

        return name != null ? name.equals(principal.name) : principal.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
