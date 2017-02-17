package no.statkart.skif.store;

import com.google.inject.ImplementedBy;

import java.util.Comparator;

/**
 * Interface for å sorterer slik at boble i avhenginghetsrekkefølge
 * @author Henrik Fredholm
 * @since 2.1
 */
@ImplementedBy(BubbleModelConfiguration.class)
public interface BubbleDependencyComparator extends Comparator<BubbleObject> {
}
