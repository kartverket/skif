package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.store3.persistence.PersistenceDescriptorWithStack;
import org.hibernate.Session;


/**
 * @author Henrik Fredholm
 */
public class HibernateSessionDescriptor extends PersistenceDescriptorWithStack<Session, HibernateSessionFactoryDescriptor> {
    public HibernateSessionDescriptor(HibernateSessionFactoryDescriptor wrapped) {
        super(wrapped);
    }

}
