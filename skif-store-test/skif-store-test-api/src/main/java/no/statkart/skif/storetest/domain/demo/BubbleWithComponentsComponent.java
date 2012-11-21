package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractEntityComponent;

/**
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class BubbleWithComponentsComponent extends AbstractEntityComponent {
    private Long id;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
