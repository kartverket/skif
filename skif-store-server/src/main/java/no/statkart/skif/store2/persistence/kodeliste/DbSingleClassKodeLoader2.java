package no.statkart.skif.store2.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.kodelistesupport2.*;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSingleClassKodeLoader2 implements DbKodeLoader2 {


    public DbSingleClassKodeLoader2() {
    }

    @Override
    public void loadKoder(Session session, DbKodeliste2 kodeliste, Map<DbKodeId2<?>, DbKode2> kodeMap) {
        Class<? extends DbKode2> kodeClass = kodeliste.getKodeClass();
        List<DbKode2> list = session.createCriteria(kodeClass).list();
        List<KodeId2<?>> kodeIds = new ArrayList<KodeId2<?>>();
        DbKodelisteId2 kodelisteId = kodeliste.getId();
        for (DbKode2 t : list) {
            if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode2: " + t + " DbKode2.getKodelisteId: " + t.getId().getKodelisteId());
            }
            kodeIds.add(t.getId());
            BubbleObject2 oldKode = kodeMap.put(t.getId(), t);
            if (oldKode != null) {
                throw new ImplementationException("KodeId2 allerede i bruk. Id: " + t.getId() + " eksisterende kode: " + oldKode);
            }
        }
        kodeliste.setKodeIds(kodeIds);
    }

}
