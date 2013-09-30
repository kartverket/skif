package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1DDUnique extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X1DDUniqueId<?> getId() {
        return (X1DDUniqueId<?>) super.getId();
    }

}
