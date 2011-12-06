package no.statkart.skif.store3.persistence.hibernate;

import com.google.inject.Inject;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import no.statkart.skif.store3.persistence.PersistenceDescriptor;
import no.statkart.skif.store3.persistence.PersistenceDescriptorRegistry;
import org.hibernate.SessionFactory;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerImpl implements  HibernateSessionFactoryManager {
    private final HibernateSessionFactoryBuilder factoryBuilder;
    private final HibernateSessionFactoryDescriptor[] descriptors;

    @Inject
    public HibernateSessionFactoryManagerImpl(HibernateSessionFactoryBuilder factoryBuilder, HibernateSessionFactoryDescriptor... descriptors) {
        this.factoryBuilder = factoryBuilder;
        this.descriptors = descriptors;
        for (int i = 0; i < descriptors.length; i++) {
            descriptors[i].setIndex(i);
        }
    }

    /**
     * Returnere et array over tilgjengelig factories som kan opprettes. Uthenting av descriptorene oppretter ikke
     * factoryene. Første gang {@link #getFactory(int)} kalles åpnes den pågjenldne factoryen
     * @return
     */
    public HibernateSessionFactoryDescriptor[] getPersistenceDescriptors() {
        return descriptors;
    }

    /**
     * Returnerer factory for gitt index. Oppretter factoryen hvis den ikke er oppretet fra før.
     * @param index
     * @return
     */
    @Override
    public synchronized SessionFactory getFactory(int index) {
        SessionFactory factory = descriptors[index].getObject();
        if (factory ==null) {
            HibernateSessionFactoryDescriptor descriptor = descriptors[index];
            factory = factoryBuilder.build(descriptor.getSeed(), descriptor.getHibernateProperties(), descriptor.getHibernateInterceptor());
        }
        return factory;
    }

    /**
     * Lukker alle åpnet factories
     */
    @Override
    public synchronized void close() {
        for (int i = 0; i < descriptors.length; i++) {
            SessionFactory factory = descriptors[i].getObject();
            if (factory!=null) {
                factory.close();
                descriptors[i] = null;
            }
        }
    }
}
