package no.statkart.skif.storetest.domain.tinglysing;

import java.math.BigDecimal;

/**
 * @author Oddbjørn Kvalsund
 */
public class Beloep {
    private BigDecimal beloepsverdi;
    private String valuta;
    private Beloepstype beloepstype;
    private String beloepstekst;

    public Beloep() {
    }

    public Beloep(BigDecimal beloepsverdi, String valuta, Beloepstype beloepstype, String beloepstekst) {
        this.beloepsverdi = beloepsverdi;
        this.valuta = valuta;
        this.beloepstype = beloepstype;
        this.beloepstekst = beloepstekst;
    }

    public BigDecimal getBeloepsverdi() {
        return beloepsverdi;
    }

    public void setBeloepsverdi(BigDecimal beloepsverdi) {
        this.beloepsverdi = beloepsverdi;
    }

    public String getValuta() {
        return valuta;
    }

    public void setValuta(String valuta) {
        this.valuta = valuta;
    }

    public Beloepstype getBeloepstype() {
        return beloepstype;
    }

    public void setBeloepstype(Beloepstype beloepstype) {
        this.beloepstype = beloepstype;
    }

    public String getBeloepstekst() {
        return beloepstekst;
    }

    public void setBeloepstekst(String beloepstekst) {
        this.beloepstekst = beloepstekst;
    }
}