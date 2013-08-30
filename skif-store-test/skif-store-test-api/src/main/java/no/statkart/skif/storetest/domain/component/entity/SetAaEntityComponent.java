package no.statkart.skif.storetest.domain.component.entity;

import no.statkart.skif.store.AbstractEntityBubbleComponentWithOwner;

/**
 * Entity som inngår i et set A som ligger i BubbleWithEntityComponent.
 *
 * <P>Komponenten har en ident {@code ident} som skal kunne endres mens komponenten inngår i et Set.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class SetAaEntityComponent extends AbstractEntityBubbleComponentWithOwner<BubbleWithEntityComponent> {
    private Long id;
    private BubbleWithEntityComponent owner;
    private int ident;
    private String text;

    public SetAaEntityComponent() {
    }

    public SetAaEntityComponent(int ident, String text) {
        this.ident=ident;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("UnusedDeclaration") // Hibernate
    private void setId(Long id) {
        this.id = id;
    }

    @Override
    public BubbleWithEntityComponent getOwner() {
        return owner;
    }

    @Override
    public void setOwner(BubbleWithEntityComponent owner) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
