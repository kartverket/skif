package no.statkart.skif.store2.persistence.hibernate;

/**
 * @author Henrik Fredholm
 */
class HibernateStoreSessionManagerEntry2 extends HibernateSessionManagerEntry2 {
    HibernateStoreSession2 storeSession;

    HibernateStoreSessionManagerEntry2(Object key) {
        super(key);
    }
}
