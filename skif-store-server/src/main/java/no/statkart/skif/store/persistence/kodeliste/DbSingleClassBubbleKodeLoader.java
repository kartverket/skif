package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleObjectInterface;
import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.store.kodelistesupport.DbBubbleKodeliste;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSingleClassBubbleKodeLoader implements DbBubbleKodeLoader {


    public DbSingleClassBubbleKodeLoader() {
    }

    @Override
    public void loadKoder(Session session, DbBubbleKodeliste kodeliste, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
//        DbBubbleKodeSupport bubbleKodeSupport = DbBubbleKodeSupport.getKodeSupport(kodeliste.getKodeIdClass());
//        Class<? extends DbBubbleKode> kodeClass = bubbleKodeSupport.getKodeClass(); 
        Class<? extends DbBubbleKode> kodeClass = kodeliste.getKodeClass();
        List<DbBubbleKode> list = session.createCriteria(kodeClass).list();
        List<BubbleKodeId<?>> kodeIds = new ArrayList<BubbleKodeId<?>>();
        DbBubbleKodelisteId kodelisteId = kodeliste.getId();
        for (DbBubbleKode t : list) {
            if (!t.getId().getKodelisteId().equals(kodelisteId)) {
                throw new ImplementationException("Feil i kodelisteIdValue for kodeliste: " + kodeliste + " DbBubbleKode: " + t + " DbBubbleKode.getKodelisteId: " + t.getId().getKodelisteId());
            }
            kodeIds.add(t.getId());
            BubbleObjectInterface oldKode = kodeMap.put(t.getId(), t);
            if (oldKode != null) {
                throw new ImplementationException("BubbleKodeId allerede i bruk. Id: " + t.getId() + " eksisterende kode: " + oldKode);
            }
        }
        kodeliste.setKodeIds(kodeIds);
    }

}
