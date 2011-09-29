package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.BubbleObject;

import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Kodeliste extends BubbleObject {
    public KodelisteId<?> getId();

    public Class<? extends KodeId<?>> getKodeIdClass();

    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass);
    
    public String getNavn();

    public void setNavn(String navn);

    public List<? extends KodeId<?>> getKodeIds();

    public void setKodeIds(List<? extends KodeId<?>> kodeIds);

    public List<? extends Kode> getKoder();

    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);
}
