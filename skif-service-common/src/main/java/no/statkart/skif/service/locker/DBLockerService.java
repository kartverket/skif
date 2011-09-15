package no.statkart.skif.service.locker;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.locker.LockInfo;

import java.util.Collection;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface DBLockerService<Long> {

    /**
     * Låser boble hørende til angitt bobleid med utløp etter lockTimeout millisekunder. Hvis boblen allerede er låst av
     * kalderen vil låsen bli fornyet med en ny utløpsperiode på lockTimeout millisekunder. Hvis boblen er låst av en
     * anden bruker, men låse perioden er utløpet vil kalderen få tildelt låsen med en ny utløpsperiode på lockTimeout
     * millisekunder. Hvis det ikke er mulig å ta låsen kastes LockedException.
     *
     * @param lockKey     lockKey for boble som skal låses
     * @param owner
     * @param lockTimeout utløpstid i millisekunder  @return informasjon om låsen, bl.a om kalder hadde låsen fra før
     * @return LockInfo for låsen
     * @throws LockedException hvis boblen er låst av anden bruker
     */
    LockInfo<Long> lock(LockKey<Long> lockKey, String owner, long lockTimeout) throws LockedException;

    /**
     * Låser alle bobler hørende til angitte bobleider med utløp etter lockTimeout millisekunder. Låser enten alle bobler
     * eller ingen av dem.
     *
     * @param lockKeys    ider for bobler som skal låses
     * @param owner
     * @param lockTimeout utløpstid i millisekunder  @return informasjon om låsene, bl.a om kalder hadde låsen fra før
     * @throws LockedException hvis en eller flere bobler er låst av anden bruker
     */
    Set<LockInfo<Long>> lockAll(Set<LockKey<Long>> lockKeys, String owner, long lockTimeout) throws LockedException;

    /**
     * Låse opp boble med angitt lockKey. Har ingen effekt hvis kalder ikke har låsen (låsen kunne være løpet ut på tid)
     *
     * @param lockKey lockKey for boble som skal låses
     * @param owner
     */
    void unlock(LockKey<Long> lockKey, String owner);

    /**
     * Låse opp alle bobler med angitt ider. Har ingen effekt hvis kalder ikke har låsen (låsen kunne være løpet ut på
     * tid)
     *
     * @param unLockKeys
     * @param owner
     */
    void unlockAll(Set<LockKey<Long>> unLockKeys, String owner);

    /**
     * Returnere alle låsene for kalder
     *
     * @param owner@return alle låsene for kalder
     */
    Collection<LockInfo<Long>> getLocksBy(String owner);

    /**
     * Frigir alle låse for kalder
     *
     * @param owner
     */
    void releaseAllLocks(String owner);

    /**
     * Fornyr alle låse for kalder med ny utløpsperiode på lockTimeout millisekunder. Låse som har lengere utløpsperiode
     * vil ikke bli endret.
     *
     * @param owner
     * @param lockTimeout utløpstid i millisekunder
     * @return informasjon om alle lås for kalder
     */
    Collection<LockInfo<Long>> renewAllLocks(String owner, long lockTimeout);
}
