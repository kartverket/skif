package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.AbstractBubbleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class Kodeliste extends AbstractBubbleObject  {
    private Class<? extends KodeImplId<?>> kodeIdClass;
    private String navn;
    private String beskrivelse;
    private List<KodeImplId<?>> kodeIds = new ArrayList<KodeImplId<?>>();

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

    public Class<? extends KodeImplId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public void setKodeIdClass(Class<? extends KodeImplId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    public List<KodeImplId<?>> getKodeIds() {
        return kodeIds;
    }

    public void setKodeIds(List<? extends KodeImplId<?>> kodeIds) {
        this.kodeIds = (List)kodeIds;
    }

    public List<KodeImpl> getKoder() {
        return store.get(kodeIds);
    }


    public String getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    } 
}
