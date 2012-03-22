package no.statkart.skif.store.persistence.hibernate;

import no.statkart.matrikkel.persistens.hibernate.bubbleref.BubbleRefIdPersister;
import org.hibernate.metadata.ClassMetadata;

/**
 *
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal integreres i superklassen
 * når SKIF støtter bubbleref for seneste versjon av hibernate
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DefaultHibernatePersistenceSessionImplExt extends HibernatePersistenceSessionMasterImpl {

    public DefaultHibernatePersistenceSessionImplExt(HibernateSessionFactoryManager sessionFactoryManager) {
        super(sessionFactoryManager);
    }

    @Override
    protected boolean erAvTypeSomIkkeSkalInitialiseresVidere(ClassMetadata classMetadata) {
        return super.erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata) || classMetadata instanceof BubbleRefIdPersister;
    }

}
