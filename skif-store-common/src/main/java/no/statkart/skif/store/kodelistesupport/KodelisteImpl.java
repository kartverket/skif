package no.statkart.skif.store.kodelistesupport;

import no.statkart.skif.store.AbstractBubbleObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteImpl extends AbstractBubbleObject implements Kodeliste {
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
    public KodelisteIdImpl getId() {
        return (KodelisteIdImpl) super.getId();
    }

    @Override
    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    @Override
    public List<KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    @Override
    public void setKodeIds(List<? extends KodeId<?>> kodeIds) {
        this.kodeIds = (List)kodeIds;
    }

    @Override
    public List<Kode> getKoder() {
        return store.get(kodeIds);
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
