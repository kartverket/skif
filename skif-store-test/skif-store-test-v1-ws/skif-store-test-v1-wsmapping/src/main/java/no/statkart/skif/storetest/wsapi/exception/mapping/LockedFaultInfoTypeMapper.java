package no.statkart.skif.storetest.wsapi.exception.mapping;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.exception.LockNotAquired;
import no.statkart.skif.storetest.wsapi.exception.LockNotAquiredList;
import no.statkart.skif.storetest.wsapi.exception.LockedFaultInfo;

import java.util.ArrayList;
import java.util.List;

public class LockedFaultInfoTypeMapper extends AbstractTypeMapper<LockedFaultInfo, LockedException, StoreTestExceptionMapping> {
    public LockedFaultInfoTypeMapper() {
        super(LockedFaultInfo.class, LockedException.class, StoreTestExceptionMapping.class);
    }

    @Override
    public LockedFaultInfo mapDomainObject(LockedException source) {
        LockedFaultInfo faultInfo = new LockedFaultInfo();

        faultInfo.setOwner(source.getOwner());
        faultInfo.setExceptionTime(getMapping().d2w(source.getExceptionTime(), Timestamp.class));

        LockNotAquiredList locksNotAquiredList = new LockNotAquiredList();
        for (LockInfo<?> lockInfo : source.getLocksNotAquired()) {
            LockNotAquired lockNotAquired = new LockNotAquired();
            lockNotAquired.setOwner(lockInfo.getOwner());
            lockNotAquired.setExpires(getMapping().d2w(lockInfo.getExpires(), Timestamp.class));

            LockNotAquired.LockKey lockKey = new LockNotAquired.LockKey();
            lockKey.setKeyValue(lockInfo.getLockKey().keyValue.toString());
            lockKey.setDiscriminator(lockInfo.getLockKey().discriminator);
            lockNotAquired.setLockKey(lockKey);

            locksNotAquiredList.getItem().add(lockNotAquired);
        }
        faultInfo.setLocksNotAquired(locksNotAquiredList);

        return faultInfo;
    }

    @Override
    public LockedException mapWsapiObject(LockedFaultInfo source) {
        List<LockInfo<String>> lockInfos = new ArrayList<>(source.getLocksNotAquired().getItem().size());

        for (LockNotAquired lockNotAquired : source.getLocksNotAquired().getItem()) {
            java.sql.Timestamp expires = getMapping().w2d(lockNotAquired.getExpires(), java.sql.Timestamp.class);
            LockInfo<String> lockInfo = new LockInfo<>(
                    new LockKey<>(
                            lockNotAquired.getLockKey().getDiscriminator(),
                            lockNotAquired.getLockKey().getKeyValue()
                    ),
                    lockNotAquired.getOwner(),
                    expires,
                    false
            );
            lockInfos.add(lockInfo);
        }

        java.sql.Timestamp exceptionTime = getMapping().w2d(source.getExceptionTime(), java.sql.Timestamp.class);

        return new LockedException(source.getOwner(), lockInfos, exceptionTime);
    }
}
