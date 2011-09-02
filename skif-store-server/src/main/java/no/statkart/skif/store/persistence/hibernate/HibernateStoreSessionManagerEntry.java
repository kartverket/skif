package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.store.persistence.StoreSession;
import org.hibernate.Session;

/**
 * @author Henrik Fredholm
 */
class HibernateStoreSessionManagerEntry extends HibernateSessionManagerEntry {
    StoreSession<Session> storeSession;
}
