package no.statkart.skif.storetest.domain.basic;

/**
 * Domeneklasse som brukes i implementasjon av {@Link no.statkart.skif.storetest.service.storetest1.StoreTest1Service}.
 * @author Henrik Fredholm
 */
public class TestMap {
    private String k;
    private String v;

    public TestMap() {
    }

    public TestMap(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }
}