package no.statkart.skif.storetest.persistence;

import no.statkart.skif.store.kodelistesupport.DbBubbleKode;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeId;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeliste;
import no.statkart.skif.store.persistence.kodeliste.DbBubbleKodelisteLoader;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestKodelisteLoader extends DbBubbleKodelisteLoader {
    @Override
    public List<DbBubbleKodeliste> load(Session session, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
        return  new ArrayList<DbBubbleKodeliste>(0); //load(session, DbKodeliste.class, kodeMap);
    }
}
