package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Set;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klassen inngår i følgende relasjon:
 * <ul>
 * <li>{@link X1AA#getSomeBBId()} - med invers relasjon {@link X1AAFinderService#findInvSomeBBIds}</li>
 * </ul>
 *
 * Relasjonen mellom {@link X1AA} og {@link X1BBOne} er implementer i database via en foreign key som ligger i tabellen
 * for {@code X1AA}. Derfor kan mange forskjellige {@code X1AA}-er peke på samme {@code X1BBOne}. Kardinaliteten på
 * invers relasjonen er derfor {@code Many}.
 *
 * <P>Invers relasjonen er modellert både via en eksplisitt property og via en egen findermetode på klassen. Findermetoden
 * er egentlig overføldig og finnes kun for test formål samt for demonstrasjon av hvordan slike findermetoder skal
 * implementeres. Findermetoden spiller kun indirekte sammen med property {@link #invSomeBBIds} ved at den bruker samme
 * underliggende mekanisme for caching av relasjonenen. For at relasjonen skal bli cachet må caching i
 * {@code StoreRelationCache} enables først.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X1BBOne extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    /**
     * Eksplisitt modellert property for invers relasjon av "X1AA---someBB-> X1BBOne". Se {@link X1AAFinderService#findInvSomeBBIds}.
     */
    private final InverseRelation<Set<X1AAId<?>>> invSomeBBIds = InverseRelation.create(this, X1AAFinderService.Role.someBB);

    @Override
    public X1BBOneId<?> getId() {
        return (X1BBOneId<?>) super.getId();
    }

    /**
     * Findermetode for invers relasjon av "X1AA---someBB-> X1BBOne". Siden {@code X1BBOne} også har en eksplisitt
     * modellert property for samme relasjon {@link #invSomeBBIds} er det egentlig unødvendig også å ha denne findermetode på
     * på klassen. Metoden finnes kun for demonstrasjon og test av hvordan findermetoder for invers relasjoner skal
     * implementeres på klasser. Metoder spiller kun indirekte sammen med property {@link #invSomeBBIds} ved at de bruker
     * samme underliggende mekanisme ({@code StoreRelationCache}) for caching av relasjonenen. For at relasjonen skal
     * bli cachet må caching enables først.
     */
    public Set<X1AAId<?>> findInvSomeBBIds() {
        return unwrap(finder(X1AAFinderService.class).findInvSomeBBIds(idAsSet()));
    }

    /**
     * Hjelpemetode for enkelt å kunne få tak i relaterte bobler
     */
    public Set<X1AA> findInvSomeBB() {
        return store.get(findInvSomeBBIds());
    }

    public InverseRelation<Set<X1AAId<?>>> getInvSomeBBIds() {
        return invSomeBBIds;
    }

    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    private void setInvSomeBBIds(InverseRelation<Set<X1AAId<?>>> invSomeBBIds) {
        invSomeBBIds.setFrom(invSomeBBIds);

    }

    /**
     * Hjelpemetode for enkelt å kunne få tak i relaterte bobler
     * @return
     */
    public Set<X1AA> getInvSomeBB() {
        return store.get(invSomeBBIds.get());
    }

}
