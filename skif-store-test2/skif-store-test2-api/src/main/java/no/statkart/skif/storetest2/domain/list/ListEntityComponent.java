package no.statkart.skif.storetest2.domain.list;

import no.statkart.skif.store.AbstractEntityComponent;

/**
 * Entity component for {@link ListOfEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class ListEntityComponent extends AbstractEntityComponent {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String textValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
