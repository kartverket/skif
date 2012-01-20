package no.statkart.skif.store.persistence.hibernate;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Henrik Fredholm
 */
public class HibernateSessionFactoryManagerBundle {
    final List<HibernateSessionFactoryManager> bundle = new ArrayList<HibernateSessionFactoryManager>(2);

    public HibernateSessionFactoryManagerBundle(HibernateSessionFactoryBuilder builder, HibernateSessionFactoryDescriptor... descriptors) {
        for (int i = 0; i < descriptors.length; i++) {
            HibernateSessionFactoryDescriptor hibernateSessionFactoryDescriptor = descriptors[i];
            bundle.add(new HibernateSessionFactoryManager(builder, hibernateSessionFactoryDescriptor));
        }
    }

    public void close() {
        for (HibernateSessionFactoryManager hibernateSessionFactoryManager : bundle) {
            hibernateSessionFactoryManager.close();
        }
    }

    public List<HibernateSessionFactoryManager> getBundle() {
        return Collections.unmodifiableList(bundle);
    }
}
