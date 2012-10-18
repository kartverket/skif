package no.statkart.skif.util.testsupport;

import no.statkart.skif.service.*;
import org.testng.annotations.Test;

/**
 * En baseklasse for SingleVm tester hvor noen testmetoder kjører på klienten og andre tester kjører på serveren.
 *
 * Alle testmetoder starter i klient-mode og eksekverer som normale klienttester, men i tillegg kan testmetoder
 * implementere en egene testservermetoder som avvikles på serveren. Dette gjøres ved å implementere en anonym
 * klasse av typen {@code RunOnServerMethod}.
 *
 * Tester som extender denne klasse er hardkodet til å kjøre i SingleVm, da rammeverket ikke støtter å sende dynamisk
 * opprettet klasser til serveren i JEE mode via Web Service kommunikasjon.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class SkifMixedTestCase extends SkifTestCase {
    final protected RunOnServer server = new RunOnServer();
    public SkifMixedTestCase() {
        setSingleVm(true);
    }

    protected class RunOnServer {
        public Object runInBeanManagedTransaction(RunOnServerMethod method) {
            BeanManagedTransactionRunOnServerService runOnServerService = injector.getInstance(BeanManagedTransactionRunOnServerService.class);
            return runOnServerService.run(method);
        }

        public Object runInTxNotSupported(RunOnServerMethod method) {
            ContainerManagedNotSupportedTransactionRunOnServerService runOnServerService = injector.getInstance(ContainerManagedNotSupportedTransactionRunOnServerService.class);
            return runOnServerService.run(method);
        }

        public Object runInTxRequiresNew(RunOnServerMethod method) {
            ContainerManagedRequiresNewTransactionRunOnServerService runOnServerService = injector.getInstance(ContainerManagedRequiresNewTransactionRunOnServerService.class);
            return runOnServerService.run(method);
        }
    }

}
