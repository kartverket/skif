package no.statkart.skif.storetest.service.store;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface StoreService extends no.statkart.skif.store.service.StoreService {

    /**
     * Henter BubbleObject av type {@code <T>} for {@code id} av type {@code <I>}. Hvis {@code id} er null
     * returneres {@code null}.
     * @param id BubbleId for objekt som skal lastes
     * @return BubbleObject for {@code id}
     * @throws ObjectNotFoundException kastes hvis {@code id} ikke finnes
     */
    @Override
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id) throws ObjectNotFoundException;

    /**
     * Henter en collecton av BubbleObjects av type {@code <T>} for {@code ids} av type {@code <I>}. Hvis
     * {@code ids} inneholder den samme id flere ganger returneres kun et objekt. Id'er som er null ignoreres.
     * @param ids id'er som skal hentes
     * @return objekter for id'er i udefinert rekkefølge.
     * @throws ObjectNotFoundException hvis ikke alle id'er kunne lastes.
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids) throws ObjectNotFoundException;

    /**
     * Henter alle versjoner av en id for et gitt tidsrom. Id'ene er sortert på versjon i stigende rekkefølge
     * @param id id som det skal hentes historikk for
     * @param start starttidspunkt som er inkludert i intervallet
     * @param end slutttidspunkt som avslutter intervallet og som ikke er inkludert
     * @return liste med id'er som ble funnet
     */
    @Override
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
    @Override
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end);

    /**
     * Låser BubbleObject av type {@code <T>} for {@code id} av type {@code <I>} for kallende bruker og returnerer
     * objektet.
     * @param id BubbleId for objekt som skal lastes
     * @return BubbleObject for {@code id}
     * @throws ObjectNotFoundException kastes hvis {@code id} ikke finnes
     * @throws LockedException kastes hvis objekt er låst av en annen bruker
     */
    @Override
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id) throws LockedException;

    /**
     * Låser opp BubbleObject av type {@code <T>} for {@code id} av type {@code <I>} dersom det er låst av kallende
     * bruker. Hvis {@code id} er null returneres {@code null}.
     * @param id BubbleId for objekt som skal låses opp
     */
    @Override
    public <I extends BubbleId<?>> void unlock(I id);

    /**
     * Rerturnerer true dersom objektet er låst av kallende bruker
     * @param id
     * @return
     */
    @Override
    public <I extends BubbleId<?>> boolean isLocked(I id);
}