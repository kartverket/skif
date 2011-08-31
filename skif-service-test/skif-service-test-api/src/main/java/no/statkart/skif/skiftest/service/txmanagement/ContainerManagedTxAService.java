package no.statkart.skif.skiftest.service.txmanagement;

/**
 * @author Henrik Fredholm
 */
public interface ContainerManagedTxAService {
    void clear();
    String get(String key);
    String put(String key, String value);
    void multiPut(String key1, String value1, String key2, String value2);
}
