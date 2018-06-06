package no.statkart.skif.store;

import java.util.Set;

public interface ComponentSet<O, E extends ComponentWithOwnerReference<O>> extends ComponentCollection<O, E>, Set<E> {
}
