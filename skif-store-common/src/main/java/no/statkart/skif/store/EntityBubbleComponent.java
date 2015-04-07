package no.statkart.skif.store;

import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Map;

/**
 * Interface som angir at objektet er en EntityComponent og at det eiende objektet er en boble
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface EntityBubbleComponent<T extends BubbleObject> extends EntityComponentWithOwnerReference<T>, BubbleComponent<T> {
}
