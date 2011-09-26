package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store2.AbstractBubbleObject2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteImpl2 extends AbstractBubbleObject2 implements Kodeliste2 {
    private Class<? extends KodeId2<?>> kodeIdClass;
    private String navn;
    private String beskrivelse;
    private List<KodeIdImpl2<?>> kodeIds = new ArrayList<KodeIdImpl2<?>>();

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    @Override
    public KodelisteIdImpl2 getId() {
        return (KodelisteIdImpl2) super.getId();
    }

    @Override
    public Class<? extends KodeId2<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public void setKodeIdClass(Class<? extends KodeId2<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    @Override
    public List<KodeIdImpl2<?>> getKodeIds() {
        return kodeIds;
    }

    @Override
    public void setKodeIds(List<? extends KodeId2<?>> kodeIds) {
        this.kodeIds = (List)kodeIds;
    }

    @Override
    public List<? extends KodeImpl2> getKoder() {
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
