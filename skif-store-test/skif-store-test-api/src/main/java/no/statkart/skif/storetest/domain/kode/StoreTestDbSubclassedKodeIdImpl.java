package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.store.kodelistesupport.DbSubclassedKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestDbSubclassedKodeIdImpl<T extends StoreTestDbSubclassedKodeImpl> extends DbKodeIdImpl<T> implements DbSubclassedKodeId<T> {
    private static DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>> kodeSupport = new DbKodeSupport<StoreTestDbKodeliste, StoreTestDbKodelisteId<StoreTestDbKodeliste>>(C2DbKodeId.class,new StoreTestDbKodelisteId(10004L, SnapshotVersion.CURRENT));


    protected StoreTestDbSubclassedKodeIdImpl(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        return (id!=null && id instanceof StoreTestDbSubclassedKodeIdImpl && ((StoreTestDbSubclassedKodeIdImpl) id).getValue().equals((((StoreTestDbSubclassedKodeIdImpl) id).getValue())) && this.getSnapshotVersion()==((StoreTestDbSubclassedKodeIdImpl) id).getSnapshotVersion());
    }

    /**
     * For endelige subklasser av DbSubclassedKodeId returneres unike instanser per kode verdi. For mellomliggende
     * subklasser returners det ikke unike instanser per kode verdi. Se {@link {#getKodeSupport}} for videre forklaring.
     *
     * @return
     */
    @Override
    public final StoreTestDbSubclassedKodeIdImpl<T> resolveInstance() {
        DbKodeSupport kodeSupport = getKodeSupport();
        if (kodeSupport==null) {
            return this;
        } else {
            return (StoreTestDbSubclassedKodeIdImpl<T>) getKodeSupport().getOrCreateInstance(this);
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
    protected DbKodeSupport getKodeSupport() {
        return null;
    }

}
