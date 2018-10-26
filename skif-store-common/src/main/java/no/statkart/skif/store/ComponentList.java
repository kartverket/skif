package no.statkart.skif.store;

import no.statkart.skif.store.ComponentCollection;
import no.statkart.skif.store.ComponentWithOwnerReference;

import java.util.List;

public interface ComponentList<O, E extends ComponentWithOwnerReference<? super O>> extends ComponentCollection<O, E>, List<E> {
}
