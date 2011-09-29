package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleObject;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Kode extends BubbleObject {
    public KodeId<?> getId();
    public String getKodeverdi();
    public void setKodeverdi(String kodeVerdi);
    public String getBeskrivelse();
    public void setBeskrivelse(String beskrivelse);
}
