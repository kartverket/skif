package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ServiceContextMapper<W> {
    W  createWSServiceContextFromDomainServiceContext();
    void setDomainServiceContextFromWSServiceContext(W apiContext);
}
