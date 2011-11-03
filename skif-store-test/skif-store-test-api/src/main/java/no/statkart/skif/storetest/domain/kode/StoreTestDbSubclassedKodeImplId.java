package no.statkart.skif.storetest.domain.kode;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbKodeId;
import no.statkart.skif.store.kodelistesupport.DbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodelisteLongId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class StoreTestDbSubclassedKodeImplId<T extends StoreTestDbSubclassedKodeImpl> extends DbKodeId<T> {
    private static DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>> kodeSupport = new DbKodeSupport<StoreTestDbKodelisteLong, StoreTestDbKodelisteLongId<StoreTestDbKodelisteLong>>(C2DbKodeId.class,new StoreTestDbKodelisteLongId(10004L, SnapshotVersion.CURRENT));


    protected StoreTestDbSubclassedKodeImplId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        return (id!=null && id instanceof StoreTestDbSubclassedKodeImplId && ((StoreTestDbSubclassedKodeImplId) id).getValue().equals((((StoreTestDbSubclassedKodeImplId) id).getValue())) && this.getSnapshotVersion()==((StoreTestDbSubclassedKodeImplId) id).getSnapshotVersion());
    }

    /**
     * For endelige subklasser av DbSubclassedKodeId returneres unike instanser per kode verdi. For mellomliggende
     * subklasser returners det ikke unike instanser per kode verdi. Se {@link {#getKodeSupport}} for videre forklaring.
     *
     * @return
     */
    @Override
    public final StoreTestDbSubclassedKodeImplId<T> resolveInstance() {
        DbKodeSupport kodeSupport = getKodeSupport();
        if (kodeSupport==null) {
            return this;
        } else {
            return (StoreTestDbSubclassedKodeImplId<T>) getKodeSupport().getOrCreateInstance(this);
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
