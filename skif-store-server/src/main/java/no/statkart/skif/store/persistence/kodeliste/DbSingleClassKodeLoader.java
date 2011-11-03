package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.kodelistesupport.*;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSingleClassKodeLoader implements DbKodeLoader {


    public DbSingleClassKodeLoader() {
    }

    @Override
    public void loadKoder(Session session, DbKodeliste kodeliste, Map<DbKodeImplId<?>, DbKodeImpl> kodeMap) {
        Class<? extends DbKodeImpl> kodeClass = kodeliste.getKodeClass();
        List<DbKodeImpl> list = session.createCriteria(kodeClass).list();
        List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();
        DbKodelisteId kodelisteId = kodeliste.getId();
        for (DbKodeImpl t : list) {
            if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbKode: " + t + " DbKode.getKodelisteId: " + t.getId().getKodelisteId());
            }
            kodeIds.add(t.getId());
            BubbleObject oldKode = kodeMap.put(t.getId(), t);
            if (oldKode != null) {
                throw new ImplementationException("KodeId allerede i bruk. Id: " + t.getId() + " eksisterende kode: " + oldKode);
            }
        }
        kodeliste.setKodeIds(kodeIds);
    }

}
