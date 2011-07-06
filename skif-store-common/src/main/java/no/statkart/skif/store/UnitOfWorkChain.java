package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public interface UnitOfWorkChain extends StoreSessionChain {
    public void startUnitOfWork();

    public UnitOfWorkTransfer getUnitOfWorkTransfer();

    public void abortUnitOfWork();

    public void endUnitOfWork();

    public boolean inUnitOfWork(); 

}
