package no.statkart.skif.service;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

/**
 * @author Henrik Fredholm
 */
@TransactionManagement(TransactionManagementType.BEAN)
public class RunOnServerWithTxBeanManagedServiceImpl implements RunOnServerWithTxBeanManagedService {
    @Inject
    Injector injector;
    public Object run(RunOnServerMethod method) {
        method.init(injector);
        return method.run();
    }
}
