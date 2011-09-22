package no.statkart.skif.store2;

/**
 * @author Henrik Fredholm
 */
public interface UnitOfWorkChain2 extends StoreSessionChain2 {
    public void startUnitOfWork();

    public UnitOfWorkTransfer2 getUnitOfWorkTransfer();

    public void abortUnitOfWork();

    public void endUnitOfWork();

    public boolean inUnitOfWork(); 

}
