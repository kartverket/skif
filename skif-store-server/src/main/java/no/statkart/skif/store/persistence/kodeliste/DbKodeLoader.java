package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;
import no.statkart.skif.store.kodelistesupport.DbKodeImplId;
import no.statkart.skif.store.kodelistesupport.DbKodeliste;
import org.hibernate.Session;

import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface DbKodeLoader {
    void loadKoder(Session session, DbKodeliste kodeliste, Map<DbKodeImplId<?>, DbKodeImpl> kodeMap);
}
