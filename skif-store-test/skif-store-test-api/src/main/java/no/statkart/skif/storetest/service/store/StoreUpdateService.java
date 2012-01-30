package no.statkart.skif.storetest.service.store;

import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreUpdateService {
    /**
     * Låser {@code bubbleId} for kallende bruker og returnerer tilhørende BubbleObject instans
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lockObject(@Nullable I bubbleId) throws ObjectNotFoundException;
    /**
     * Låser en collection av {@code bubbleId}s for kallende bruker og returnerer tilhørende BubbleObject instanser
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lockObjects(Collection<I> bubbleIds);
}
