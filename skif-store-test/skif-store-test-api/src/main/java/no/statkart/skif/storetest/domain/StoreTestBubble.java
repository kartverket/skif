package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleObject;

/**
 * Alle bobler i StoreTest applikasjonen implementerer dette interfacet
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestBubble extends BubbleObject {
    // Java tillater ikke overskrivning av return type som kan føre til diamanthieraki
    //@Override
    //StoreTestBubbleId<?> getId();
}
