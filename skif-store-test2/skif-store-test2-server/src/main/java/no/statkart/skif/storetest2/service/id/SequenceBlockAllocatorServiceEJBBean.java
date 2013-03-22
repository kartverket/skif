package no.statkart.skif.storetest2.service.id;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest2.config.StoreTest2EJBInterceptorJEE;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 2.2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.storetest2.service.id.SequenceBlockAllocatorServiceEJBBean")
@Interceptors(StoreTest2EJBInterceptorJEE.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class SequenceBlockAllocatorServiceEJBBean extends EJBTimedService implements SequenceBlockAllocatorService {

    @Inject @EJBServiceChain
    SequenceBlockAllocatorService serviceChain;

    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        return serviceChain.allocateSequenceBlock(sequenceName, blockSize);
    }
}
