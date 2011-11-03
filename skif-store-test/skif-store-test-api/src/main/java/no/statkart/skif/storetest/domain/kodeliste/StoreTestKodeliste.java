package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.store.kodelistesupport.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKodeliste extends StoreTestBubble {
    @Override
    StoreTestKodelisteId<?> getId();

    public String getNavn();

    public void setNavn(String navn);

    public Class<? extends KodeId<?>> getKodeIdClass();

    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass);

    public List<KodeId<?>> getKodeIds();

    public void setKodeIds(List<? extends KodeId<?>> kodeIds);

    public List<Kode> getKoder();


    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);
}
