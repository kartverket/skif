package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
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
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store clientStore;

    @Inject
    private DBLockerService dbLockerService;

    public void testInsert() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final Simple Simple = new Simple(mockIdService.getNextId(SimpleId.class), "InsertTest");
        UnitOfWork unitOfWork = clientStore.beginUnitOfWork();
        try {

            Assert.assertFalse(clientStore.isLocked(Simple.getId()), "Nyopprettet objekt skal ikke være låst.");

            clientStore.insert(Simple);

            Assert.assertTrue(clientStore.isLocked(Simple.getId()), "Nyinsertet objekt skal være låst.");

            try {
                clientStore.unlock(Simple.getId());
                Assert.fail("Nyinsertet objekt skal ikke kunne låses opp.");
            } catch (ImplementationException e) {
                Assert.assertTrue(e.getMessage().startsWith("Object has been changed and can not be unlocked"), "Annen exception enn forventet: " + e.getMessage());
            }

            clientStore.lock(Simple.getId());

            Assert.assertTrue(clientStore.isLocked(Simple.getId()), "Nyinsertet objekt skal fortsatt være låst.");

            final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), Simple.getId().getValue())), "Nyinsertet objekt skal ikke være låst i lockerservice.");

            server.runInTxRequired(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Override
                public Object run() {
                    Assert.assertFalse(serverStore.isLocked(Simple.getId()), "Nyinsertet objekt skal ikke enda være låst på tjener.");

                    serverStore.registerTransfer(unitOfWorkTransfer);

                    Assert.assertTrue(serverStore.isLocked(Simple.getId()), "Nyinsertet objekt skal nå være låst på tjener.");

                    return null;
                }
            });

            clientStore.endUnitOfWork(unitOfWork);

            Assert.assertFalse(clientStore.isLocked(Simple.getId()), "Nycommittet objekt skal ikke lenger være låst.");
            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), Simple.getId().getValue())), "Nyinsertet objekt skal ikke være låst i lockerservice.");
        } finally {
            clientStore.closeUnitOfWork(unitOfWork);
        }

    }

    public void testUpdate() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final SimpleId<?> id = mockIdService.getNextId(SimpleId.class);

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                Simple Simple = new Simple(id, "UpdateTest");

                serverStore.insert(Simple);

                return null;
            }
        });

        Assert.assertFalse(clientStore.isLocked(id), "Ikke-oppdatert objekt skal ikke være låst.");
        Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Ikke-oppdatert objekt skal ikke være låst i lockerservice.");

        UnitOfWork unitOfWork = clientStore.beginUnitOfWork();
        try {
            clientStore.get(id);
            Simple Simple = clientStore.lock(id);

            Assert.assertTrue(clientStore.isLocked(id), "Ikke-oppdatert objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Ikke-oppdatert objekt skal være låst i lockerservice.");

            Simple.setText("UpdateTestUpdated");
            clientStore.update(Simple);

            Assert.assertTrue(clientStore.isLocked(id), "Oppdatert objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Oppdatert objekt skal være låst i lockerservice.");

            final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

            server.runInTxRequired(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Inject
                private DBLockerService dbLockerService;

                @Override
                public Object run() {
                    Assert.assertTrue(serverStore.isLocked(id), "Oppdatert objekt skal allerede være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Oppdatert objekt skal fortsatt være låst i lockerservice.");

                    serverStore.registerTransfer(unitOfWorkTransfer);

                    Assert.assertTrue(serverStore.isLocked(id), "Oppdatert objekt skal nå være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Oppdatert objekt skal fortsatt være låst i lockerservice.");

                    return null;
                }
            });

            clientStore.endUnitOfWork(unitOfWork);

            Assert.assertFalse(clientStore.isLocked(Simple.getId()), "Oppdatert objekt skal ikke lenger være låst.");
            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), Simple.getId().getValue())), "Oppdatert objekt skal ikke være låst i lockerservice.");
        } finally {
            clientStore.closeUnitOfWork(unitOfWork);
        }
    }

    public void testDelete() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final SimpleId<?> id = mockIdService.getNextId(SimpleId.class);

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                Simple Simple = new Simple(id, "DeleteTest");

                serverStore.insert(Simple);

                return null;
            }
        });

        Assert.assertFalse(clientStore.isLocked(id), "Ikke-slettet objekt skal ikke være låst.");
        Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Ikke-slettet objekt skal ikke være låst i lockerservice.");

        UnitOfWork unitOfWork = clientStore.beginUnitOfWork();
        try {
            clientStore.get(id);
            Simple Simple = clientStore.lock(id);

            Assert.assertTrue(clientStore.isLocked(id), "Ikke-slettet objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Ikke-slettet objekt skal være låst i lockerservice.");

            clientStore.delete(Simple);

            Assert.assertTrue(clientStore.isLocked(id), "Slettet objekt skal være låst.");
            Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Slettet objekt skal være låst i lockerservice.");

            final UnitOfWorkTransfer unitOfWorkTransfer = clientStore.getUnitOfWorkTransfer();

            server.runInTxRequired(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Inject
                private DBLockerService dbLockerService;

                @Override
                public Object run() {
                    Assert.assertTrue(serverStore.isLocked(id), "Slettet objekt skal allerede være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Slettet objekt skal fortsatt være låst i lockerservice.");

                    serverStore.registerTransfer(unitOfWorkTransfer);

                    Assert.assertTrue(serverStore.isLocked(id), "Slettet objekt skal nå være låst på tjener.");
                    Assert.assertNotNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), id.getValue())), "Slettet objekt skal fortsatt være låst i lockerservice.");

                    return null;
                }
            });

            clientStore.endUnitOfWork(unitOfWork);

            Assert.assertFalse(clientStore.isLocked(Simple.getId()), "Slettet objekt skal ikke lenger være låst.");
            Assert.assertNull(dbLockerService.getLock(new LockKey<Long>(SimpleId.class.getName(), Simple.getId().getValue())), "Slettet objekt skal ikke være låst i lockerservice.");
        } finally {
            clientStore.closeUnitOfWork(unitOfWork);
        }
    }

    /**
     * Tester at StoreSessionServer tar seg bryet med å hente låsinformasjon fra databasen ved vanlig get, dersom man er i unit-of-work.
     */
    public void testGetLocked() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        clientStore.lock(simpleId1);

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                UnitOfWork unitOfWork = serverStore.beginUnitOfWork();
                try {
                    Simple simple = serverStore.get(simpleId1);

                    serverStore.update(simple);

                    serverStore.abortUnitOfWork(unitOfWork);
                } finally {
                    unitOfWork.close();
                }

                return null;
            }
        });
    }

    /**
     * Tester at objekter kan låses og låses opp på i StoreSessionServer så lenge de ikke er modifisert.
     */
    public void testStoreSessionServerUnlockUnmodified() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;

            @Override
            public Object run() {
                serverStore.lock(simpleId1);
                serverStore.unlock(simpleId1);
                return null;
            }
        });
    }

    /**
     * Tester at objekter kan låses og låses opp på i StoreSessionClient så lenge de ikke oppdateres.
     */
    public void testStoreSessionClientUnlockUnmodified() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        clientStore.lock(simpleId1);
        clientStore.unlock(simpleId1);
    }

    /**
     * Tester at objekter som er modifisert vil gi feil hvis blir forsøk låst. Presis hvilken feil man får
     * avhenger av om update er kallt
     */
    public void testStoreSessionServerUnlockGirFeilForModifisertObjektVedUpdateIkkeKallt() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        try {
            server.runInTxRequired(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Override
                public Object run() {
                    Simple simple = serverStore.lock(simpleId1);
                    simple.setText("Jeg er endret");
                    serverStore.unlock(simpleId1);
                    return null;
                }
            });
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().startsWith("Modified object not updated!"));
        }
    }

    /**
     * Tester at objekter som er modifisert vil gi feil hvis blir forsøk låst. Presis hvilken feil man får
     * avhenger av om update er kallt
     */
    public void testStoreSessionServerUnlockGirFeilForModifisertObjektVedUpdateKallt() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final SimpleId<?> simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();

        try {
            server.runInTxRequired(new RunOnServerMethod() {
                @Inject
                private Store serverStore;

                @Override
                public Object run() {
                    Simple simple = serverStore.lock(simpleId1);
                    simple.setText("Jeg er endret");
                    serverStore.update(simple);

                    serverStore.unlock(simpleId1);
                    return null;
                }
            });
        } catch (ImplementationException e) {
            Assert.assertTrue(e.getMessage().startsWith("Object has been changed and can not be unlocked"));
        }
    }

    /**
     * Tester at hvis serveren inneholder et nyere object så blir dette brukt ved låsing selvom klienten har allerede
     * har lastet en eldre kopi.
     */
    public void testLockRetrivesLatestObjectFromServer() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService mockIdService = mockupFacade.getStore().getInstance(IdService.class);

        final SimpleId<?> id = mockIdService.getNextId(SimpleId.class);

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;
            @Override
            public Object run() {
                Simple Simple = new Simple(id, "Initial version");
                serverStore.insert(Simple);
                return null;
            }
        });

        Assert.assertEquals(clientStore.get(id).getText(), "Initial version");

        server.runInTxRequired(new RunOnServerMethod() {
            @Inject
            private Store serverStore;
            @Override
            public Object run() {
                Simple simple = serverStore.lock(id);
                simple.setText("Updated version");
                serverStore.update(simple);
                return null;
            }
        });
        Assert.assertEquals(clientStore.get(id).getText(), "Initial version");
        Assert.assertEquals(clientStore.lock(id).getText(), "Updated version");
    }
}
