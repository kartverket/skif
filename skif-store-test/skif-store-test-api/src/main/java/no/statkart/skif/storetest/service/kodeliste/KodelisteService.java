package no.statkart.skif.storetest.service.kodeliste;

import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.kodelistesupport.KodelisteImplId;

import java.util.Collection;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface KodelisteService {
    public Collection<? extends KodelisteImplId> getKodelisteIds();

    public KodelisteTransfer getKodelister();

    public String getKodelisterTest();
}