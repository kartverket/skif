package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.StoreServer5;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreUpdateServiceImpl implements StoreUpdateService {
    @Inject
    StoreServer5 store;

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId) throws ObjectNotFoundException {
        return store.lock(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds) {
        return store.lock(bubbleIds);
    }
}
