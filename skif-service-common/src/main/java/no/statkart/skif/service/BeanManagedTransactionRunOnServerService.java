package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 */
public interface BeanManagedTransactionRunOnServerService {
    Object run(RunOnServerMethod method);
}
