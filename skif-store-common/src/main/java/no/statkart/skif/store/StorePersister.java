package no.statkart.skif.store;

import java.util.Collection;

/**
 * Interface for å hente og evt oppdatere objekter som håndteres av Store-rammeverket. StorePersister objektet kobles
 * inn i Store-rammeverket via et {@link StoreSessionPersisterChain} kjedeledd som avslutter kjeden.
 * <p>
 * Det er mulig å bruke flere StorePersister objekter samtidig ved å impelementere en {@link StorePersisterStrategy}
 * som velger hvilken StorePersister som skal brukes basert på AbstractBubbleId.
 * @author Henrik Fredholm
 * @since 0.3
 */
/**
 * Interface for å hente og evt oppdatere objekter som håndteres av Store-rammeverket. StorePersister objektet kobles
 * inn i Store-rammeverket via et {@link StoreSessionPersisterChain} kjedeledd som avslutter kjeden.
 * <p>
 * Det er mulig å bruke flere StorePersister objekter samtidig ved å impelementere en {@link StorePersisterStrategy}
 * som velger hvilken StorePersister som skal brukes basert på BubbleId.
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StorePersister<T extends BubbleObject, I extends BubbleId<? extends T>> {
     T get(I bubbleId);
     Collection<? extends T> get(Collection <? extends I> bubbleIds);

     void evict(I bubbleId);
     void evictAll();
}
