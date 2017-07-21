package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.*;

import java.util.Collection;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2EntityComponentOne implements EntityBubbleComponent<X2AAWithEntityComponent>, InverseRelationParticipation {
    private Long id;
    private String text;
    private X2AAWithEntityComponent owner;
    private X2BBOneId<?> someBBId;
    private final Set<X2CCManyId<?>> someCCsIds= Bubbles.newSet(this, X2AAWithEntityComponentFinderService.Role.someCCs);

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
        collector.put(X2AAWithEntityComponentFinderService.Role.someBB, someBBId);
        collector.put(X2AAWithEntityComponentFinderService.Role.someCCs, someCCsIds);
    }

    @Override
    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public X2AAWithEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(X2AAWithEntityComponent owner) {
        this.owner = Components.checkSetOwner(this, this.owner,owner);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public X2BBOneId<?> getSomeBBId() {
        return someBBId;
    }

    public X2BBOne getSomeBB() {
        return Components.getOwningBubbleNullSafe(this).store().get(someBBId);
    }

    public void setSomeBBId(X2BBOneId<?> someBBId) {
        this.someBBId = Components.onChangeRelation(this, X2AAWithEntityComponentFinderService.Role.someBB, this.someBBId, someBBId);
    }

    public Set<X2CCManyId<?>> getSomeCCsIds() {
        return someCCsIds;
    }

    public <T extends Collection<? super X2CCManyId<?>>> T getSomeCCsIds(T targetCollection)  {
        targetCollection.addAll(someCCsIds);
        return targetCollection;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Set<X2CCManyId<?>> getSomeCCsIdsSet() {
        return Bubbles.getDelegate(someCCsIds);
    }

    public void setSomeCCsIds(Set<X2CCManyId<?>> someCCsIds) {
        Bubbles.setFrom(this.someCCsIds, someCCsIds);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setSomeCCsIdsSet(Set<X2CCManyId<?>> someCCsIds) {
        Bubbles.setDelegate(this.someCCsIds, someCCsIds);
    }

}
