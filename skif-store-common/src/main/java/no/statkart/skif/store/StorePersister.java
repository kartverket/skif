package no.statkart.skif.store;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for å hente og evt oppdatere objekter som håndteres av Store-rammeverket. StorePersister objektet kobles
 * inn i Store-rammeverket via et {@link no.statkart.skif.store.StoreSessionPersisterChain} kjedeledd som avslutter kjeden.
 * <p>
 * Det er mulig å bruke flere StorePersister objekter samtidig ved å impelementere en {@link StorePersisterStrategy}
 * som velger hvilken StorePersister som skal brukes basert på AbstractBubbleId.
 * @author Henrik Fredholm
 * @since 2.0
 */
/**
 * Interface for å hente og evt oppdatere objekter som håndteres av Store-rammeverket. StorePersister objektet kobles
 * inn i Store-rammeverket via et {@link StoreSessionPersisterChain} kjedeledd som avslutter kjeden.
 * <p>
 * Det er mulig å bruke flere StorePersister objekter samtidig ved å impelementere en {@link StorePersisterStrategy}
 * som velger hvilken StorePersister som skal brukes basert på BubbleId.
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StorePersister<T extends BubbleObject, I extends BubbleId<? extends T>> {
     T get(I bubbleId);
     Map<SnapshotVersion, Collection<? extends T>> get(Map<SnapshotVersion, Collection<? extends I>> bubbleIdsForSnapshotMap);

     void evict(I bubbleId);
     void evictAll();
}
