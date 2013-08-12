package no.statkart.skif.storetest.service.txcascade;

/**
 * @author Henrik Fredholm
 */
public interface ContainerManagedTxCMTCascadeService {
    void clear();
    String get(String key);
    String put(String key, String value);
    void containerTest1(String key1, String value1, String key2, String value2);
    void containerTest2(String key1, String value1, String key2, String value2);
    void containerTest3(String key1, String value1, String key2, String value2);
    void containerTest4(String key1, String value1, String key2, String value2);
    void beanTest1(String key1, String value1, String key2, String value2);
    void beanTest2(String key1, String value1, String key2, String value2);
    void beanTest3(String key1, String value1, String key2, String value2);
}
