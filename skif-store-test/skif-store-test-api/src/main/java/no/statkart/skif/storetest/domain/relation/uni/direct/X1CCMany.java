package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistory;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Boble som inngår i en sett-relasjon som ligger i {@link X1AA}. Boblen kan kun ligge i ett sett om gangen.
 * Relasjone er implementer i database via en foreign key som ligger i tabellen for X1Cmany.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X1CCMany extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X1CCManyId<?> getId() {
        return (X1CCManyId<?>) super.getId();
    }

    public Set<X1AAId<?>> findInvSomeCCsIds() {
        return unwrap(finder(X1AAFinderService.class).findInvSomeCCsIds(idAsSet()));
    }

    public Set<X1AA> findInvSomeCCs() {
        return store.get(findInvSomeCCsIds());
    }


}
