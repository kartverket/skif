package no.statkart.skif.service;

/**
 * @author Henrik Fredholm
 */
@Deprecated
public interface ContainerManagedTransactionRunOnServerService {
    Object runInTxNotSupported(RunOnServerMethod method);
    Object runWithTxSupported(RunOnServerMethod method);
    Object runInTxRequiresNew(RunOnServerMethod method);
}
