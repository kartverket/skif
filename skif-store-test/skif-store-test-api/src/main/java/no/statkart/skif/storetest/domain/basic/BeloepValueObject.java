package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.ValueObject;

import javax.annotation.Nullable;
import java.util.Objects;


/**
 * Eksempel på et ValueObject. Bemerk at dette objekt er immutable.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BeloepValueObject implements ValueObject {
    private static final long serialVersionUID = 1L;

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
        this.valuta = Objects.requireNonNull(valuta);
        this.verdi = verdi;
        this.kommentar = kommentar;
    }

    public String getValuta() {
        return valuta;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    @Deprecated // WS-mapping
    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public int getVerdi() {
        return verdi;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    @Deprecated // WS-mapping
    private void setVerdi(int verdi) {
        this.verdi = verdi;
    }


    @Nullable
    public String getKommentar() {
        return kommentar;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    @Deprecated // WS-mapping
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
        return (verdi == that.verdi)
                && Objects.equals(kommentar, that.kommentar)
                && valuta.equals(that.valuta)
                ;
    }

    @Override
    public int hashCode() {
        int result = valuta.hashCode();
        result = 31 * result + verdi;
        result = 31 * result + Objects.hashCode(kommentar);
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
