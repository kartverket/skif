package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.BubbleKode;
import no.statkart.skif.storetest.domain.TestBubble;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class Kode extends TestBubble implements BubbleKode {
    private String kodeverdi;
    private String beskrivelse;

    @Override
    public KodeId<?> getId() {
        return (KodeId<?>) super.getId();
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
