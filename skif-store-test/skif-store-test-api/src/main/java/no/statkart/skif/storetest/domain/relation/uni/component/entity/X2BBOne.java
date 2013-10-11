package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Pekt på av {@link X2EntityComponentOne}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2BBOne  extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X2BBOneId<?> getId() {
        return (X2BBOneId<?>) super.getId();
    }

    public Set<X2AAWithEntityComponentId<?>> findInvSomeBBIds() {
        return unwrap(finder(X2AAWithEntityComponentFinderService.class).findInvSomeBBIds(idAsSet()));
    }

    public Set<X2AAWithEntityComponent> findInvSomeBB() {
        return store.get(findInvSomeBBIds());
    }
}
