package no.statkart.skif.storetest.domain.relation.uni.direct;

import java.io.Serializable;

/**
 * Ident for {@link no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne}.
 */
public class X1BBOneIdent implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bNr;

    public X1BBOneIdent() {
    }

    public X1BBOneIdent(int bNr) {
        this.bNr = bNr;
    }

    public int getBNr() {
        return bNr;
    }

    public void setBNr(int bNr) {
        this.bNr = bNr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        X1BBOneIdent x1AAIdent = (X1BBOneIdent) o;

        return bNr == x1AAIdent.bNr;
    }

    @Override
    public int hashCode() {
        int result = bNr;
        return result;
    }

    @Override
    public String toString() {
        return "X1BBOneIdent{" +
                "bNr=" + bNr +
                '}';
    }
}
