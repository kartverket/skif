package no.statkart.skif.service;

import com.google.inject.Inject;
import com.google.inject.Injector;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings("unused")
@Deprecated
public class ContainerManagedTransactionRunOnServerServiceImpl implements ContainerManagedTransactionRunOnServerService {
    @Inject
    Injector injector;

    @Override
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Object runInTxNotSupported(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Object runWithTxSupported(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object runInTxRequiresNew(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }
}
