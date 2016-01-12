package no.statkart.skif.storetest.domain.relation.uni.direct;

import java.io.Serializable;

/**
 * Komposittident for {@link X1AA} som er sammensatt av {@link X1BBOne#nr} og {@link X1AA#nr}.
 */
public class X1AAIdent implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bNr;
    private int aNr;

    public X1AAIdent() {
    }

    public X1AAIdent(int bNr, int aNr) {
        this.bNr = bNr;
        this.aNr = aNr;
    }

    public int getBNr() {
        return bNr;
    }

    public void setBNr(int bNr) {
        this.bNr = bNr;
    }

    public int getANr() {
        return aNr;
    }

    public void setANr(int aNr) {
        this.aNr = aNr;
    }

    public static X1AAIdent from(X1BBOne someBB, int nr) {
        return someBB==null ? null : new X1AAIdent(someBB.getNr(), nr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        X1AAIdent x1AAIdent = (X1AAIdent) o;

        return aNr == x1AAIdent.aNr && bNr == x1AAIdent.bNr;
    }

    @Override
    public int hashCode() {
        int result = bNr;
        result = 31 * result + aNr;
        return result;
    }

    @Override
    public String toString() {
        return "X1AAIdent{" +
                "bNr=" + bNr +
                ", aNr=" + aNr +
                '}';
    }

}
