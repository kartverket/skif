package no.statkart.skif.storetest.service.store;

import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;

import java.util.List;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface StoreService {

    /**
     * Full doc here
     * @param id
     * @param <T>
     * @param <I>
     * @return
     */
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> T getObject(I id);
    public <T extends StoreTestBubble, I extends StoreTestBubbleId<? extends T>> List<T> getObjects(List<I> ids);

}