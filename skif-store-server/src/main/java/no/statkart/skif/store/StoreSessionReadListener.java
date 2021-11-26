package no.statkart.skif.store;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 */
public interface StoreSessionReadListener {
    /**
     * Kalles etter at en boble har blitt lastet, rett før den blir registrert i Store.
     *
     * ReadListener kan kaste {@link no.statkart.skif.exception.PermissionDeniedException} hvis brukeren ikke har rett
     * til å laste instansen. {@link Store} kaster normalt denne exception videre til kallende koden med mindre
     * boblen lastes via {@link Store#getIgnoreMissing}.
     */
    <T extends BubbleObject> T onRegister(T bubbleObject);

    /**
     * Kalles når flere bobler kommer til å bli registrert i Store rett etter hverander, rett før første kall til {@link
     * #onRegister(BubbleObject)}.
     *
     * Denne metoden gir ReadListener mulighet for å utføre batch operasjoner for alle bobler som kommer til
     * å bli registrert, f.eks utføre søk mot databasen og cache resultatet, i forkant av
     * {@link #onRegister(BubbleObject)} kallene.
     */
    default <T extends BubbleObject> void onPreRegisterBubbles(Collection<? extends T> bubbleObjects) {}

    /**
     * Kalles når flere bobler kommer til å bli registrert i Store rett etter hverander, rett etter siste kall til
     * #onRegister(BubbleObject)}.
     *
     * Denne metoden gir ReadListener mulighet for fjerne cachet data som har blitt lastet av
     * {@link #onPreRegisterBubbles}.
     */
    default void onPostRegisterBubbles() {}
}
