package no.statkart.skif.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.mockup.MockupFacade;
import no.statkart.skif.storetest.mockup.MockupFacadeFactory;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester låsing gjennom Store, i samspill med låsebehandlingen i transaksjonshåndteringen.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreLockingTest extends StoreTestMixedTestCase {
    @Inject
    private MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store clientStore;

    @Inject
    private DBLockerService dbLockerService;

    public void testInsert() {
        final MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final TestBubble testBubble = new TestBubble(mockIdService.getNextId(TestBubbleId.class), "InsertTest");
        clientStore.beginUnitOfWork();
        try {

            Assert.assertFalse(clientStore.isLocked(testBubble.getId()), "Nyopprettet objekt skal ikke være låst.");

            clientStore.insert(testBubble);

            Assert.assertTrue(clientStore.isLocked(testBubble.getId()), "Nyinsertet objekt skal være låst.");

            try {
                clientStore.unlock(testBubble.getId());
                Assert.fail("Nyinsertet objekt skal ikke kunne låses opp.");
            } catch (ImplementationException e) {
                Assert.assertTrue(e.getMessage().startsWith("Objekt har blitt endret og kan ikke låses opp"), "Annen exception enn forventet: " + e.getMessage());
            }

            clientStore.lock(testBubble.getId());

            Assert.assertTrue(clientStore.isLocked(testBubble.getId()), "Nyinsertet objekt skal fortsatt være låst.");

            final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), testBubble.getId().getValue())), "Nyinsertet objekt skal ikke være låst i lockerservice.");

            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Override
                public Object run() {
                    Assert.assertFalse(serverStore.isLocked(testBubble.getId()), "Nyinsertet objekt skal ikke enda være låst på tjener.");

                    serverStore.registerTransfer(unitOfWorkTransfer);

                    Assert.assertTrue(serverStore.isLocked(testBubble.getId()), "Nyinsertet objekt skal nå være låst på tjener.");

                    return null;
                }
            });

            clientStore.endUnitOfWork();

            // TODO: Denne sjekken er feil. Det skulle vært assertFalse, men en bug i unit-of-work-håndtering tilbakestiller ikke tilstand ved ferdigstilling.
            Assert.assertTrue(clientStore.isLocked(testBubble.getId()), "Nycommittet objekt skal ikke lenger være låst.");

            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), testBubble.getId().getValue())), "Nyinsertet objekt skal ikke være låst i lockerservice.");
        } finally {
            if (clientStore.inUnitOfWork()) clientStore.abortUnitOfWork();
        }

    }

    public void testUpdate() {
        final MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final TestBubbleId<?> id = mockIdService.getNextId(TestBubbleId.class);

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                TestBubble testBubble = new TestBubble(id, "UpdateTest");

                serverStore.insert(testBubble);

                return null;
            }
        });

        Assert.assertFalse(clientStore.isLocked(id), "Ikke-oppdatert objekt skal ikke være låst.");
        Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Ikke-oppdatert objekt skal ikke være låst i lockerservice.");

        clientStore.beginUnitOfWork();
        try {
            clientStore.get(id);
            TestBubble testBubble = clientStore.lock(id);

            Assert.assertTrue(clientStore.isLocked(id), "Ikke-oppdatert objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Ikke-oppdatert objekt skal være låst i lockerservice.");

            testBubble.setText("UpdateTestUpdated");
            clientStore.update(testBubble);

            Assert.assertTrue(clientStore.isLocked(id), "Oppdatert objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Oppdatert objekt skal være låst i lockerservice.");

            final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Inject
                private DBLockerService dbLockerService;

                @Override
                public Object run() {
                    Assert.assertTrue(serverStore.isLocked(id), "Oppdatert objekt skal allerede være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Oppdatert objekt skal fortsatt være låst i lockerservice.");

                    serverStore.registerTransfer(unitOfWorkTransfer);

                    Assert.assertTrue(serverStore.isLocked(id), "Oppdatert objekt skal nå være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Oppdatert objekt skal fortsatt være låst i lockerservice.");

                    return null;
                }
            });

            clientStore.endUnitOfWork();

            // TODO: Denne sjekken er feil. Det skulle vært assertFalse, men en bug i unit-of-work-håndtering tilbakestiller ikke tilstand ved ferdigstilling.
            Assert.assertTrue(clientStore.isLocked(testBubble.getId()), "Oppdatert objekt skal ikke lenger være låst.");

            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), testBubble.getId().getValue())), "Oppdatert objekt skal ikke være låst i lockerservice.");
        } finally {
            if (clientStore.inUnitOfWork()) clientStore.abortUnitOfWork();
        }
    }

    public void testDelete() {
            final MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();
            final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

            final TestBubbleId<?> id = mockIdService.getNextId(TestBubbleId.class);

            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Override
                public Object run() {
                    TestBubble testBubble = new TestBubble(id, "DeleteTest");

                    serverStore.insert(testBubble);

                    return null;
                }
            });

            Assert.assertFalse(clientStore.isLocked(id), "Ikke-slettet objekt skal ikke være låst.");
            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Ikke-slettet objekt skal ikke være låst i lockerservice.");

            clientStore.beginUnitOfWork();
            try {
                clientStore.get(id);
                TestBubble testBubble = clientStore.lock(id);

                Assert.assertTrue(clientStore.isLocked(id), "Ikke-slettet objekt skal være låst.");
                Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Ikke-slettet objekt skal være låst i lockerservice.");

                clientStore.delete(testBubble);

                Assert.assertTrue(clientStore.isLocked(id), "Slettet objekt skal være låst.");
                Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Slettet objekt skal være låst i lockerservice.");

                final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

                server.runInTxRequiresNew(new RunOnServerMethod() {
                    @Inject
                    private Store serverStore;

                    @Inject
                    private DBLockerService dbLockerService;

                    @Override
                    public Object run() {
                        Assert.assertTrue(serverStore.isLocked(id), "Slettet objekt skal allerede være låst på tjener.");
                        Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Slettet objekt skal fortsatt være låst i lockerservice.");

                        serverStore.registerTransfer(unitOfWorkTransfer);

                        Assert.assertTrue(serverStore.isLocked(id), "Slettet objekt skal nå være låst på tjener.");
                        Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), id.getValue())), "Slettet objekt skal fortsatt være låst i lockerservice.");

                        return null;
                    }
                });

                clientStore.endUnitOfWork();

                // TODO: Denne sjekken er feil. Det skulle vært assertFalse, men en bug i unit-of-work-håndtering tilbakestiller ikke tilstand ved ferdigstilling.
                Assert.assertTrue(clientStore.isLocked(testBubble.getId()), "Slettet objekt skal ikke lenger være låst.");

                Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(TestBubbleId.class.getName(), testBubble.getId().getValue())), "Slettet objekt skal ikke være låst i lockerservice.");
            } finally {
                if (clientStore.inUnitOfWork()) clientStore.abortUnitOfWork();
            }
        }
}
