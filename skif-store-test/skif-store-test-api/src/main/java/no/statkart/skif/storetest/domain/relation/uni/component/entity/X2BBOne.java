package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.InverseRelation;
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
    /**
     * Eksplisitt modellert property for invers relasjon av "X2AA.entityComponentOne --someBB-> X1BBOne". Se {@link X2AAWithEntityComponentFinderService#findInvSomeBBIds}.
     */
    private final InverseRelation<Set<X2AAWithEntityComponentId<?>>> invSomeBBIds = InverseRelation.create(this, X2AAWithEntityComponentFinderService.Role.someBB);


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

    public InverseRelation<Set<X2AAWithEntityComponentId<?>>> getInvSomeBBIds() {
        return invSomeBBIds;
    }

    public void setInvSomeBBIds(InverseRelation<Set<X2AAWithEntityComponentId<?>>> invSomeBBIds) {
        this.invSomeBBIds.setFrom(invSomeBBIds);
    }

    public Set<X2AAWithEntityComponent> getInvSomeBB() {
        return store.get(invSomeBBIds.get());
    }

    public Set<X2AAWithEntityComponentId<?>> findInvRole1BBOneIds() {
        return unwrap(finder(X2AAWithEntityComponentFinderService.class).findInvRole1BBIds(idAsSet()));
    }

}
