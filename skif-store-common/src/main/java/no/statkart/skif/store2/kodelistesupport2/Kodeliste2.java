package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store2.BubbleObject2;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Kodeliste2 extends BubbleObject2 {
    public KodelisteId2<?> getId();

    public Class<? extends BubbleKodeId2<?>> getKodeIdClass();

    public void setKodeIdClass(Class<? extends BubbleKodeId2<?>> kodeIdClass);
    
    public String getNavn();

    public void setNavn(String navn);

    public List<? extends BubbleKodeId2<?>> getKodeIds();

    public void setKodeIds(List<? extends BubbleKodeId2<?>> kodeIds);

    public List<? extends BubbleKode2> getKoder();

    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);
}
