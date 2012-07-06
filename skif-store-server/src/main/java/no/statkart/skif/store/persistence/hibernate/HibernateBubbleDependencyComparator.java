package no.statkart.skif.store.persistence.hibernate;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.store.BubbleDependencyComparator;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.BubbleObject;

import java.util.List;
import java.util.Map;

/**
 * Sorterer bobler i henhold til den rekkefølge som {@link HibernateSessionFactoryBuilder#addResource} ble
 * kallt for hver boble. Boblertyper som avhenger av andre bobletype sorteres slik at de kommer etter de bobletyper
 * de avhenger av. Rekkefølgen innen for en bobletype endres ikke.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class HibernateBubbleDependencyComparator implements BubbleDependencyComparator {
    final Map<Class<?>, Integer> bubbleDependencyMap;

    @Inject
    public HibernateBubbleDependencyComparator(HibernateSessionFactoryManagerBundle bundleManager) {
        final List<Class<?>> bubbleDependencyOrder = bundleManager.getBundle().get(0).getBubbleDependencyOrder();
        bubbleDependencyMap = Maps.newHashMap();
        for (int i = 0; i < bubbleDependencyOrder.size(); i++) {
            Class<?> aClass = bubbleDependencyOrder.get(i);
            bubbleDependencyMap.put(aClass, i);
        }
    }


    @Override
    public int compare(BubbleObject o1, BubbleObject o2) {
        // Håndtere også Lazy klasser
        int i1 = bubbleDependencyMap.get(o1.getId().getType());
        int i2 = bubbleDependencyMap.get(o2.getId().getType());
        if (i1<i2) return -1;
        if (i1==i2) return 0;
        return 1;
    }
}
