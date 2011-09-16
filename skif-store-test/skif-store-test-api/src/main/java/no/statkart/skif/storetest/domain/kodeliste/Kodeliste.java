package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store.kodelistesupport.BubbleKodeliste;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.TestBubble;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class Kodeliste extends StoreTestBubble implements BubbleKodeliste {
    private Class<? extends BubbleKodeId<?>> kodeIdClass;
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

    @Override
    public Class<? extends BubbleKodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public void setKodeIdClass(Class<? extends BubbleKodeId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    @Override
    public List<KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    @Override
    public void setKodeIds(List<? extends BubbleKodeId<?>> kodeIds) {
        this.kodeIds = (List)kodeIds;
    }

    @Override
    public List<? extends Kode> getKoder() {
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
