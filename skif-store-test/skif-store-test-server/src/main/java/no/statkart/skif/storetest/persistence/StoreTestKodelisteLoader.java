package no.statkart.skif.storetest.persistence;

import no.statkart.skif.store.kodeliste.DbKode;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeliste;
import no.statkart.skif.store.persistence.kodeliste.DbKodelisteLoader;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestKodelisteLoader extends DbKodelisteLoader {
    @Override
    public List<DbKodeliste> load(Session session, Map<DbKodeId<?>, DbKode> kodeMap) {
        return  new ArrayList<DbKodeliste>(0); //load(session, DbKodeliste.class, kodeMap);
    }
}
