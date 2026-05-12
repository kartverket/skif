package no.statkart.skif.storetest.domain.relation.uni.direct;

import jakarta.annotation.Nullable;
import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubble;

import java.util.Collection;

/**
 * Klasse for å test unidireksjonelle relasjoner. Klassen inngår i følgende relasjon:
 * <ul>
 * <li>{@link X1AA#getSomeCCsIds()} - med invers relasjon {@link X1AAFinderService#findInvSomeCCsId}</li>
 * </ul>
 *
 * Relasjonen mellom {@link X1AA} og {@link X1CCMany} er implementer i database via en koblingstabell `X1AAForX1CCMany`.
 * Derfor kan relasjonen kun inngå ett sett om gangen. Kardinaliteten på invers relasjonen er derfor {@code ONE}.
 *
 * <P>Invers relasjonen er modellert både via en eksplisitt property og via en egen findermetode på klassen.
 * Findermetoden er egentlig overflødig og finnes kun for test formål samt for demonstrasjon av hvordan slike
 * findermetoder skal implementeres. Findermetoden spiller kun indirekte sammen med property {@link #invSomeCCsId}
 * ved at den bruker samme underliggende mekanisme for caching av relasjonen. For at relasjonen skal bli cachet må
 * caching i {@code StoreRelationCache} enables først.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X1CCMany extends AbstractRelationTestBubble {
    private static final long serialVersionUID = 1L;

    private final InverseRelation<X1AAId> invSomeCCsId = InverseRelation.create(this, X1AAFinderService.Role.someCCs);
    @Override
    public X1CCManyId<?> getId() {
        return (X1CCManyId<?>) super.getId();
    }

    public X1AAId<?> findInvSomeCCsIds() {
        return unwrap(finder(X1AAFinderService.class).findInvSomeCCsId((Collection<? extends X1CCManyId<?>>) idAsSet()));
    }

    @Nullable
    public X1AA findInvSomeCCs() {
        return store.get(findInvSomeCCsIds());
    }

    public InverseRelation<X1AAId> getInvSomeCCsId() {
        return invSomeCCsId;
    }

    @SuppressWarnings("UnusedDeclaration") // WS-Mapping
    private void setInvSomeCCsId(InverseRelation<X1AAId> invSomeCCsId) {
        this.invSomeCCsId.setFrom(invSomeCCsId);
    }

    public X1AA getInvSomeCCs() {
        X1AAId<?> result = invSomeCCsId.get();
        return store.get(result);
    }
}

