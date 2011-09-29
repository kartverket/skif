package no.statkart.skif.storetest.service.kodeliste;

import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.kodelistesupport.KodelisteId;

import java.util.Collection;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface KodelisteService {
    public Collection<? extends KodelisteId> getKodelisteIds();

    public KodelisteTransfer getKodelister();
}