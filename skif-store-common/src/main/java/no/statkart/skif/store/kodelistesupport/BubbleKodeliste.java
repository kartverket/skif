package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.BubbleObjectInterface;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleKodeliste extends BubbleObjectInterface {
    public BubbleKodelisteId<?> getId();

    public Class<? extends BubbleKodeId<?>> getKodeIdClass();

    public void setKodeIdClass(Class<? extends BubbleKodeId<?>> kodeIdClass);
    
    public void setNavn(String navn);

    public List<? extends BubbleKodeId<?>> getKodeIds();

    public void setKodeIds(List<? extends BubbleKodeId<?>> kodeIds);

    public List<? extends BubbleKode> getKoder();

    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);
}
