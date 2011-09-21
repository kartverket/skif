package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store2.AbstractBubbleObject2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class KodeImpl2 extends AbstractBubbleObject2 implements BubbleKode2 {
    private String kodeverdi;
    private String beskrivelse;

    @Override
    public KodeIdImpl2<?> getId() {
        return (KodeIdImpl2<?>) super.getId();
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
