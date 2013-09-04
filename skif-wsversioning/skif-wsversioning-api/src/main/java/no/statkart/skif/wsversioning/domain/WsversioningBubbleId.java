package no.statkart.skif.wsversioning.domain;

import no.statkart.skif.store.BubbleId;

/**
 * Interface for alle bobleid-er i WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface WSVersioningBubbleId<T extends WSVersioningBubbleObject> extends BubbleId<T> {
}
