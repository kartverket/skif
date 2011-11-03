package no.statkart.skif.storetest.persistence;

import no.statkart.skif.store.kodelistesupport.DbKodeImpl;
import no.statkart.skif.store.kodelistesupport.DbKodeImplId;
import no.statkart.skif.store.kodelistesupport.DbKodeliste;
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
    public List<DbKodeliste> load(Session session, Map<DbKodeImplId<?>, DbKodeImpl> kodeMap) {
        return  new ArrayList<DbKodeliste>(0); //load(session, DbKodeliste.class, kodeMap);
    }
}
