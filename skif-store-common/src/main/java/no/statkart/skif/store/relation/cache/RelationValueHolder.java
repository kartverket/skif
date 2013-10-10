package no.statkart.skif.store.relation.cache;

/**
 * Hjelpeobject som brukes av metoder som skal hente frem verdien av en relasjon. Siden {@code null} er en lovlig
 * verdi brukes dette objekt til å skelne mellom den situasjon hvor verdien ikke cachet og den
 * situasjon hvor verdien er cachet og er null.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class RelationValueHolder {
    public final Object value;

    public RelationValueHolder(Object relationValue) {
        this.value = relationValue;
    }

    public Object getValue() {
        return value;
    }
}
