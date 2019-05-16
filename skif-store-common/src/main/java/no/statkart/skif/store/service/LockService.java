package no.statkart.skif.store.service;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;

/**
 * Tjenester for å låse og låse opp objekter. Utfyller oppdateringsfunksjonaliteten i Store.
 */
public interface LockService {
    /**
     * Låser {@link BubbleObject} av type {@code <T>} for {@code id} av type {@code <I>} for kallende bruker og
     * returnerer objektet.
     *
     * @param id BubbleId for objekt som skal låses og lastes
     * @return BubbleObject for {@code id}
     * @throws no.statkart.skif.exception.ObjectNotFoundException kastes hvis {@code id} ikke finnes
     * @throws no.statkart.skif.exception.LockedException         kastes hvis objekt er låst av en annen bruker
     */
    <T extends BubbleObject> T lock(BubbleId<? extends T> id);

    /**
     * Låser {@link BubbleObject} av type {@code <T>} for {@code id} av type {@code <I>} for kallende bruker og
     * returnerer objektet.
     *
     * @param ids BubbleIds for objekter som skal låses og lastes
     * @return BubbleObject for {@code ids}
     * @throws no.statkart.skif.exception.ObjectsNotFoundException kastes hvis noen {@code ids} ikke finnes
     * @throws no.statkart.skif.exception.LockedException          kastes hvis et objekt er låst av en annen bruker
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockForList(Collection<I> ids);

    /**
     * Låser opp {@link BubbleObject} for {@code id} av type {@code <I>} dersom det er låst av kallende bruker.
     *
     * @param id BubbleId for objekt som skal låses opp
     */
    <I extends BubbleId<?>> void unlock(I id);

    /**
     * Låser opp {@link BubbleObject}-er for gitte {@code ids} dersom det er låst av kallende bruker.
     *
     * @param ids BubbleIds for objekter som skal låses opp
     */
    void unlockForList(Collection<? extends BubbleId<?>> ids);

    /**
     * Rerturnerer true dersom objektet er låst av kallende bruker
     *
     * @param id BubbleId for objekt som skal sjekkes om er låst
     * @return {@code true} dersom objektet er låst av kallende bruker, {@code false} hvis ikke låst eller låst av andre
     */
    <I extends BubbleId<?>> boolean isLocked(I id);
}
