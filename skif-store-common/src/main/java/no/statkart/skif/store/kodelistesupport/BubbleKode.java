package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleObject;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleKode extends BubbleObject {
    public BubbleKodeId<?> getId();
    public String getKodeverdi();
    public void setKodeverdi(String kodeVerdi);
    public String getBeskrivelse();
    public void setBeskrivelse(String beskrivelse);
}
