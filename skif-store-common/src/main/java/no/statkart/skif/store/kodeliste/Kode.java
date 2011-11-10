package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleObject;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class Kode extends AbstractBubbleObject  {
    private String kodeverdi;
    private String beskrivelse;

    @Override
    public KodeId<?> getId() {
        return (KodeId<?>) super.getId();
    }

    public String getKodeverdi() {
        return kodeverdi;
    }

    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

}
