package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.store3.persistence.PersistenceDescriptorWrapper;

/**
 * @author Henrik Fredholm
 */
public class HibernatePersistenceSessionDescriptor extends PersistenceDescriptorWrapper<HibernatePersistenceSession, HibernateSessionDescriptor> {
    public HibernatePersistenceSessionDescriptor(HibernateSessionDescriptor wrapped) {
        super(wrapped);
    }
}
