package no.statkart.skif.storetest.persistence2;

import no.statkart.skif.store2.kodelistesupport2.DbKode2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeId2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeliste2;
import no.statkart.skif.store2.persistence.kodeliste.DbKodelisteLoader2;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestKodelisteLoader2 extends DbKodelisteLoader2 {
    @Override
    public List<DbKodeliste2> load(Session session, Map<DbKodeId2<?>, DbKode2> kodeMap) {
        return  new ArrayList<DbKodeliste2>(0); //load(session, DbKodeliste.class, kodeMap);
    }
}
