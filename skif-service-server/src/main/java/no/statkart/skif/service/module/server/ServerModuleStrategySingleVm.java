package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import no.statkart.skif.persistence.SingleVmTransactionManager;
import no.statkart.skif.persistence.SingleVmUserTransaction;
import no.statkart.skif.persistence.jdbc.SingleVmTransactionAwareDataSource;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.scope.ServiceRequestScoped;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ServerModuleStrategySingleVm extends ServerModuleStrategy {

    @Override
    public void configure(Binder binder) {
        binder.bind(SingleVmRemoteCallContext.class).in(ServiceRequestScoped.class);

        binder.bind(TransactionManager.class).to(SingleVmTransactionManager.class);
        binder.bind(UserTransaction.class).to(SingleVmUserTransaction.class);
        binder.bind(DataSource.class).to(SingleVmTransactionAwareDataSource.class);
    }
}
