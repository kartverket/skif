package no.statkart.skif.storetest.service.txcascade;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorJEE;
import no.statkart.skif.storetest.config.StoreTestEJBInterceptorSpring;
import no.statkart.skif.storetest.service.txcascade.ContainerManagedTxCMTCascadeService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.interceptor.Interceptors;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTShadowServiceEJBBean")
@Interceptors(StoreTestEJBInterceptorJEE.class)
@StoreTestEJBInterceptorSpring
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED) // Springs default for metoder er ingen transaksjonshåndtering. Må angi REQUIRED her hvis det skal være default for klassen
public class ContainerManagedTxCMTCascadeServiceEJBBean extends EJBTimedService implements ContainerManagedTxCMTCascadeService {

    @Inject @EJBServiceChain
    ContainerManagedTxCMTCascadeService serviceChain;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void clear() {
        serviceChain.clear();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public String get(String key) {
        return serviceChain.get(key);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public String put(String key, String value) {
        return serviceChain.put(key, value);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void containerTest1(String key1, String value1, String key2, String value2) {
        serviceChain.containerTest1(key1, value1, key2, value2);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void containerTest2(String key1, String value1, String key2, String value2) {
        serviceChain.containerTest2(key1, value1, key2, value2);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void containerTest3(String key1, String value1, String key2, String value2) {
        serviceChain.containerTest3(key1, value1, key2, value2);
    }

    @Override
    // Bruk default. Hvilket er det samme som: @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void containerTest4(String key1, String value1, String key2, String value2) {
        serviceChain.containerTest4(key1, value1, key2, value2);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void beanTest1(String key1, String value1, String key2, String value2) {
        serviceChain.beanTest1(key1, value1, key2, value2);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void beanTest2(String key1, String value1, String key2, String value2) {
        serviceChain.beanTest2(key1, value1, key2, value2);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void beanTest3(String key1, String value1, String key2, String value2) {
        serviceChain.beanTest3(key1, value1, key2, value2);
    }

}
