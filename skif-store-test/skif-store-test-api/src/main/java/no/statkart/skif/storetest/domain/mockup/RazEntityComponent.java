package no.statkart.skif.storetest.domain.mockup;

import no.statkart.skif.store.AbstractEntityComponent;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RazEntityComponent extends AbstractEntityComponent {
    private Long id;
    private String componentName;

    public RazEntityComponent() {
    }

    public RazEntityComponent(String componentName) {
        this.componentName = componentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
