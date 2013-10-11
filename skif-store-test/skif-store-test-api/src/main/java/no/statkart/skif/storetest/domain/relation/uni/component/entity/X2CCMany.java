package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import javax.annotation.Nullable;

/**
 * Pekt på av {@link X2EntityComponentOne}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2CCMany extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    @Override
    public X2CCManyId<?> getId() {
        return (X2CCManyId<?>) super.getId();
    }

    public X2AAWithEntityComponentId<?> findInvSomeCCsIds() {
        return unwrap(finder(X2AAWithEntityComponentFinderService.class).findInvSomeCCsIds(idAsSet()));
    }

    @Nullable
    public X2AAWithEntityComponent findInvSomeCCs() {
        return store.get(findInvSomeCCsIds());
    }
}
