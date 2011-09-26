package no.statkart.skif.store2.persistence.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.kodelistesupport2.*;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public abstract class DbKodelisteLoader2 {

    public abstract List<DbKodeliste2> load(Session session, Map<DbKodeId2<?>, DbKode2> kodeMap);

    protected  DbKodeLoader2 getKodeLoader(Class<? extends DbKode2> kodeClass) {
        return new DbSingleClassKodeLoader2();
    }

    protected List<DbKodeliste2> load(Session session, Class<? extends DbKodeliste2> dbKodelisteClass, Map<DbKodeId2<?>, DbKode2> kodeMap) {
        List<DbKodeliste2> kodelister = session.createCriteria(dbKodelisteClass).list();
        loadKoderAndInitialiseKodelister(session, kodelister, kodeMap);
        return kodelister;
    }

    private void loadKoderAndInitialiseKodelister(Session session, List<DbKodeliste2> kodelisteList, Map<DbKodeId2<?>, DbKode2> kodeMap) {
        for (DbKodeliste2 kodeliste : kodelisteList) {
            Class<? extends DbKode2> kodeClass = kodeliste.getKodeClass();
            Class<? extends DbKode2> kodeIdClass = getClass(kodeClass.getName() + "Id");
            kodeliste.setKodeIdClass((Class<? extends KodeId2<?>>) kodeIdClass);
            KodeSupport2 bubbleKodeSupport = KodeSupport2.getKodeSupport(kodeliste.getKodeIdClass());
            if (!bubbleKodeSupport.getKodeIdClass().equals(kodeIdClass)) {
                throw new ImplementationException("KodeId klassen (for kodeklasse) angitt i databasen '"+ kodeIdClass +"' stemmer ikke overens med KodeId klassen angitt i Java definisjon '" +bubbleKodeSupport.getKodeIdClass() + "' for kodeliste"  + kodeliste);
            }
            DbKodeLoader2 kodeLoader = getKodeLoader(kodeClass);
            if (kodeLoader==null) {
                throw new ImplementationException("Fant ingen kodeLoader for " + kodeliste.getKodeClassname());
            }
            kodeLoader.loadKoder(session, kodeliste, kodeMap);
        }
    }


    private Class<? extends DbKode2> getClass(String classname) {
        try {
            return (Class<? extends DbKode2>) Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}
