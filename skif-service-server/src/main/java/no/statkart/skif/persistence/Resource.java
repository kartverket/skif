package no.statkart.skif.persistence;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface Resource {
    void close();
    boolean isActive();
    void setActive();

}
