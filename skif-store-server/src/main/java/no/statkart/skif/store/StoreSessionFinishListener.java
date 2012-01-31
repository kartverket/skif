package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreSessionFinishListener {
    void onFinish(StoreServer storeServer);
}
