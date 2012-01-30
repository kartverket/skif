package no.statkart.skif.storetest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.mockup.MockupTransfer;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * EJB for {@link TestServiceImpl}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest.service.test.TestServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
public class TestServiceEJBBean extends EJBTimedService implements TestService {
    @Inject @EJBServiceChain
    private TestService service;

    @Override
    public int getNextTestNumber() {
        return service.getNextTestNumber();
    }

    @Override
    public void saveSnapshotTransfer(MockupTransfer transfer, SnapshotVersion snapshotVersion) {
        service.saveSnapshotTransfer(transfer, snapshotVersion);
    }

    @Override
    public void deleteObject(long id, String tableName) {
        service.deleteObject(id, tableName);
    }
}
