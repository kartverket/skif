package no.statkart.skif.storetest2.service.test;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest2.config.StoreTest2EJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.SortedMap;

/**
 * EJB for {@link TestdataServiceImpl}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest2.service.test.TestServiceEJBBean")
@Interceptors(StoreTest2EJBInterceptorJEE.class)
public class TestdataServiceEJBBean extends EJBTimedService implements TestdataService {
    @Inject @EJBServiceChain
    private TestdataService service;


    @Override
    public TestNumber getTestNumber0() {
        return service.getTestNumber0();
    }

    @Override
    public TestNumber getNextTestNumber() {
        return service.getNextTestNumber();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveAll(SortedMap<SnapshotVersion, MockupTransfer> snapshotTransfers) {
        service.saveAll(snapshotTransfers);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveSnapshotTransfer(SnapshotVersion snapshotVersion, MockupTransfer mockupTransfer) {
        service.saveSnapshotTransfer(snapshotVersion, mockupTransfer);
    }
}
