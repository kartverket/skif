package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbKode;
import no.statkart.skif.store.kodelistesupport.DbKodeId;
import no.statkart.skif.store.kodelistesupport.DbKodelisteImpl;
import org.hibernate.Session;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbKodeLoader {
    void loadKoder(Session session, DbKodelisteImpl kodeliste, Map<DbKodeId<?>, DbKode> kodeMap);
}
