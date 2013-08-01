package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Boble som har en direkte peker til seg fra en eller flere {@link X1A}
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1BOne extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X1BOneId<?> getId() {
        return (X1BOneId<?>) super.getId();
    }

    public Set<X1A> findAs() {
        return null;
    }
}
