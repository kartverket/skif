package no.statkart.skif.service;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnServerWithTxRequiresNewServiceImpl implements RunOnServerWithTxRequiresNewService {
    @Inject
    Injector injector;
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object run(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }
}
