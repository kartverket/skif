package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klassen inngår i følgende relasjoner:
 * <ul>
 *     <li>{@link X1AA#getSomeBBId()} - med invers relasjon {@link X1BBOne#findInvSomeBBIds()}</li>
 * </ul>
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X1BBOne extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X1BBOneId<?> getId() {
        return (X1BBOneId<?>) super.getId();
    }

    public Set<X1AAId<?>> findInvSomeBBIds() {
        return unwrap(finder(X1AAFinderService.class).findInvSomeBBIds(idAsSet()));
    }

    public Set<X1AA> findInvSomeBB() {
        return store.get(findInvSomeBBIds());
    }

}
