package no.statkart.skif.store;

import java.io.Closeable;

/**
 * Eksternt API for å forholde seg til en unit of work.
 * <p>
 * Klassen implementerer {@link Closeable}, og kan derfor benyttes med try-with-resource. {@link #close()} vil medføre
 * at unit-of-work blir abortert dersom den fortsatt er aktiv.
 *
 * @see StoreUnitOfWork
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class UnitOfWork implements Closeable {
    private final StoreUnitOfWork unitOfWork;

    protected UnitOfWork(StoreUnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    protected StoreUnitOfWork getUnitOfWork() {
        return unitOfWork;
    }

    @Override
    public void close() {
        Store store = unitOfWork.store;
        store.closeUnitOfWork(this);
    }
}
