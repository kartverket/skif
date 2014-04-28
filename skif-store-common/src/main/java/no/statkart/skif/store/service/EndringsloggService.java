package no.statkart.skif.store.service;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.store.endringslogg.Endringer;
import no.statkart.skif.store.endringslogg.ReturnerBobler;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Tjeneste for lesing av endringslogg for bobler som er endret.
 * <P/>
 * Hvilke subtyper som støttes for filtrering av bobleklasser er implementasjonsavhengig og ikke alle subtyper vil
 * nødvendigvis være støttet. Hvis en subtype ikke er støttet må supertypen brukes i stedet og ytereligere filtrering må
 * skje på klienten etter at boblen har blitt lastet over på klienten.
 * <P/>
 * For noen bobleklasser kan det finnes spesifikke filtre som filtrerer yterligere hva som returneres. Filtre som ikke
 * er relevant for en bobleklasse ignoreres mens filtre som er ukjente gir ImplementationException.
 *
 * @author Henrik Fredholm
 * @since 2.5.0
 */
public interface EndringsloggService<E extends AbstractEndring<?, ?>> {
    /**
     * Finner siste endringId uavhengig av bobleklasse og filter.
     *
     * @return endringId
     */
    @Nullable
    public <I extends AbstractEndringId<?>> I findSisteEndringId();

    /**
     * Henter alle endringsobjekter etter gitt {@code id} for bobler av gitt type eller subtype. Endringsobjekter hørende til {@code id} er ikke inkludert.
     *
     * @param id id før første id som skal hentes. Kan være null.
     * @param bobleklasse bobleklasse eller subtype herav for endringer som skal hentes.
     * @param filter boblespesifikt filter som kan reduserere yterligere hvilke endringer som returneres
     * @param returnerBobler angir om metoden skal returnere bobler i tillegg til endringer.
     * @param maksAntall maksimalt endringer som skal hentes.
     * @return endringer som ble funnet med indikator for om det finnes fler endringer for gitt bobleklasse og filter
     */
    public Endringer<E> findEndringer(@Nullable AbstractEndringId<?> id, Class<? extends BubbleObject> bobleklasse, @Nullable String filter, ReturnerBobler returnerBobler, int maksAntall);

    /**
     * Beregner kontroll for endringsobjekter
     *
     * @param id id før første id som skal hentes, kan være null
     * @param bobleklasse klasse som skal hentes. Hvilke bobleklasse som støttes er implementasjonsavhengig
     * @param filter angir filter på endring
     * @param antall     maksimalt antall endringer som skal hentes
     * @return beregnet kontroll
     *
     */
    public <T extends BubbleObject> Kontroll calcEndringskontroll(@Nullable AbstractEndringId<?> id, Class<T> bobleklasse, @Nullable String filter, int antall);

    /**
     * Beregner kontroll for bobler med id-er i liste
     *
     * @param ids id-er som skal danne grunnlag for beregningen
     * @param bobleklasse klasse som skal hentes. Hvilke bobleklasse som støttes er implementasjonsavhengig
     * @return beregnet kontroll
     *
     */
    public <T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<? extends BubbleId<?>> ids, Class<T> bobleklasse);
}
