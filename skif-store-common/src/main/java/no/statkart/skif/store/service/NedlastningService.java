package no.statkart.skif.store.service;

import jakarta.annotation.Nullable;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Kontroll;

import java.util.Collection;
import java.util.List;

/**
 * Tjeneste for nedlastning av bobler av gitt type eller subtype .
 * <p/>
 * Hvilke subtyper som støttes for filtrering av bobleklasser er implementasjonsavhengig og ikke alle subtyper vil
 * nødvendigvis være støttet. Hvis en subtype ikke er støttet må supertypen brukes i stedet og ytereligere filtrering må
 * skje på klienten etter at boblen har blitt lastet over på klienten.
 * <p/>
 * For noen bobleklasser kan det finnes spesifikke filtre som filtrerer yterligere hva som returneres. Filtre som ikke
 * er relevant for en bobleklasse ignoreres mens filtre som er ukjente gir ImplementationException.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 *
 */
public interface NedlastningService {

    /**
     * Henter et antall id-er etter en gitt id for bobler av en gitt type eller subtype.
     *
     * @param id id før første id som skal hentes. Kan være null.
     * @param domainklasse domainklasse eller subtype herav som skal hentes.
     * @param filter boblespesifikt filter som kan reduserere yterligere hvilke id-er som returneres
     * @param maksAntall maksimalt objekter som skal hentes.
     * @return id-ene sortert i stigende rekkerføge; tomt liste hvis alle id-er har blitt hentet for gitt domainklasse og filter
     *
     */
    <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> domainklasse, @Nullable String filter, int maksAntall);

    /**
     * Henter et antall bobler etter en gitt id for bobler av en gitt type eller subtype
     *
     * @param id id før første boble som skal hentes. Kan være null.
     * @param domainklasse domainklasse eller subtype herav som skal hentes.
     * @param filter boblespesifikt filter som kan reduserere yterligere hvilke id-er som returneres
     * @param maksAntall maksimalt objekter som skal hentes.
     * @return bobler sortert i stigende rekkerføge; tomt liste hvis alle bobler har blitt hentet for gitt domainklasse og filter
     *
     */
    <T extends BubbleObject> List<T> findObjekterEtterId(@Nullable BubbleId<? extends T> id, Class<T> domainklasse, @Nullable String filter, int maksAntall);

    /**
     * Beregner kontroll for bobler med id-er i intervall "]fraId, tilId]"
     *
     * @param fraId første id i intervall. Hvis null tas første id med i beregningen
     * @param tilId siste id i intervall. Hvis null tas siste id  med i beregningen
     * @param domainklasse angir filter for domainklasse som skal inkluderes i beregningen.
     * @param filter boblespesifikt filter som kan reduserere yterligere hvilke id-er som returneres
     * @return beregnet kontroll
     *
     */
    <T extends BubbleObject> Kontroll calcObjektkontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> domainklasse, @Nullable String filter);

    /**
     * Beregner kontroll for bobler med id-er i liste
     *
     * @param ids id-er som skal danne grunnlag for beregningen
     * @param domainklasse angir domainklasse som skal danne grunnlag for beregningen.
     * @return beregnet kontroll
     *
     */
    <I extends BubbleId<? extends T>, T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<I> ids, Class<T> domainklasse);

}
