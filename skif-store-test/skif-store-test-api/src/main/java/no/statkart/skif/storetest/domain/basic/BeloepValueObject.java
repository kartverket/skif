package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.guava.Preconditions;
import no.statkart.skif.store.ValueObject;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 */
public class BeloepValueObject implements ValueObject {
    private String valuta;
    private int verdi;
    @Nullable
    private String kommentar;


    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public BeloepValueObject() {
    }

    public BeloepValueObject(String valuta, int verdi) {
        this(valuta, verdi, null);
    }

    public BeloepValueObject(String valuta, int verdi, @Nullable String kommentar) {
        this.valuta = checkNotNull(valuta);
        this.verdi = verdi;
        this.kommentar = kommentar;
    }

    public String getValuta() {
        return valuta;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public int getVerdi() {
        return verdi;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setVerdi(int verdi) {
        this.verdi = verdi;
    }


    @Nullable
    public String getKommentar() {
        return kommentar;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setKommentar(String kommentar) {
        this.kommentar = kommentar;
    }

    public BeloepValueObject withKommentar(String kommentar) {
        return new BeloepValueObject(valuta, verdi, kommentar);
    }

    public BeloepValueObject withVerdi(int verdi) {
        return new BeloepValueObject(valuta, verdi, kommentar);
    }

    public BeloepValueObject withValuta(String valuta) {
        return new BeloepValueObject(valuta, verdi, kommentar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeloepValueObject that = (BeloepValueObject) o;

        if (verdi != that.verdi) return false;
        if (kommentar != null ? !kommentar.equals(that.kommentar) : that.kommentar != null) return false;
        if (!valuta.equals(that.valuta)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = valuta.hashCode();
        result = 31 * result + verdi;
        result = 31 * result + (kommentar != null ? kommentar.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BeloepValueObject{" +
                "valuta='" + valuta + '\'' +
                ", verdi=" + verdi +
                ", kommentar='" + kommentar + '\'' +
                '}';
    }
}
