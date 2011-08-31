package no.statkart.skif.service.ejb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceManagerEmptyImpl implements EJBResourceManager{
    private static Logger log = LoggerFactory.getLogger(EJBResourceManagerEmptyImpl.class);

    @Override
    public void beginService() {
        log.debug("begin");
    }

    @Override
    public void completeService() {
        log.debug("complete");
    }

    @Override
    public void abortService() {
        log.debug("abortService");
    }
}
