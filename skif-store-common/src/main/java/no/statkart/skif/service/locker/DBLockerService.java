package no.statkart.skif.service.locker;

import no.statkart.skif.exception.LockedException;
import no.statkart.skif.locker.LockKey;
import no.statkart.skif.locker.LockInfo;

import java.util.Collection;
import java.util.Set;

/**
 * Service for å behandle låser i egne transaksjoner.
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface DBLockerService<T> {

    /**
     * Låser boble hørende til angitt bobleid med utløp etter lockTimeout millisekunder. Hvis boblen allerede er låst av
     * kalderen vil låsen bli fornyet med en ny utløpsperiode på lockTimeout millisekunder. Hvis boblen er låst av en
     * anden bruker, men låse perioden er utløpet vil kalderen få tildelt låsen med en ny utløpsperiode på lockTimeout
     * millisekunder. Hvis det ikke er mulig å ta låsen kastes LockedException.
     *
     * @param lockKey     lockKey for boble som skal låses
     * @param owner       brukernavn som forsøker å låse
     * @param lockTimeout utløpstid i millisekunder  @return informasjon om låsen, bl.a om kalder hadde låsen fra før
     * @return LockInfo for låsen
     * @throws LockedException hvis boblen er låst av anden bruker
     */
    LockInfo<T> lock(LockKey<T> lockKey, String owner, long lockTimeout) throws LockedException;

    /**
     * Låser alle bobler hørende til angitte bobleider med utløp etter lockTimeout millisekunder. Låser enten alle bobler
     * eller ingen av dem.
     *
     * @param lockKeys    ider for bobler som skal låses
     * @param owner       brukernavn som forsøker å låse
     * @param lockTimeout utløpstid i millisekunder  @return informasjon om låsene, bl.a om kalder hadde låsen fra før
     * @throws LockedException hvis en eller flere bobler er låst av anden bruker
     */
    Set<LockInfo<T>> lockAll(Set<LockKey<T>> lockKeys, String owner, long lockTimeout) throws LockedException;

    /**
     * Låse opp boble med angitt lockKey. Har ingen effekt hvis kalder ikke har låsen (låsen kunne være løpet ut på tid)
     *
     * @param lockKey lockKey for boble som skal låses opp
     * @param owner   brukernavn som forsøker å låse opp
     */
    void unlock(LockKey<T> lockKey, String owner);

    /**
     * Låse opp alle bobler med angitt ider. Har ingen effekt hvis kalder ikke har låsen (låsen kunne være løpet ut på
     * tid)
     *
     * @param unLockKeys    lockKeys for bobler som skal låses opp
     * @param owner         brukernavn som forsøker å låse opp
     */
    void unlockAll(Set<LockKey<T>> unLockKeys, String owner);

    /**
     * Returnere alle låsene for kalder
     *
     * @param owner@return alle låsene for kalder
     */
    Collection<LockInfo<T>> getLocksBy(String owner);

    /**
     * Frigir alle låse for kalder
     *
     * @param owner    brukernavn som skal slippe alle sine låser
     */
    void releaseAllLocks(String owner);

    /**
     * Fornyr alle låse for kalder med ny utløpsperiode på lockTimeout millisekunder. Låse som har lengere utløpsperiode
     * vil ikke bli endret.
     *
     * @param owner       brukernavn som forsøker å fornye sine låser
     * @param lockTimeout utløpstid i millisekunder
     * @return informasjon om alle lås for kalder
     */
    Collection<LockInfo<T>> renewAllLocks(String owner, long lockTimeout);

    /**
     * Finner lås for lockKey dersom elementet er låst.
     * @param lockKey LockKey for element vi ønsker å søke på
     * @return LockInfo<T> for lockKey dersom denne finnes, null ellers
     */
    LockInfo<T> getLock(LockKey<T> lockKey);
}
