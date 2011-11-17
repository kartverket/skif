package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class Kodeliste extends AbstractBubbleObject  {
    private Class<? extends KodeId<?>> kodeIdClass;
    private String navn;
    private String beskrivelse;
    private List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    @Override
    public KodelisteId getId() {
        return (KodelisteId) super.getId();
    }

    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    public List<KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    public void setKodeIds(List<? extends KodeId<?>> kodeIds) {
        this.kodeIds = (List)kodeIds;
    }

    public List<Kode> getKoder() {
        return store.get(kodeIds);
    }


    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    } 
}
