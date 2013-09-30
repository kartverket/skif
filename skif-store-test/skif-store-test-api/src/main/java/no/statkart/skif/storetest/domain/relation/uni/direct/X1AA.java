package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klasen har 3 forskjellige typer relasjoner
 * <ul>
 * <li>Enkelt relasjon {@link #getSomeBBId()} - med invers relasjon {@link X1BBOne#findInvSomeBBIds()}</li>
 * <li>Mange relasjon til {@code X1CCMany}: 'someCCs'(TODO) </li>
 * <li>En-til-en relasjon til {@code X1DDUnique}: 'myUniqueDD' (TODO</li>
 * </ul>
 * <p/>
 * De 3 relaterte klassene implementerer en finder for å navigerer relasjonen i motsatt rettning.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1AA extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    private X1BBOneId<?> someBBId;

//    private Set<X1CCMany> someCCs;
//    private X1DDUnique myUniqueDD;

//    private Set<X1EManyMany> x1EManyManySet;

    @Override
    public X1AAId<?> getId() {
        return (X1AAId<?>) super.getId();
    }

    public X1BBOneId<?> getSomeBBId() {
        return someBBId;
    }

    public X1BBOne getSomeBB() {
        return store.get(someBBId);
    }

    @Relation(type = RelationType.DIRECT, cardinality = Cardinality.ONE, name="someBB")
    public void setSomeBBId(X1BBOneId<?> someBBId) {
        this.someBBId = onChangeRelation(X1AAFinderService.Role.someBB, this.someBBId, someBBId);
    }
}
