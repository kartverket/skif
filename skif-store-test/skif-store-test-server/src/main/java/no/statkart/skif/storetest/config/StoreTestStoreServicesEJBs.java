package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.exceptiontest.ExceptionTestService;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.locker.DBLockerInTransactionService;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.locking.LockingTestService;
import no.statkart.skif.storetest.service.nedlastning.NedlastningService;
import no.statkart.skif.storetest.service.store.StoreService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/StoreServiceEJBBean", beanInterface = StoreService.class),
        @EJB(name = "ejb/DBLockerServiceEJBBean", beanInterface = DBLockerService.class),
        @EJB(name = "ejb/DBLockerInTransactionServiceEJBBean", beanInterface = DBLockerInTransactionService.class),
        @EJB(name = "ejb/KodelisteServiceEJBBean", beanInterface = KodelisteService.class),
        @EJB(name = "ejb/HistTestServiceEJBBean", beanInterface = HistTestService.class),
        @EJB(name = "ejb/LockingTestServiceEJBBean", beanInterface = LockingTestService.class),
        @EJB(name = "ejb/ExceptionTestServiceEJBBean", beanInterface = ExceptionTestService.class),
        @EJB(name = "ejb/EndringsloggServiceEJBBean", beanInterface = EndringsloggService.class),
        @EJB(name = "ejb/NedlastningServiceEJBBean", beanInterface = NedlastningService.class)
})
public class StoreTestStoreServicesEJBs extends EJBRegistration {
    public StoreTestStoreServicesEJBs() {
        super(new StoreTestStoreServices());
    }
}
