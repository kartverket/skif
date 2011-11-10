package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeliste;
import org.hibernate.Session;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbKodeLoader {
    void loadKoder(Session session, DbKodeliste kodeliste, Map<DbKodeId<?>, DbKode> kodeMap);
}
