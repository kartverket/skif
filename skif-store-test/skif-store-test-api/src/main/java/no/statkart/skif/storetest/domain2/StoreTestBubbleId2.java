package no.statkart.skif.storetest.domain2;

import no.statkart.skif.store2.BubbleId2;

/**
 * Alle bobleid'er (inkl kodeid'er implementerer dette interfacet
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTestBubbleId2<T extends StoreTestBubble2> extends BubbleId2<T> {
    public String getStringValue();
}
