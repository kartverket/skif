package no.statkart.skif.wsversioning.wsapi.v1.exception.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.wsversioning.wsapi.v1.exception.LockNotAquired;
import no.statkart.skif.wsversioning.wsapi.v1.exception.LockNotAquiredList;
import no.statkart.skif.wsversioning.wsapi.v1.exception.LockedFaultInfo;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class LockedFaultInfoTypeMapper extends AbstractTypeMapper<LockedFaultInfo, LockedException, WSVersioningExceptionMapping> {
    private final DatatypeFactory datatypeFactory;

    public LockedFaultInfoTypeMapper() {
        super(LockedFaultInfo.class, LockedException.class, WSVersioningExceptionMapping.class);

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }
    }

    @Override
    public LockedFaultInfo mapDomainObject(LockedException source) {
        LockedFaultInfo faultInfo = new LockedFaultInfo();

        faultInfo.setOwner(source.getOwner());
        faultInfo.setExceptionTime(mapTimestamp(source.getExceptionTime()));

        LockNotAquiredList locksNotAquiredList = new LockNotAquiredList();
        for (LockInfo<?> lockInfo : source.getLocksNotAquired()) {
            LockNotAquired lockNotAquired = new LockNotAquired();
            lockNotAquired.setOwner(lockInfo.getOwner());
            lockNotAquired.setExpires(mapTimestamp(lockInfo.getExpires()));

            LockNotAquired.LockKey lockKey = new LockNotAquired.LockKey();
            lockKey.setKeyValue(lockInfo.getLockKey().keyValue.toString());
            lockKey.setDiscriminator(lockInfo.getLockKey().discriminator);
            lockNotAquired.setLockKey(lockKey);

            locksNotAquiredList.getItem().add(lockNotAquired);
        }
        faultInfo.setLocksNotAquired(locksNotAquiredList);

        return faultInfo;
    }

    /**
     * Dette er ikke ordentlig mapping av datoer, men det er ikke det som skal demonstreres her.
     */
    private XMLGregorianCalendar mapTimestamp(Timestamp timestamp) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(timestamp);
        return datatypeFactory.newXMLGregorianCalendar();
    }

    /**
     * Dette er ikke ordentlig mapping av datoer, men det er ikke det som skal demonstreres her.
     */
    private Timestamp mapTimestamp(XMLGregorianCalendar xmlCalendar) {
        GregorianCalendar calendar = xmlCalendar.toGregorianCalendar();
        return new Timestamp(calendar.getTimeInMillis());
    }

    @Override
    public LockedException mapWsapiObject(LockedFaultInfo source) {
        List<LockInfo<String>> lockInfos = new ArrayList<>(source.getLocksNotAquired().getItem().size());

        for (LockNotAquired lockNotAquired : source.getLocksNotAquired().getItem()) {
            java.sql.Timestamp expires = mapTimestamp(lockNotAquired.getExpires());
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

        java.sql.Timestamp exceptionTime = mapTimestamp(source.getExceptionTime());

        return new LockedException(source.getOwner(), lockInfos, exceptionTime);
    }
}
