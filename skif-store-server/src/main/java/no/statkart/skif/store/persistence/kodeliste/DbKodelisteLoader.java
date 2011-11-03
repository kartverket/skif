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
public abstract class DbKodelisteLoader {

    public abstract List<DbKodeliste> load(Session session, Map<DbKodeId<?>, DbKode> kodeMap);

    protected  DbKodeLoader getKodeLoader(Class<? extends DbKode> kodeClass) {
        return new DbSingleClassKodeLoader();
    }

    protected List<DbKodeliste> load(Session session, Class<? extends DbKodeliste> dbKodelisteClass, Map<DbKodeId<?>, DbKode> kodeMap) {
        List<DbKodeliste> kodelister = session.createCriteria(dbKodelisteClass).list();
        loadKoderAndInitialiseKodelister(session, kodelister, kodeMap);
        return kodelister;
    }

    private void loadKoderAndInitialiseKodelister(Session session, List<DbKodeliste> kodelisteList, Map<DbKodeId<?>, DbKode> kodeMap) {
        for (DbKodeliste kodeliste : kodelisteList) {
            Class<? extends DbKode> kodeClass = kodeliste.getKodeClass();
            String name = kodeClass.getName();
            Class<? extends DbKodeId> kodeIdClass = getClass(name + "Id");
            kodeliste.setKodeIdClass((Class<? extends KodeId<?>>) kodeIdClass);
            KodeSupport bubbleKodeSupport = KodeSupport.getKodeSupport(kodeliste.getKodeIdClass());
            if (!bubbleKodeSupport.getKodeIdClass().equals(kodeIdClass)) {
                throw new ImplementationException("KodeId klassen (for kodeklasse) angitt i databasen '"+ kodeIdClass +"' stemmer ikke overens med KodeId klassen angitt i Java definisjon '" +bubbleKodeSupport.getKodeIdClass() + "' for kodeliste"  + kodeliste);
            }
            DbKodeLoader kodeLoader = getKodeLoader(kodeClass);
            if (kodeLoader==null) {
                throw new ImplementationException("Fant ingen kodeLoader for " + kodeliste.getKodeClassname());
            }
            kodeLoader.loadKoder(session, kodeliste, kodeMap);
        }
    }


    private Class<? extends DbKodeId> getClass(String classname) {
        try {
            return (Class<? extends DbKodeId>) Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}
