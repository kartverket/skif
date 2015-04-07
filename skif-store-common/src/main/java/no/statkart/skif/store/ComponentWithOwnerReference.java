package no.statkart.skif.store;

import no.statkart.skif.store.relation.cache.RelationName;

import java.util.Map;

/**
 * Interface som angir at et domeneobjekt er en komponent som har en tilbakepeker til det eiende objektet
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ComponentWithOwnerReference<T> extends Component{
    T getOwner();
    void setOwner(T owner);
}
