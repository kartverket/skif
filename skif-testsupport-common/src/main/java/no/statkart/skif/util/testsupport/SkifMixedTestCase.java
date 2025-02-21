package no.statkart.skif.util.testsupport;

import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
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
            RunOnServerWithTxBeanManagedService runOnServerService = injector.getInstance(RunOnServerWithTxBeanManagedService.class);
            return runOnServerService.run(method);
        }

        public Object runInTxNotSupported(RunOnServerMethod method) {
            RunOnServerWithTxNotSupportedService runOnServerService = injector.getInstance(RunOnServerWithTxNotSupportedService.class);
            return runOnServerService.run(method);
        }

        /**
         *
         * @deprecated Bruk {@link #runInTxRequired}. Oppførslen til runInTxRequiresNew kommer muligvis til å endre seg
         * i fremtiden mht frigivelse av låser ved commit.
         */
        public Object runInTxRequiresNew(RunOnServerMethod method) {
            RunOnServerWithTxRequiresNewService runOnServerService = injector.getInstance(RunOnServerWithTxRequiresNewService.class);
            return runOnServerService.run(method);
        }

        public Object runInTxRequired(RunOnServerMethod method) {
            RunOnServerWithTxRequiredService runOnServerService = injector.getInstance(RunOnServerWithTxRequiredService.class);
            return runOnServerService.run(method);
        }
    }

}
