package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKode extends StoreTestBubble {
    public StoreTestKodeId<?> getId();

    public KodelisteId<?> getKodelisteId();

    public void setKodelisteId(KodelisteId<?> kodelisteId);

    public String getKodeverdi();

    public void setKodeverdi(String kodeverdi);

    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);


}
