package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
import no.statkart.skif.storetest.service.endringslogg.EndringsloggService;
import no.statkart.skif.storetest.service.histtest.HistTestService;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.locker.DBLockerInTransactionService;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.locking.LockingTestService;
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
        @EJB(name = "ejb/X1AAFinderServiceEJBBean", beanInterface = X1AAFinderService.class),
        @EJB(name = "ejb/X2AAWithEntityComponentFinderServiceEJBBean", beanInterface = X2AAWithEntityComponentFinderService.class)
})
public class StoreTestDomainFinderServicesEJBs extends EJBRegistration {
    public StoreTestDomainFinderServicesEJBs() {
        super(new StoreTestDomainFinderServices());
    }
}
