package no.statkart.skif.util.testsupport;

import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;
import no.statkart.skif.service.RunOnServerMethod;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 */
@Test
public class SkifServerTestCase extends SkifTestCase {
    final protected RunOnServer server = new RunOnServer();
    public SkifServerTestCase() {
        setSingleVm(true);
    }

    protected class RunOnServer {
        public Object runInBeanManagedTransaction(RunOnServerMethod method) {
            BeanManagedTransactionRunOnServerService runOnServerService = injector.getInstance(BeanManagedTransactionRunOnServerService.class);
            return runOnServerService.run(method);
        }

        public Object runInTxNotSupported(RunOnServerMethod method) {
            ContainerManagedTransactionRunOnServerService runOnServerService = injector.getInstance(ContainerManagedTransactionRunOnServerService.class);
            return runOnServerService.runInTxNotSupported(method);
        }

        public Object runInTxSupported(RunOnServerMethod method) {
            ContainerManagedTransactionRunOnServerService runOnServerService = injector.getInstance(ContainerManagedTransactionRunOnServerService.class);
            return runOnServerService.runWithTxSupported(method);
        }

        public Object runInTxRequiresNew(RunOnServerMethod method) {
            ContainerManagedTransactionRunOnServerService runOnServerService = injector.getInstance(ContainerManagedTransactionRunOnServerService.class);
            return runOnServerService.runInTxRequiresNew(method);
        }
    }

}
