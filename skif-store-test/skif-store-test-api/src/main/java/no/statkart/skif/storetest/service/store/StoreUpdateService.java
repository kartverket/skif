package no.statkart.skif.storetest.service.store;

import jakarta.annotation.Nullable;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreUpdateService {

    /**
     * Låser {@code bubbleId} for kallende bruker og returnerer tilhørende BubbleObject instans
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> T lockObject(@Nullable I bubbleId) throws ObjectNotFoundException;

    /**
     * Låser en collection av {@code bubbleId}s for kallende bruker og returnerer tilhørende BubbleObject instanser
     */
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockObjects(Collection<I> bubbleIds);

    /**
     * Committer en transfer. Dette er en metode som en vanlig applikasjon normalt ikke vil implementere fordi det
     * er ønskelig med brukstilfellespesifikk validering i forbindelse med oppdatering.
     */
    void saveTransfer(UnitOfWorkTransfer transfer);

}
