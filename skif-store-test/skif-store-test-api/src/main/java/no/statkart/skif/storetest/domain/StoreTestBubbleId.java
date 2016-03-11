package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleId;

/**
 * Alle bobleid'er (inkl kodeid'er implementerer dette interfacet
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTestBubbleId<T extends StoreTestBubble> extends BubbleId<T> {

    String getStringValue();

}
