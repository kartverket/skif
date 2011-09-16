package no.statkart.skif.store.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodelistesupport.*;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class DbBubbleKodelisteLoader {

    public abstract List<DbBubbleKodeliste> load(Session session, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap);

    protected  DbBubbleKodeLoader getKodeLoader(Class<? extends DbBubbleKode> kodeClass) {
        return new DbSingleClassBubbleKodeLoader();
    }

    protected List<DbBubbleKodeliste> load(Session session, Class<? extends DbBubbleKodeliste> dbKodelisteClass, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
        List<DbBubbleKodeliste> kodelister = session.createCriteria(dbKodelisteClass).list();
        loadKoderAndInitialiseKodelister(session, kodelister, kodeMap);
        return kodelister;
    }

    private void loadKoderAndInitialiseKodelister(Session session, List<DbBubbleKodeliste> kodelisteList, Map<DbBubbleKodeId<?>, DbBubbleKode> kodeMap) {
        for (DbBubbleKodeliste kodeliste : kodelisteList) {
            Class<? extends DbBubbleKode> kodeClass = kodeliste.getKodeClass();
            Class<? extends DbBubbleKode> kodeIdClass = getClass(kodeClass.getName() + "Id");
            kodeliste.setKodeIdClass((Class<? extends BubbleKodeId<?>>) kodeIdClass);
            BubbleKodeSupport bubbleKodeSupport = BubbleKodeSupport.getKodeSupport(kodeliste.getKodeIdClass());
            if (!bubbleKodeSupport.getKodeIdClass().equals(kodeIdClass)) {
                throw new ImplementationException("KodeId klassen (for kodeklasse) angitt i databasen '"+ kodeIdClass +"' stemmer ikke overens med KodeId klassen angitt i Java definisjon '" +bubbleKodeSupport.getKodeIdClass() + "' for kodeliste"  + kodeliste);
            }
            DbBubbleKodeLoader kodeLoader = getKodeLoader(kodeClass);
            if (kodeLoader==null) {
                throw new ImplementationException("Fant ingen kodeLoader for " + kodeliste.getKodeClassname());
            }
            kodeLoader.loadKoder(session, kodeliste, kodeMap);
        }
    }


    private Class<? extends DbBubbleKode> getClass(String classname) {
        try {
            return (Class<? extends DbBubbleKode>) Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}
