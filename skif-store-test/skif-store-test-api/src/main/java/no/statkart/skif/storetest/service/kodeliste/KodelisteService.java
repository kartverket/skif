package no.statkart.skif.storetest.service.kodeliste;

import no.statkart.skif.storetest.domain.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.KodelisteTransfer;

import java.util.Collection;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface KodelisteService  {
    public Collection<? extends KodelisteId> getKodelisteIds();

    public KodelisteTransfer getKodelister();
}