package no.statkart.skif.storetest.service.id;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.id.SequenceBlockAllocatorServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@StoreTestEJBInterceptorSpring
@TransactionManagement(TransactionManagementType.BEAN)
@Transactional(propagation = Propagation.NOT_SUPPORTED)  // Spring krever dette i tillegg til TransactionManagementType.BEAN
public class SequenceBlockAllocatorServiceEJBBean extends EJBTimedService implements SequenceBlockAllocatorService {

    @Inject @EJBServiceChain
    SequenceBlockAllocatorService serviceChain;

    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        return serviceChain.allocateSequenceBlock(sequenceName, blockSize);
    }
}
