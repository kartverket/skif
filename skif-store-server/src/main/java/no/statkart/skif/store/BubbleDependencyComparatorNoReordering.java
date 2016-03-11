package no.statkart.skif.store;

/**
 * DependencyComparator som ikke endre på rekkefølgen
 * @author Henrik Fredholm
 * @since 2.1
 */
public class BubbleDependencyComparatorNoReordering implements BubbleDependencyComparator{
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    @Override
    public int compare(BubbleObject o1, BubbleObject o2) {
        return 0;
    }
}
