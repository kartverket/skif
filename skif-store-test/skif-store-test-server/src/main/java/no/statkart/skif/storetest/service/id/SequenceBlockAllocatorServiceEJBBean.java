package no.statkart.skif.storetest.service.id;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.id.SequenceBlockAllocatorServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class SequenceBlockAllocatorServiceEJBBean extends EJBTimedService implements SequenceBlockAllocatorService {

    @Inject @EJBServiceChain
    SequenceBlockAllocatorService serviceChain;

    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        return serviceChain.allocateSequenceBlock(sequenceName, blockSize);
    }
}
