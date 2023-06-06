package no.statkart.skif.standalone.service.locker;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.locker.DBLockerService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Testklasse for DBLockerService. NB! Det finnes ikke noen webservice for denne tjenesten så testen kan kun kjøres i singleVM mode
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test(groups="singlevm-required")
public class DBLockerServiceTestSVM extends StoreTestTestCase {
    private final TypeLiteral<DBLockerService<Long>> dbLockerServiceTypeLiteral = new TypeLiteral<DBLockerService<Long>>() {
    };

    @Test
    public void testLockElement() {
        DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        long l = System.currentTimeMillis();
        LockInfo<Long> lock = service.lock(new LockKey<>("TestKlasse1", 1123L), "ingroa", 50);
        Assert.assertEquals(lock.getLockKey().discriminator, "TestKlasse1");
        Assert.assertEquals(lock.getLockKey().keyValue, Long.valueOf(1123L));
        Assert.assertEquals(lock.getOwner(), "ingroa");
        Assert.assertTrue(lock.getExpires().getTime() > l);

        service.releaseAllLocks("ingroa");
    }

    @Test
    public void testFindLocksForOwner() {
        DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        Collection<LockInfo<Long>> locks = service.getLocksBy("ingroa");
        Assert.assertEquals(locks.size(), 0);

        HashSet<LockKey<Long>> lockKeys = new HashSet<>();
        lockKeys.add(new LockKey<>("Test1", 1L));
        lockKeys.add(new LockKey<>("Test1", 2L));
        service.lockAll(lockKeys, "ingroa", 4000);

        locks = service.getLocksBy("ingroa");
        Assert.assertEquals(locks.size(), 2);

        service.releaseAllLocks("ingroa");
    }

    @Test
    public void testLockAlreadyLockedElement() {
        DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        service.lock(new LockKey<>("Test1", 1L), "ingroa", 10000);

        Set<LockKey<Long>> lockKeys = new HashSet<>();
        lockKeys.add(new LockKey<>("Test1", 1L));
        lockKeys.add(new LockKey<>("Test1", 2L));
        service.lockAll(lockKeys, "ingroa", 4000);

        service.releaseAllLocks("ingroa");

    }

    @Test(groups = "broken")
    public void testLockElementLockedByOtherUser() {

        DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");

        service.lock(new LockKey<>("Test1", 1L), "ingroa", 10000);

        Set<LockKey<Long>> lockKeys = new HashSet<>();
        lockKeys.add(new LockKey<>("Test1", 1L));
        lockKeys.add(new LockKey<>("Test1", 2L));
        try {
            service.lockAll(lockKeys, "ingroa2", 4000);
            Assert.fail("Forventet exception!");
        } catch (LockedException e) {
            //OK
            Assert.assertEquals(e.getLocksNotAquired().size(), 1);
            Assert.assertEquals(e.getLocksNotAquired().get(0).getLockKey().keyValue, 1L);
        }

        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");
    }

    @Test
    public void testUnlock() {
        DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");

        service.lock(new LockKey<>("Test1", 1L), "ingroa", 10000);
        service.unlock(new LockKey<>("Test1", 1L), "ingroa");

        Set<LockKey<Long>> lockKeys = new HashSet<>();
        lockKeys.add(new LockKey<>("Test1", 1L));
        lockKeys.add(new LockKey<>("Test1", 2L));
        service.lockAll(lockKeys, "ingroa2", 4000);

        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");
    }

    /**
     * Flere brukere prøver å låse de samme elementene. Forventet resultat er at "ingroa" og "ingroa2" ikke får låst
     * noe da noen av elementene de prøver å låse allerede har lås på seg.
     */
    @Test
    public void testConcurrentLocks() throws InterruptedException {
        final DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa0");
        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");

        final Set<LockKey<Long>> set0 = byggTestSet0();
        final Set<LockKey<Long>> set1 = byggTestSet();
        final Set<LockKey<Long>> set2 = byggTestSet2();

        service.lockAll(set0, "ingroa0", 10000);

        Thread r1 = new Thread() {
            @Override
            public void run() {
                int antall = 0;
                try {
                    service.lockAll(set1, "ingroa", 10000);
                } catch (LockedException e) {
                    antall = e.getLocksNotAquired().size();
                }
                Assert.assertEquals(antall, 250);
            }
        };

        Thread r2 = new Thread() {
            @Override
            public void run() {
                int antall = 0;
                try {
                    service.lockAll(set2, "ingroa2", 10000);
                } catch (LockedException e) {
                    antall = e.getLocksNotAquired().size();
                }
                Assert.assertEquals(antall, 250);
            }

        };

        r1.start();
        r2.start();

        Thread.sleep(3000);

        Collection<LockInfo<Long>> locksForIngroa0 = service.getLocksBy("ingroa0");
        Collection<LockInfo<Long>> locksForIngroa = service.getLocksBy("ingroa");
        Collection<LockInfo<Long>> locksForIngroa2 = service.getLocksBy("ingroa2");

        Assert.assertEquals(locksForIngroa0.size(), 500);
        Assert.assertEquals(locksForIngroa.size(), 0);
        Assert.assertEquals(locksForIngroa2.size(), 0);

        service.releaseAllLocks("ingroa0");
        service.releaseAllLocks("ingroa");
        service.releaseAllLocks("ingroa2");
    }

    private Set<LockKey<Long>> byggTestSet0() {
        Set<LockKey<Long>> retur = new HashSet<>();
        for (int i = 0; i < 250; i++) {
            retur.add(new LockKey<>("Test1", (long) i));
        }
        for (int i = 1000; i < 1250; i++) {
            retur.add(new LockKey<>("Test1", (long) i));
        }
        return retur;
    }

    private Set<LockKey<Long>> byggTestSet() {
        Set<LockKey<Long>> retur = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            retur.add(new LockKey<>("Test1", (long) i));
        }
        return retur;
    }

    private Set<LockKey<Long>> byggTestSet2() {
        Set<LockKey<Long>> retur = new HashSet<>();
        for (int i = 500; i < 1500; i++) {
            retur.add(new LockKey<>("Test1", (long) i));
        }
        return retur;
    }

    @Test
    public void testLockUnlockRelock(){
        final DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        service.lock(new LockKey<>("Test1", 10L), "ingroa", 10000);
        service.unlock(new LockKey<>("Test1", 10L), "ingroa");
        Collection<LockInfo<Long>> locks = service.renewAllLocks("ingroa", 10000);
        Assert.assertEquals(locks.size(), 0);

        service.releaseAllLocks("ingroa");
    }

    @Test
    public void testRenewLocks(){
        final DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        LockInfo<Long> lock10 = service.lock(new LockKey<>("Test1", 10L), "ingroa", 30000);
        LockInfo<Long> lock11 = service.lock(new LockKey<>("Test1", 11L), "ingroa", 1000);
        Collection<LockInfo<Long>> locks = service.renewAllLocks("ingroa", 10000);
        Assert.assertEquals(locks.size(), 2);
        for (LockInfo<Long> lock : locks) {
            if(lock.getLockKey().keyValue.equals(10L)) {
                Assert.assertEquals(lock.getExpires(), lock10.getExpires()); //Skal ikke ha blitt endret
            } else if(lock.getLockKey().keyValue.equals(11L)) {
                Assert.assertTrue(lock.getExpires().after(lock11.getExpires()), "Nytt utløpstidspunkt er ikke etter opprinnelig utløpstidspunkt");
                Assert.assertTrue(lock.getExpires().getTime() < lock11.getExpires().getTime() + 15000, "Fornyet for langt inn i fremtiden");
            }
        }

        service.releaseAllLocks("ingroa");
    }

    @Test
    public void testUnlockNonLockedItems(){
        final DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        service.lock(new LockKey<>("Test1", 10L), "ingroa", 30000);

        HashSet<LockKey<Long>> unLockKeys = new HashSet<>();
        unLockKeys.add(new LockKey<>("Test1", 10L));
        unLockKeys.add(new LockKey<>("Test1", 11L));
        unLockKeys.add(new LockKey<>("Test1", 12L));

        service.unlockAll(unLockKeys, "ingroa");

        service.releaseAllLocks("ingroa");
    }


    @Test
    public void testGetLock(){
        final DBLockerService<Long> service = injector.getInstance(Key.get(dbLockerServiceTypeLiteral));

        service.releaseAllLocks("ingroa");

        service.lock(new LockKey<>("Test1", 10L), "ingroa", 30000);
        service.lock(new LockKey<>("Test1", 12L), "ingroa", 30000);
        service.lock(new LockKey<>("Test1", 13L), "ingroa", 30000);
        service.lock(new LockKey<>("Test1", 14L), "ingroa", 30000);

        LockInfo<Long> lock = service.getLock(new LockKey<>("Test1", 10L));
        Assert.assertEquals(lock.getOwner(), "ingroa");

        lock = service.getLock(new LockKey<>("Test1", 11L));
        Assert.assertNull(lock);

        service.releaseAllLocks("ingroa");
    }

}
