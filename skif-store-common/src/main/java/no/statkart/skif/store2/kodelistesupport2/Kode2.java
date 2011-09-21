package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store2.BubbleObject2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Kode2 extends BubbleObject2 {
    public KodeId2<?> getId();
    public String getKodeverdi();
    public void setKodeverdi(String kodeVerdi);
    public String getBeskrivelse();
    public void setBeskrivelse(String beskrivelse);
}
