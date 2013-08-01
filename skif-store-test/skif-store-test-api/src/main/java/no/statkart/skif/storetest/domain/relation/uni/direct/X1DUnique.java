package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1DUnique  extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    @Override
    public X1DUniqueId<?> getId() {
        return (X1DUniqueId<?>) super.getId();
    }

    public X1A findA() {
        return null;
    }

}
