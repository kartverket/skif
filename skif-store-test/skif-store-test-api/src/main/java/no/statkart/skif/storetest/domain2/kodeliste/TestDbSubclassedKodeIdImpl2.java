package no.statkart.skif.storetest.domain2.kodeliste;

import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.DbKodeSupport2;
import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKodeId2;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class TestDbSubclassedKodeIdImpl2<T extends TestDbSubclassedKodeImpl2> extends DbKodeIdImpl2<T> implements DbSubclassedKodeId2<T> {

    protected TestDbSubclassedKodeIdImpl2(Long value, ReplicaVersion2 replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        return (id!=null && id instanceof TestDbSubclassedKodeIdImpl2 && ((TestDbSubclassedKodeIdImpl2) id).getValue().equals((((TestDbSubclassedKodeIdImpl2) id).getValue())) && this.getReplicaVersion()==((TestDbSubclassedKodeIdImpl2) id).getReplicaVersion());
    }

    /**
     * For endelige subklasser av DbSubclassedKodeId returneres unike instanser per kode verdi. For mellomliggende
     * subklasser returners det ikke unike instanser per kode verdi. Se {@link {#getKodeSupport}} for videre forklaring.
     *
     * @return
     */
    @Override
    public final TestDbSubclassedKodeIdImpl2<T> resolveInstance() {
        DbKodeSupport2 kodeSupport = getKodeSupport();
        if (kodeSupport==null) {
            return this;
        } else {
            return (TestDbSubclassedKodeIdImpl2<T>) getKodeSupport().getOrCreateInstance(this);
        }
    }


    /**
     * Endelige subklasser av DbSubclassedKodeId vil (per design) alltid overskrive denne og dermed ha unike instanser
     * per id value. Mellomliggende subklasser av DbSubclassedKodeId har ingen kodesupport og deres instanser blir
     * dermed ikke unike per kode verdi. Mellomliggende id subklasser brukes kun internt i Skif rammeverket ifm lasting av
     * Kode klassen og byttes alltid ut med endlig id subklasse før klassen er ferdig lastet. Dette skjer i
     * no.statkart.skif.persistence.hibernate.HibernateInterceptor#onLoad
     *
     * @return
     */
    @Override
    protected DbKodeSupport2 getKodeSupport() {
        return null;
    }

}
