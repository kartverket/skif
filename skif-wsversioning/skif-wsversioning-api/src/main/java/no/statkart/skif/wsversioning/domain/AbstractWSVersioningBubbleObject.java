package no.statkart.skif.wsversioning.domain;

import no.statkart.skif.store.AbstractBubbleObject;

/**
 * Abstrakt baseklasse of vanlige bobler i WSVersioning-prosjektet.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public abstract class AbstractWSVersioningBubbleObject extends AbstractBubbleObject implements WSVersioningBubbleObject {
    private static final long serialVersionUID = 1L;

    private long versjonId;

    @Override
    public WSVersioningBubbleId<?> getId() {
        return (WSVersioningBubbleId<?>) super.getId();
    }

    @Override
    public long getVersjonId() {
        return versjonId;
    }

    @Override
    public void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }
}
