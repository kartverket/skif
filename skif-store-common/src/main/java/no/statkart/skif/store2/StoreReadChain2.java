package no.statkart.skif.store2;

import java.util.List;

/**
 * @author Henrik Fredholm
 */
public interface StoreReadChain2 {
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> T get(I bubbleId);
    public <T extends BubbleObject2, I extends BubbleId2<? extends T>> List<T> get(List<I> bubbleIds);
}
