package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreBubbleTransfer;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.uow.UowTestService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester bruk av UnitOfWork på klient og server.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreUnitOfWorkYtreIndreTest extends StoreTestMixedTestCase {
    @Inject
    private Store clientStore;

    @DataProvider(name = "boolean1Dmatrix")
    protected Object[][] boolean1Dmatrix() {
        return new Object[][]{
                {Boolean.TRUE},
                {Boolean.FALSE},
        };
    }

    @DataProvider(name = "boolean2Dmatrix")
    protected Object[][] boolean2Dmatrix() {
        return new Object[][]{
                {Boolean.TRUE, Boolean.TRUE},
                {Boolean.FALSE, Boolean.TRUE},
                {Boolean.TRUE, Boolean.FALSE},
                {Boolean.FALSE, Boolean.FALSE},
        };
    }

    public void ytreUnitOfWorkSkalFrigiLaaserVedAbortUnitOfWork() {
        UowTestService uowTestService = clientStore.getInstance(UowTestService.class);
        releaseAllLockForCurrentUser(); // Så vi har en veldefinert tilstand
        SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        try (UnitOfWork ytre = clientStore.beginUnitOfWork()) {
            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                StoreBubbleTransfer storeBubbleTransfer = findAndLock(simpleId);
                assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
                clientStore.register(storeBubbleTransfer);
                clientStore.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            clientStore.abortUnitOfWork(ytre);
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }

    public void indreUowSkalIkkePaavirkeBoblerEndretUOWIYtreHvisBoblenIkkeOppdateres() {
        UowTestService uowTestService = clientStore.getInstance(UowTestService.class);
        SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        assertThat(clientStore.get(simpleId).getText()).isEqualTo("foo");
        uowTestService.updateTextInNewTransaction(simpleId, "external");
        try (UnitOfWork ytre = clientStore.beginUnitOfWork()) {
            Simple simple1 = clientStore.lock(simpleId);
            assertThat(simple1.getText()).isEqualTo("external");
            simple1.setText("ytre"); // Her endres simple i ytre
            clientStore.update(simple1);
            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                StoreBubbleTransfer storeBubbleTransfer = findAndLock(simpleId);
                Simple transferedBubble = (Simple) storeBubbleTransfer.getBubbleObjects().get(simpleId);
                assertThat(transferedBubble.getText()).isEqualTo("external"); // Server ved ikke om endring, men store gjør
                clientStore.register(storeBubbleTransfer);
                Simple simple2 = clientStore.get(simpleId);
                assertThat(simple2.getText()).isEqualTo("ytre");
                assertThat(simple2).isNotSameAs(simple1);
                simple2.setText("indre"); // Boble i indre endres, men store.update kalles ikke. Endring blir ikke med ved commit
                clientStore.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            Simple simple3 = clientStore.get(simpleId); // Boble er uforandret i ytre, fortsatt samme instans som før indre startet
            assertThat(simple3).isSameAs(simple1);
            assertThat(simple1.getText()).isEqualTo("ytre");
            clientStore.abortUnitOfWork(ytre);
        }
        assertThat(clientStore.get(simpleId).getText()).isEqualTo("external");
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }


    @Test(dataProvider = "boolean2Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(boolean useTransferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(clientStore, simpleId, useTransferForLaasing, useLockOperation);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(boolean useTransferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(serverStore, simpleId, useTransferForLaasing, useLockOperation);
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreMedUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTransferForLaasing, boolean useLockOperation) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTransferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses og oppdateres
                // Har lagt inn endring så man kan se den blir med.
                fooLockedIndre.setText("changed indre");
                store.update(fooLockedIndre);
                store.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            // Uthenting via lock og get skal ha samme effekt, da objektet allerede er låst
            Simple fooAfter = useLockOperation ? store.lock(simpleId) : store.get(simpleId);
            assertThat(fooAfter).isNotNull();
            assertThat(fooAfter).isNotSameAs(foo);
            assertThat(fooAfter).isSameAs(fooLockedIndre);
            assertThat(fooAfter.getText()).isEqualTo("changed indre");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            assertThat(store.isLocked(simpleId)).isTrue();
            assertThat(store.get(simpleId).store()).isNotNull();
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(boolean useTransferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(clientStore, simpleId, useTransferForLaasing, useLockOperation);
    }

    @Test(dataProvider = "boolean2Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(boolean useTransferForLaasing, boolean useLockOperation) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(serverStore, simpleId, useTransferForLaasing, useLockOperation);
                return null;
            }
        });
    }

    public void boblerITransferFraCmtServiceTilhorerSammeStoreSomYtreBmtService() {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer serverStore;
            @Inject
            UowTestService uowTestService;

            @Override
            public Object run() {
                serverStore.beginTransaction();
                StoreBubbleTransfer transfer = uowTestService.findAndLock(simpleId); // Kall til container managed service
                assertThat(transfer.getBubbleObjects().get(simpleId).store())
                        .describedAs("bobler fra cmt service forventes å ligge i samme store som ytre bmt service")
                        .isSameAs(serverStore);
                serverStore.commitTransaction();
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterCommitIIndreUtenUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTransferForLaasing, boolean useLockOperation) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTransferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses men indre gjør ingen oppdatering.
                // Har lagt inn endring så man kan se den ikke blir med.
                fooLockedIndre.setText("changed indre");
                store.commitUnitOfWork(indre);
            }
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            // Uthenting via lock og get skal ha samme effekt, da objektet allerede er låst
            Simple fooAfter = useLockOperation ? store.lock(simpleId) : store.get(simpleId);
            assertThat(fooAfter).isNotNull();
            assertThat(fooAfter).isNotSameAs(foo);
            assertThat(fooAfter).isNotSameAs(fooLockedIndre);
            assertThat(fooAfter.getText()).isEqualTo("external update");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            assertThat(store.isLocked(simpleId)).isTrue();
            assertThat(store.get(simpleId).store()).isNotNull();
        }
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
    }


    @Test(dataProvider = "boolean1Dmatrix")
    public void clientLaasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(boolean useTransferForLaasing) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(clientStore, simpleId, useTransferForLaasing);
    }

    @Test(dataProvider = "boolean1Dmatrix")
    public void serverLaasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(boolean useTransferForLaasing) {
        final SimpleId<?> simpleId = createSimpleObjectOnServer("foo");
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            Store serverStore;

            @Override
            public Object run() {
                laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(serverStore, simpleId, useTransferForLaasing);
                return null;
            }
        });
    }

    private void laasAvBobleLagerNyInstansIYtreUnitWorkEtterAbortIIndreUtenUpdateAvLaastBoble(Store store, SimpleId<?> simpleId, boolean useTransferForLaasing) {
        UowTestService uowTestService = store.getInstance(UowTestService.class);
        assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
        //noinspection unused
        try (UnitOfWork ytre = store.beginUnitOfWork()) {
            Simple foo = store.get(simpleId);
            uowTestService.updateTextInNewTransaction(simpleId, "external update");
            assertThat(foo).isNotNull();
            assertThat(foo.getText()).isEqualTo("foo");
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(0);
            Simple fooLockedIndre;
            try (UnitOfWork indre = store.beginUnitOfWork()) {
                laasSimpleBoble(store, simpleId, useTransferForLaasing, uowTestService);
                fooLockedIndre = store.get(simpleId);
                assertThat(store.isLocked(simpleId)).isTrue();
                assertThat(fooLockedIndre).isNotSameAs(foo);
                assertThat(fooLockedIndre.getText()).isEqualTo("external update");
                // Objektet låses men indre gjør ingen oppdatering.
                // Har lagt inn endring så man kan se den ikke blir med.
                fooLockedIndre.setText("changed indre");
                store.abortUnitOfWork(indre);
            }
            assertThat(store.isLocked(simpleId)).isFalse();
            assertThat(store.get(simpleId).getText()).isEqualTo("external update");
            assertThat(store.get(simpleId).store()).isNotNull();
        }
    }

    public void indreUowSkalKasteInstansVedCommitHvisUpdateIkkeErKalt() {
        try (UnitOfWork ytre = clientStore.beginUnitOfWork()) {
            Simple simple = new Simple();
            clientStore.insert(simple);
            SimpleId<?> simpleId = simple.getId();
            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                clientStore.get(simpleId).setText("Blir ikke med, store.update mangler");
                clientStore.commitUnitOfWork(indre);
            }
            clientStore.get(simpleId).setText("ytre");

            try (UnitOfWork indre = clientStore.beginUnitOfWork()) {
                clientStore.get(simpleId);
                assertThat(clientStore.get(simpleId).getText()).isEqualTo("ytre");
                clientStore.commitUnitOfWork(indre);
            }
            clientStore.abortUnitOfWork(ytre);
        }
    }


    private void laasSimpleBoble(Store store, SimpleId<?> simpleId, boolean useTransferForLaasing, UowTestService uowTestService) {
        if (useTransferForLaasing) {
            StoreBubbleTransfer storeBubbleTransfer = uowTestService.findAndLock(simpleId);
            assertThat(uowTestService.antallLaaserForBruker()).isEqualTo(1);
            store.register(storeBubbleTransfer);
        } else {
            store.lock(simpleId);
        }
    }

    private StoreBubbleTransfer findAndLock(final SimpleId<?> simpleId) {
        return (StoreBubbleTransfer) server.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                StoreBubbleTransfer transfer = new StoreBubbleTransfer();
                transfer.add(store.lock(simpleId));
                return transfer;
            }
        });
    }


    private void releaseAllLockForCurrentUser() {
        DBLockerService lockerService = injector.getInstance(DBLockerService.class);
        LoginUserHolder loginUser = injector.getInstance(LoginUserHolder.class);
        lockerService.releaseAllLocks(loginUser.get().getUsername());
    }

    @SuppressWarnings("SameParameterValue")
    private SimpleId<?> createSimpleObjectOnServer(final String text) {
        return (SimpleId<?>) server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            public Object run() {
                Simple simple = new Simple();
                simple.setText(text);
                store.insert(simple);
                return simple.getId();
            }
        });
    }
}
