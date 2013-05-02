package no.statkart.skif.store.persistence.hibernate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleDependencyComparator;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Map;

/**
 * Sorterer bobler i henhold til sorteringsindex definert i {@link HibernateSessionFactoryBuilder}.
 * Boblertyper som avhenger (refererer) andre bobletype sorteres slik at de kommer etter de bobletyper
 * de avhenger av. Analysen gjøres på klassenivå og tar ikke hensyn til faktiske referanser i bobleinstanser.
 * Den innbyrdes rekkefølge på bobler som har samme sorteringsindex endres ikke
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class HibernateBubbleDependencyComparator implements BubbleDependencyComparator {
     final Map<Class<? extends BubbleObject>, Integer> bubbleClassDependencyIndex;

    @Inject
    public HibernateBubbleDependencyComparator(HibernateSessionFactoryManagerBundle bundleManager) {
        bubbleClassDependencyIndex = bundleManager.getBundle().get(0).getBubbleClassDependencyIndex();
    }


    @Override
    public int compare(BubbleObject o1, BubbleObject o2) {
        // Håndtere også Lazy klasser
        int i1 = getIndex(o1);
        int i2 = getIndex(o2);
        if (i1<i2) return -1;
        if (i1==i2) return 0;
        return 1;
    }

    private Integer getIndex(BubbleObject bubbleObject) {
        final BubbleId<?> bubbleId = bubbleObject.getId();
        final Integer index = bubbleClassDependencyIndex.get(bubbleId.getType());
        if (index==null) {
            throw new ImplementationException("No dependency index defined for BubbleObject class (all subclasses must be specified in HibernateSessionFactoryBuilder): " + bubbleId.getType());
        }
        return index;
    }
}
