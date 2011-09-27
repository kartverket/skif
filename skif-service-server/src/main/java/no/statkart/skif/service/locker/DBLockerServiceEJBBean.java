package no.statkart.skif.service.locker;

import com.google.inject.Inject;
import no.statkart.skif.config.SkifEJBInterceptorJEE;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.ejb.EJBTimedService;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;
import java.util.Collection;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@RolesAllowed("Innsyn")
@Stateless(name = "no.statkart.skif.service.locker.DBLockerServiceEJBBean")
@Interceptors(SkifEJBInterceptorJEE.class)
@TransactionManagement(TransactionManagementType.BEAN)
public class DBLockerServiceEJBBean extends EJBTimedService implements DBLockerService<Long> {

    @Inject @EJBServiceChain
    DBLockerService<Long> serviceChain;

    @Override
    public LockInfo<Long> lock(LockKey<Long> lockKey, String owner, long lockTimeout) throws LockedException {
        return serviceChain.lock(lockKey, owner, lockTimeout);
    }

    @Override
    public Set<LockInfo<Long>> lockAll(Set<LockKey<Long>> lockKeys, String owner, long lockTimeout) throws LockedException {
        return serviceChain.lockAll(lockKeys, owner, lockTimeout);
    }

    @Override
    public void unlock(LockKey<Long> lockKey, String owner) {
        serviceChain.unlock(lockKey, owner);
    }

    @Override
    public void unlockAll(Set<LockKey<Long>> unLockKeys, String owner) {
        serviceChain.unlockAll(unLockKeys, owner);
    }

    @Override
    public Collection<LockInfo<Long>> getLocksBy(String owner) {
        return serviceChain.getLocksBy(owner);
    }

    @Override
    public void releaseAllLocks(String owner) {
        serviceChain.releaseAllLocks(owner);
    }

    @Override
    public Collection<LockInfo<Long>> renewAllLocks(String owner, long lockTimeout) {
        return serviceChain.renewAllLocks(owner, lockTimeout);
    }

    @Override
    public LockInfo<Long> getLock(LockKey<Long> lockKey) {
        return serviceChain.getLock(lockKey);
    }
}
