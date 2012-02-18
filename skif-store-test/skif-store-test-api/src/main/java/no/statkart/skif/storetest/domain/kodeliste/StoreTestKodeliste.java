package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.List;

/**
 * Alle Kodelister i StoreTest applikasjonen implementerer dette interface. Der er nødvendig å angi hvilke metoder
 * som en kodeliste har siden interfacet ikke kan subklasse Kodeliste klassen
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestKodeliste extends StoreTestBubble {
    @Override
    public StoreTestKodelisteId<?> getId();
    public void setId(BubbleId<?> kodelisteId);

    String getBeskrivelse();
    void setBeskrivelse(String beskrivelse);

    Class<? extends KodeId<?>> getKodeIdClass();
    void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass);

    List<KodeId<?>> getKodeIds();
    void setKodeIds(List<? extends KodeId<?>> kodeIds);

    String getNavn();
    void setNavn(String navn);

    boolean isEditerbar();
    void setEditerbar(boolean editerbar);
}
