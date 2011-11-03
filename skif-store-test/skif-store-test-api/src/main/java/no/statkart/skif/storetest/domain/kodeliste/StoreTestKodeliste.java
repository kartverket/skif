package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.KodeImpl;
import no.statkart.skif.store.kodelistesupport.KodeImplId;
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

    public Class<? extends KodeImplId<?>> getKodeIdClass();

    public void setKodeIdClass(Class<? extends KodeImplId<?>> kodeIdClass);

    public List<KodeImplId<?>> getKodeIds();

    public void setKodeIds(List<? extends KodeImplId<?>> kodeIds);

    public List<KodeImpl> getKoder();


    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);
}
