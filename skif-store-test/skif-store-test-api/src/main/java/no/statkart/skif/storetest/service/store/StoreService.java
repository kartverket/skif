package no.statkart.skif.storetest.service.store;

import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.List;
import java.util.Map;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface StoreService extends no.statkart.skif.store.StoreService {

    /**
     * Henter BubbleObject av type {@code <T>} for {@code id} av type {@code <I>}. Hvis {@code id} er null
     * returneres {@code null}.
     * @param id BubbleId for objekt som skal lastes
     * @return BubbleObject for {@code id}
     * @throws ObjectNotFoundException kastes hvis {@code id} ikke finnes
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id) throws ObjectNotFoundException;

    /**
     * Henter en uordnet liste av BubbleObjects av type  {@code <T>} for {@code ids} av type {@code <I>}. Hvis
     * {@code ids} inneholder den samme id flere ganger returneres kun et objekt. Id'er som er null ignoreres.
     * @param ids id'er som skal hentes
     * @return liste av objekter som ble funnet i udefinert rekkefølge.
     * @throws ObjectNotFoundException hvis ikke alle id'er kunne lastes.
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getObjects(List<I> ids) throws ObjectNotFoundException;

    /**
     * Henter alle versjoner av en id for et gitt tidsrom. Id'ene er sortert på versjon i stigende rekkefølge
     * @param id id som det skal hentes historikk for
     * @param start starttidspunkt som er inkludert i intervallet
     * @param end slutttidspunkt som avslutter intervallet og som ikke er inkludert
     * @return liste med id'er som ble funnet
     */
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);

    /**
     *  Henter alle versjoner for et liste av id'er for et gitt tidsrom. Id'en returneres i en map som
     *  har id'en som nøkkel og listen av fundne id'er som verdi. Listen inneholder id'ene sortert på versjon i i
     *  stigende rekkerfølge.
     * @param ids id'er som det skal hentes historikk for
     * @param start starttidspunkt som er inkludert i intervallet
     * @param end slutttidspunkt som avslutter intervallet og som ikke er inkludert
     * @return map av fundne id'er
     */
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);
}