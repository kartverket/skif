package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.store.InverseRelationCollector;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.store.relation.cache.annotation.Cardinality;
import no.statkart.skif.store.relation.cache.annotation.Relation;
import no.statkart.skif.store.relation.cache.annotation.RelationType;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klasen har 5 forskjellige typer relasjoner
 * <ul>
 * <li>Enkelt relasjon {@link #getSomeBBId()} - med invers relasjon {@link X1BBOne#findInvSomeBBIds()}</li>
 * <li>Mange relasjon til {@code X1CCMany}: 'someCCs' </li>
 * <li>En-til-en relasjon til {@code X1DDUnique}: 'myUniqueDD' (TODO)</li>
 * <li>En verdi som brukes som en unik index for klassen</li>
 * <li>En verdi som brukes son en nonunik index for klassen</li>
 * </ul>
 * <p/>
 * De 3 relaterte klassene implementerer en finder for å navigerer relasjonen i motsatt rettning. Det finnes ingen
 * klasser for indexene. De bruker String klassen. Oppslag for disse skjer via servicefindermetoder.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1AA extends AbstractRelationTestBubble implements InverseRelationParticipation {
    private static final long serialVersionUID = 1L;

    private X1BBOneId<?> someBBId;
    private final Set<X1CCManyId<?>> someCCsIds = Bubbles.newSet(this, X1AAFinderService.Role.someCCs);

    private String uniqueOnX1AA;
    private String nonUniqueOnX1AA;

//    private X1DDUnique myUniqueDD;

//    private Set<X1EManyMany> x1EManyManySet;

    @Override
    public void collectInverseRelationValues(InverseRelationCollector collector) {
        collector.put(X1AAFinderService.Role.someBB, someBBId);
        collector.put(X1AAFinderService.Role.someCCs, someCCsIds);
        collector.put(X1AAFinderService.Role.uniqueOnX1AA, uniqueOnX1AA);
        collector.put(X1AAFinderService.Role.nonUniqueOnX1AA, nonUniqueOnX1AA);
    }

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

    public void setSomeBBId(X1BBOneId<?> someBBId) {
        this.someBBId = Bubbles.onChangeRelation(this, X1AAFinderService.Role.someBB, this.someBBId, someBBId);
    }


    public Set<X1CCManyId<?>> getSomeCCsIds() {
        return someCCsIds;
    }

    public void setSomeCCsIds(Set<X1CCManyId<?>> someCCsIds) {
        Bubbles.setFrom(this.someCCsIds, someCCsIds);
    }

    public <T extends Collection<? super X1CCManyId<?>>> T getSomeCCsIds(T targetCollection) {
        targetCollection.addAll(someCCsIds);
        return targetCollection;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private Set<X1CCManyId<?>> getSomeCCsIdsSet() {
        return Bubbles.getDelegate(someCCsIds);
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setSomeCCsIdsSet(Set<X1CCManyId<?>> someCCsIds) {
        Bubbles.setDelegate(this.someCCsIds, someCCsIds);
    }

    public String getUniqueOnX1AA() {
        return uniqueOnX1AA;
    }

    public void setUniqueOnX1AA(String uniqueOnX1AA) {
        this.uniqueOnX1AA = Bubbles.onChangeRelation(this, X1AAFinderService.Role.uniqueOnX1AA, this.uniqueOnX1AA, uniqueOnX1AA);
    }

    public String getNonUniqueOnX1AA() {
        return nonUniqueOnX1AA;
    }

    public void setNonUniqueOnX1AA(String nonUniqueOnX1AA) {
        this.nonUniqueOnX1AA = Bubbles.onChangeRelation(this, X1AAFinderService.Role.nonUniqueOnX1AA, this.nonUniqueOnX1AA, nonUniqueOnX1AA);
    }
}
