package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Henrik Fredholm
 */
public interface StoreTestKode extends StoreTestBubble {
    public KodeId<?> getId();

    public String getKodeverdi();

    public void setKodeverdi(String kodeverdi);

    public String getBeskrivelse();

    public void setBeskrivelse(String beskrivelse);


}
