package no.statkart.skif.storetest.persistence;

import no.statkart.skif.store.kodelistesupport.DbKode;
import no.statkart.skif.store.kodelistesupport.DbKodeId;
import no.statkart.skif.store.kodelistesupport.DbKodelisteImpl;
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
    public List<DbKodelisteImpl> load(Session session, Map<DbKodeId<?>, DbKode> kodeMap) {
        return  new ArrayList<DbKodelisteImpl>(0); //load(session, DbKodeliste.class, kodeMap);
    }
}
