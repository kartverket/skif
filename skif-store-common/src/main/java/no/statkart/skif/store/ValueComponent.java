package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent som ikke har en id i det hele tatt. Objektet lagres i
 * databasen i samme tabell som det eiende objektet. De kan eventuelt også brukes som komponenter i en liste.
 * ValueComponent skal ikke inneholde collections eller komponenter som ikke er ValueComponent.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface ValueComponent extends Component {
}
