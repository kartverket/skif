package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.EntityBubbleComponent;
import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;

import java.util.Collection;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2EntityComponentOne implements EntityBubbleComponent<X2AAWithEntityComponent> {
    private Long id;
    private String text;
    private X2AAWithEntityComponent owner;
    private X2BBOneId<?> someBBId;
    private final Set<X2CCManyId<?>> someCCsIds= BubbleIds.newSet(this, X2AAWithEntityComponentFinderService.Role.someCCs);

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

    @Relation(type = RelationType.DIRECT, cardinality = Cardinality.ONE, name="someBB")
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
        return BubbleIds.getDelegate(someCCsIds);
    }

    public void setSomeCCsIds(Set<X2CCManyId<?>> someCCsIds) {
        BubbleIds.setFrom(this.someCCsIds, someCCsIds);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setSomeCCsIdsSet(Set<X2CCManyId<?>> someCCsIds) {
        BubbleIds.setDelegate(this.someCCsIds, someCCsIds);
    }

}
