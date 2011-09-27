package no.statkart.skif.storetest.domain.kodeliste.impl;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.DbSubclassedBubbleKodeId;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class DbSubclassedKodeId<T extends DbSubclassedKode> extends DbKodeId<T> implements DbSubclassedBubbleKodeId<T> {

    protected DbSubclassedKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        return (id!=null && id instanceof DbSubclassedKodeId && ((DbSubclassedKodeId) id).getValue().equals((((DbSubclassedKodeId) id).getValue())) && this.getSnapshotVersion()==((DbSubclassedKodeId) id).getSnapshotVersion());
    }

    /**
     * For endelige subklasser av DbSubclassedKodeId returneres unike instanser per kode verdi. For mellomliggende
     * subklasser returners det ikke unike instanser per kode verdi. Se {@link {#getKodeSupport}} for videre forklaring.
     *
     * @return
     */
    @Override
    public final DbSubclassedKodeId<T> resolveInstance() {
        DbKodeSupport kodeSupport = getKodeSupport();
        if (kodeSupport==null) {
            return this;
        } else {
            return getKodeSupport().getOrCreateInstance(this);
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
