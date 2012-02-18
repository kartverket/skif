package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

/**
 * Alle bobler (inkl koder og kodelister) i StoreTest applikasjonen implementerer dette interfacet
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestBubble extends BubbleObject {
    @Override
    StoreTestBubbleId<?> getId();
}
