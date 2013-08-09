package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class GeometricElement extends AbstractBubbleObject implements StoreTestBubble{

    @Override
    public GeometricElementId<GeometricElement> getId() {
        return (GeometricElementId<GeometricElement>) super.getId();
    }

}
