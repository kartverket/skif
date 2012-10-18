package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent som ikke har en id i det hele tatt. Objektet lagres i
 * databasen i samme tabell som det eiende objektet
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface ComponentWithoutId<I extends Serializable> extends Component {
}
