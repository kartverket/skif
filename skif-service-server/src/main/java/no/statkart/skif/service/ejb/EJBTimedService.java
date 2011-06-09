package no.statkart.skif.service.ejb;

import no.statkart.skif.service.StopRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TimedObject;
import javax.ejb.Timer;


/**
 * @author Henrik Fredholm
 */
public class EJBTimedService implements TimedObject {
    private static Logger logger = LoggerFactory.getLogger(EJBTimedService.class);

    @Override
    public void ejbTimeout(Timer timer) {
        StopRequest s = (StopRequest) timer.getInfo();
        if (logger.isDebugEnabled()) {
            logger.debug("Service timeout. Stop thread= " + Thread.currentThread().getName() + ", " + s);
        }
        s.stop();
    }
}
