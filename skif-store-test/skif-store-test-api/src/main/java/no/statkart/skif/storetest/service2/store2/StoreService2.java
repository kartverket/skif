package no.statkart.skif.storetest.service2.store2;

import no.statkart.skif.storetest.domain2.StoreTestBubble2;
import no.statkart.skif.storetest.domain2.StoreTestBubbleId2;

import java.util.List;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface StoreService2 {

    /**
     * Full doc here
     * @param id
     * @param <T>
     * @param <I>
     * @return
     */
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> T getObject(I id);
    public <T extends StoreTestBubble2, I extends StoreTestBubbleId2<? extends T>> List<T> getObjects(List<I> ids);

}