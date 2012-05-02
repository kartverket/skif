package no.statkart.skif.storetest.domain.tinglysing.kobling;

import no.statkart.skif.storetest.domain.tinglysing.util.Kobling;
import no.statkart.skif.storetest.domain.tinglysing.util.KoblingFactory;

import java.math.BigDecimal;

public class Beloep extends Kobling<RettsstiftelseBeloepRolle, Beloep> {

    public static KoblingFactory<RettsstiftelseBeloepRolle, Beloep, Beloep> KOBLING_FACTORY =
            new BeloepFactory();

    private BigDecimal beloepsverdi;
    private String valuta;
    private String beloepstekst;

    private String getRolle() {
        return rolle.toString();
    }

    private void setRolle(String rolle) {
        this.rolle = RettsstiftelseBeloepRolle.valueOf(rolle);
    }

    private void setRolle(RettsstiftelseBeloepRolle rolle) {
        this.rolle = rolle;
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

    public String getBeloepstekst() {
        return beloepstekst;
    }

    public void setBeloepstekst(String beloepstekst) {
        this.beloepstekst = beloepstekst;
    }

    @Override
    protected Beloep getValue() {
        return this;
    }

    @Override
    protected void setValue(Beloep value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private static class BeloepFactory implements KoblingFactory<RettsstiftelseBeloepRolle, Beloep, Beloep> {
        @Override
        public Beloep create(RettsstiftelseBeloepRolle rolle, Beloep target) {
            target.setRolle(rolle);
            return target;
        }
    }
}