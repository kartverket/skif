package no.statkart.skif.storetest.service2.kodeliste2;

import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;

import java.util.Collection;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface KodelisteService2 {
    public Collection<? extends KodelisteId2> getKodelisteIds();

    public KodelisteTransfer2 getKodelister();
}