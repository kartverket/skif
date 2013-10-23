package no.statkart.skif.storetest.domain.component.historikk;

import no.statkart.skif.storetest.domain.AbstractEntityComponentWithHistory;

/**
 * Entity component for {@link HistorikkBubbleWithListEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class HistorikkListEntityComponent extends AbstractEntityComponentWithHistory {
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
