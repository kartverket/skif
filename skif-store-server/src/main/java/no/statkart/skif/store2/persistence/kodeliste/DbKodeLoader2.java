package no.statkart.skif.store2.persistence.kodeliste;

import no.statkart.skif.store2.kodelistesupport2.DbKode2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeId2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeliste2;
import org.hibernate.Session;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbKodeLoader2 {
    void loadKoder(Session session, DbKodeliste2 kodeliste, Map<DbKodeId2<?>, DbKode2> kodeMap);
}
