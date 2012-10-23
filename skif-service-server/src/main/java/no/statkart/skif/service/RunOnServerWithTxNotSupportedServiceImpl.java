package no.statkart.skif.service;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnServerWithTxNotSupportedServiceImpl implements RunOnServerWithTxNotSupportedService {
    @Inject
    Injector injector;
    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object run(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }
}
