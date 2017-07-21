package no.statkart.skif.store.persistence.hibernate;


import no.statkart.skif.service.sequence.IdService;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0 (under dette navnet)
 */
public class DefaultHibernateSessionFactoryManagerBundle implements HibernateSessionFactoryManagerBundle {
    final List<HibernateSessionFactoryManager> bundle = new ArrayList<>(2);

    public DefaultHibernateSessionFactoryManagerBundle(HibernateSessionFactoryBuilder builder, Provider<IdService> idServiceProvider, HibernateSessionFactoryDescriptor... descriptors) {
        addFactoryDescriptors(builder, idServiceProvider, descriptors);
    }

    public void addFactoryDescriptors(HibernateSessionFactoryBuilder builder, Provider<IdService> idServiceProvider, HibernateSessionFactoryDescriptor... descriptors) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < descriptors.length; i++) {
            HibernateSessionFactoryDescriptor hibernateSessionFactoryDescriptor = descriptors[i];
            bundle.add(new HibernateSessionFactoryManager(builder, idServiceProvider, hibernateSessionFactoryDescriptor));
        }
    }

    @Override
    public void close() {
        for (HibernateSessionFactoryManager hibernateSessionFactoryManager : bundle) {
            hibernateSessionFactoryManager.close();
        }
    }

    @Override
    public List<HibernateSessionFactoryManager> getBundle() {
        return Collections.unmodifiableList(bundle);
    }
}
