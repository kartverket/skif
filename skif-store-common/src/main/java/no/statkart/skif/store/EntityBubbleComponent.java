package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface EntityBubbleComponent<T extends BubbleObject> extends EntityComponent, BubbleComponent<T> {
}
