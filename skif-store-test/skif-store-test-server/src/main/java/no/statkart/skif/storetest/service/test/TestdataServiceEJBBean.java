package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

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
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.test.TestServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class TestdataServiceEJBBean extends EJBTimedService implements TestdataService {
    @Inject @EJBServiceChain
    private TestdataService service;


    @Override
    public TestNumber getTestNumber_0() {
        return service.getTestNumber_0();
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

    @Override
    public void deleteObject(long id, String tableName) {
        service.deleteObject(id, tableName);
    }
}
