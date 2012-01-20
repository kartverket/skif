package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleObject;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionReadListener5 {
    <T extends BubbleObject> T onRegister(T bubbleObject);

}
