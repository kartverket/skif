package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import jakarta.annotation.Nullable;
import java.util.Collection;

/**
 * Pekt på av {@link X2EntityComponentOne}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2CCMany extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    /**
     * Eksplisitt modellert property for invers relasjon av "X2AA.entityComponentOne --someCCs-> X2CCMany". Se {@link X2AAWithEntityComponentFinderService#findInvSomeCCsId}.
     */
    private final InverseRelation<X2AAWithEntityComponentId<?>> invSomeCCsId = InverseRelation.create(this, X2AAWithEntityComponentFinderService.Role.someCCs);

    @Override
    public X2CCManyId<?> getId() {
        return (X2CCManyId<?>) super.getId();
    }

    public X2AAWithEntityComponentId<?> findInvSomeCCsIds() {
        return unwrap(finder(X2AAWithEntityComponentFinderService.class).findInvSomeCCsId((Collection<? extends X2CCManyId<?>>) idAsSet()));
    }

    @Nullable
    public X2AAWithEntityComponent findInvSomeCCs() {
        return store.get(findInvSomeCCsIds());
    }

    public InverseRelation<X2AAWithEntityComponentId<?>> getInvSomeCCsId() {
        return invSomeCCsId;
    }

    public void setInvSomeCCsId(InverseRelation<X2AAWithEntityComponentId<?>> invSomeCCsId) {
        this.invSomeCCsId.setFrom(invSomeCCsId);
    }

    public X2AAWithEntityComponent getInvSomeCCs() {
        return store.get((this.invSomeCCsId.get()));
    }
}
