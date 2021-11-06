package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggServiceEJBBean;
import no.statkart.skif.storetest.service.exceptiontest.ExceptionTestService;
import no.statkart.skif.storetest.service.exceptiontest.ExceptionTestServiceEJBBean;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.histtest.HistTestServiceEJBBean;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.kodeliste.KodelisteServiceEJBBean;
import no.statkart.skif.storetest.service.lock.LockService;
import no.statkart.skif.storetest.service.lock.LockServiceEJBBean;
import no.statkart.skif.storetest.service.locking.LockingTestService;
import no.statkart.skif.storetest.service.locking.LockingTestServiceEJBBean;
import no.statkart.skif.storetest.service.nedlastning.NedlastningService;
import no.statkart.skif.storetest.service.nedlastning.NedlastningServiceEJBBean;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service.store.StoreServiceEJBBean;
import no.statkart.skif.storetest.service.uow.UowTestService;
import no.statkart.skif.storetest.service.uow.UowTestServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestStoreServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestStoreServicesEJBsSpring() {
        super(new StoreTestStoreServices());
    }

    @Bean
    public StoreService getStoreService() {
        return new StoreServiceEJBBean();
    }

    @Bean
    public LockService getLockService() {
        return new LockServiceEJBBean();
    }

    @Bean
    public KodelisteService geKodelisteService() {
        return new KodelisteServiceEJBBean();
    }

    @Bean
    public HistTestService getHistTestService() {
        return new HistTestServiceEJBBean();
    }

    @Bean
    public LockingTestService getLockingTestService() {
        return new LockingTestServiceEJBBean();
    }

    @Bean
    public UowTestService getUowTestService() {
        return new UowTestServiceEJBBean();
    }

    @Bean
    public ExceptionTestService getExceptionTestService() {
        return new ExceptionTestServiceEJBBean();
    }

    @Bean
    public EndringsloggService getEndringsloggService() {
        return new EndringsloggServiceEJBBean();
    }

    @Bean
    public NedlastningService getNedlastningService() {
        return new NedlastningServiceEJBBean();
    }

}
