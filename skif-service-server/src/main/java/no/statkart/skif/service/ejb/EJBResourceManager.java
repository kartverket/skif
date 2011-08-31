package no.statkart.skif.service.ejb;

/**
 * @author Henrik Fredholm
 */
public interface EJBResourceManager {
    void beginService();
    void completeService() throws Throwable;
    void abortService() throws Throwable;
}
