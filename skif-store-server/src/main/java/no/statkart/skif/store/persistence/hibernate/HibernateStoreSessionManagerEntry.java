package no.statkart.skif.store.persistence.hibernate;

/**
 * @author Henrik Fredholm
 */
class HibernateStoreSessionManagerEntry extends HibernateSessionManagerEntry {
    HibernateStoreSession storeSession;

    HibernateStoreSessionManagerEntry(Object key) {
        super(key);
    }
}
