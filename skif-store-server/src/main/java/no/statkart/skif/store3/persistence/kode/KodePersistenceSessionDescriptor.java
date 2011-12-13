package no.statkart.skif.store3.persistence.kode;

import no.statkart.skif.store3.persistence.PersistenceDescriptorWrapper;
import no.statkart.skif.store3.persistence.hibernate.HibernatePersistenceSession;
import no.statkart.skif.store3.persistence.hibernate.HibernateSessionDescriptor;

/**
 * @author Henrik Fredholm
 */
public class KodePersistenceSessionDescriptor extends PersistenceDescriptorWrapper<KodePersistenceSession, HibernateSessionDescriptor> {
    public KodePersistenceSessionDescriptor(HibernateSessionDescriptor wrapped) {
        super(wrapped);
    }
}
