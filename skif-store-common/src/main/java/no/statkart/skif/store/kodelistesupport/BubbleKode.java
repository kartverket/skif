package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleObjectInterface;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleKode extends BubbleObjectInterface {
    public BubbleKodeId<?> getId();
    public String getKodeverdi();
    public void setKodeverdi(String kodeVerdi);
    public String getBeskrivelse();
    public void setBeskrivelse(String beskrivelse);
}
