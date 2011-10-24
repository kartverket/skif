package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.AbstractBubbleObject;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeImpl extends AbstractBubbleObject implements Kode {
    private String kodeverdi;
    private String beskrivelse;

    @Override
    public KodeIdImpl<?> getId() {
        return (KodeIdImpl<?>) super.getId();
    }

    @Override
    public String getKodeverdi() {
        return kodeverdi;
    }

    @Override
    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
    }

    @Override
    public String getBeskrivelse() {
        return beskrivelse;
    }

    @Override
    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

}
