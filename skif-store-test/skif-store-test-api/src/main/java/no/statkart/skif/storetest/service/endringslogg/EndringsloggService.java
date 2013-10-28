package no.statkart.skif.storetest.service.endringslogg;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.Kontroll;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * Tjeneste for lesing av endringslogg.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface EndringsloggService {
    /**
     * Finner siste endringsnummer totalt.
     *
     * @return siste endringenummer
     */
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion);

    /**
     * Henter alle endringer etter gitt endringsnummer. Endringen med gitt endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param endringsklasse angir filter for endringsklasse
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @param snapshotVersion historisk tidspunkt for spørring
     * @return endringene, sortert etter stigende endringsnummer
     */
    public <E extends Endring> List<E> findEndringerEtterEndringsnummer(long endringsnummer, Class<E> endringsklasse, int maksAntall, SnapshotVersion snapshotVersion);

    /**
     * Henter alle id-er etter en gitt id.
     *
     * @param id id før første id som skal hentes
     * @param klassefilter angir filter for bobleklasse som skal hentes.
     * @param maksAntall maksimalt antall endringer som skal hentes
     * @param snapshotVersion historisk tidspunkt for spørring
     * @return id-ene sortert i stigende rekkerføge
     *
     * TODO: Vurdere å legge denne tjeneste i egen service InitiellnedlastService
     */
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> klassefilter, int maksAntall, SnapshotVersion snapshotVersion);

    /**
     * Beregner kontroll for bobler med id-er i intervall "]fraId, tilId]"
     * @param fraId første id i intervall. Hvis null tas første id i systemet med i beregningen
     * @param tilId siste id i intervall. Hvis null  tas siste id i systemet med i beregningen
     * @param klassefilter angir filter for bobleklasse som skal inkluderes i beregningen.
     * @param snapshotVersion historisk tidspunkt for spørring
     * @return beregnet kontroll
     *
     * TODO: Vurdere å legge denne tjeneste i egen service KontrollService
     */
    public <K extends Kontroll, I extends StoreTestBubbleId<T>, T extends StoreTestBubble> K calcKontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId,  Class<T> klassefilter, SnapshotVersion snapshotVersion);
    /**
     * Beregner kontroll for bobler med id-er i liste
     *
     * @param ids id-er som skal danne grunnlag for beregningen
     * @param klassefilter angir filter for bobleklasse som skal inkluderes i beregningen.
     * @param snapshotVersion historisk tidspunkt for spørring
     * @return beregnet kontroll
     *
     * TODO: Vurdere å legge denne tjeneste i egen service KontrollService
     */
    public <K extends Kontroll, I extends StoreTestBubbleId<? extends T>, T extends StoreTestBubble> K calcKontrollForList(Collection<I> ids,  Class<T> klassefilter,  SnapshotVersion snapshotVersion);
}
