package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface ServiceContextMapper<W> {
    W  createWSServiceContextFromDomainServiceContext();
    void setDomainServiceContextFromWSServiceContext(W apiContext);
}
