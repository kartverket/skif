package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * Boble som inngår i en sett-relasjon som ligger i {@link X1AA}. Boblen kan kun ligge i ett sett om gangen.
 * Relasjone er implementer i database via en foreignkey som ligger i tabellen for X1Cmany.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1CCMany extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X1CCManyId<?> getId() {
        return (X1CCManyId<?>) super.getId();
    }

}
