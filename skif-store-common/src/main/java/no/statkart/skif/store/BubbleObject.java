package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public interface BubbleObject extends Serializable {
    public BubbleId<?> getId();
    public void setId(BubbleId<?> id);
    public Store store();
}
