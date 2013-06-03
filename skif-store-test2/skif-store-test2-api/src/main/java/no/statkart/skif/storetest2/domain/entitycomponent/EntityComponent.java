package no.statkart.skif.storetest2.domain.entitycomponent;

import no.statkart.skif.storetest2.domain.AbstractEntityComponentWithHistory;

/**
 * Generisk entity component for bruk både i én-til-én og én-til-mange, fortrinnsvis Set.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class EntityComponent extends AbstractEntityComponentWithHistory {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
