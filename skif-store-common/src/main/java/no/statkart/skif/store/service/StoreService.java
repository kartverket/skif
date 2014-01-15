package no.statkart.skif.store.service;

import no.statkart.skif.service.annotation.SuppressSnapshotVersionMapping;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public interface StoreService {
    /**
     * Henter {@link BubbleObject} av type {@code <T>} for {@code id} av type {@code <I>}. Hvis {@code id} er null
     * returneres {@code null}.
     *
     * @param id {@link BubbleId} for objekt som skal lastes
     * @return BubbleObject for {@code id}
     * @throws no.statkart.skif.exception.ObjectNotFoundException
     *          kastes hvis {@code id} ikke finnes
     */
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id);

    /**
     * Henter en collecton av {@link BubbleObject}s av type {@code <T>} for {@code ids} av type {@code <I>}. Hvis
     * {@code ids} inneholder den samme id flere ganger returneres kun et objekt. Id-er som er null ignoreres.
     *
     * @param ids id-er som skal hentes
     * @return objekter for id-er i udefinert rekkefølge.
     * @throws no.statkart.skif.exception.ObjectNotFoundException
     *          hvis ikke alle id-er kunne lastes.
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids);

    /**
     * Henter en collecton av {@link BubbleObject}s av type {@code <T>} for {@code ids} av type {@code <I>}, uten å
     * kaste en exeception dersom objekter ikke kan finnes. Returlisten vil derfor kunne være kortere enn paramteren.
     * Hvis {@code ids} inneholder den samme id flere ganger returneres kun et objekt. Id-er som er null ignoreres.
     *
     * @param ids id-er som skal hentes
     * @return objekter for id-er i udefinert rekkefølge.
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids);

    /**
     * Henter alle versjoner av en id for et gitt tidsrom. Id-ene er sortert på versjon i stigende rekkefølge.
     *
     * @param id    id som det skal hentes historikk for
     * @param start starttidspunkt som er inkludert i intervallet
     * @param end   slutttidspunkt som avslutter intervallet og som ikke er inkludert
     * @return liste med id-er som ble funnet
     */
    @SuppressSnapshotVersionMapping
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);

    /**
     * Henter alle versjoner for et liste av id-er for et gitt tidsrom. Id-en returneres i en map som
     * har id-en som nøkkel og listen av funnede id-er som verdi. Listen inneholder id-ene sortert på versjon i
     * stigende rekkerfølge.
     *
     * @param ids   id-er som det skal hentes historikk for
     * @param start starttidspunkt som er inkludert i intervallet
     * @param end   slutttidspunkt som avslutter intervallet og som ikke er inkludert
     * @return map av funnede id-er
     */
    @SuppressSnapshotVersionMapping
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(Collection<I> ids, SnapshotVersion start, SnapshotVersion end);

    /**
     * Låser {@link BubbleObject} av type {@code <T>} for {@code id} av type {@code <I>} for kallende bruker og
     * returnerer objektet.
     *
     * @param id BubbleId for objekt som skal lastes
     * @return BubbleObject for {@code id}
     * @throws no.statkart.skif.exception.ObjectNotFoundException
     *          kastes hvis {@code id} ikke finnes
     * @throws no.statkart.skif.exception.LockedException
     *          kastes hvis objekt er låst av en annen bruker
     */
    public <T extends BubbleObject> T lock(BubbleId<? extends T> id);

    /**
     * Låser opp {@link BubbleObject} av type {@code <T>} for {@code id} av type {@code <I>} dersom det er låst av
     * kallende bruker. Hvis {@code id} er null returneres {@code null}.
     *
     * @param id BubbleId for objekt som skal låses opp
     */
    public <I extends BubbleId<?>> void unlock(I id);

    /**
     * Rerturnerer true dersom objektet er låst av kallende bruker
     *
     * @param id BubbleId for objekt som skal sjekkes om er låst
     * @return {@code true} dersom objektet er låst av kallende bruker, {@code false} hvis ikke låst eller låst av andre
     */
    public <I extends BubbleId<?>> boolean isLocked(I id);
}
