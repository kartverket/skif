package no.statkart.skif.store;

/**
 * Et domeneobjekt som har en id av type {@code I}
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface Identifiable<I> {
    I getId();
    void setId(I id);
}
