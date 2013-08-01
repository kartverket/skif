package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;

/**
 * Boble som inngår i en sett-relasjon som ligger i {@link X1A}. Boblen kan kun ligge i ett sett om gangen.
 * Relasjone er implementer i database via en foreignkey som ligger i tabellen for X1Cmany.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1CMany extends AbstractStoreTestBubbleWithHistory {
    private static final long serialVersionUID = 1L;

    @Override
    public X1CManyId<?> getId() {
        return (X1CManyId<?>) super.getId();
    }

    public X1A findA() {
        return null;
    }

}
