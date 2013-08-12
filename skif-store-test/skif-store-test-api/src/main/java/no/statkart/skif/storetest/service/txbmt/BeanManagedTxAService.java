package no.statkart.skif.storetest.service.txbmt;

/**
 * @author Henrik Fredholm
 */
public interface BeanManagedTxAService {
    void clear();
    String get(String key);
    String put(String key, String value);
    void multiPut(String key1, String value1, String key2, String value2);
}
