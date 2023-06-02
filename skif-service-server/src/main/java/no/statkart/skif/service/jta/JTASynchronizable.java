package no.statkart.skif.service.jta;

/**
 * Interface that allows an object be notified that it has been registered with a JTA transaction such that is can
 * decide to synchronize its transactional actions with the JTA transaction.
 *
 * @author Henrik Fredholm
 * since 2.0
 */
public interface JTASynchronizable extends jakarta.transaction.Synchronization {
    void setWaitForJTASynchronization(boolean doWait);
}