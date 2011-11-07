package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.DbKodeId;
import no.statkart.skif.store.kodeliste.DbKodeSupport;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeId;

/**
 * Id superklasse for alle koder av type CDbKode. Denne klasse kan ikke være abstrakt siden hibernate oppretter
 * instanser av denne type når idene lastes. I hibernate interceptoren bytte CDbKodeId ut med id av riktig
 * subtype.
 *
 * Det at en kode arver fra CDbKode betyder i denne sammenheng blot at koden skal hentes fra tabellen til CDbKode.
 * Det vil f.eks ikke være noen kodeliste som inneholder alle kode av type CDbKode. Det er kun kodelister for
 * hver individuel kode. Det vil heller ikke være noen domeneobjekter som har felter av type CDbKode. Det har ingen
 * mening.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class CDbKodeId<T extends CDbKode> extends DbKodeId<T> implements StoreTestDbKodeId<T> {
    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    protected CDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        return (id!=null && id instanceof CDbKodeId && ((CDbKodeId) id).getValue().equals((((CDbKodeId) id).getValue())) && this.getSnapshotVersion()==((CDbKodeId) id).getSnapshotVersion());
    }

    /**
     * For endelige subklasser av DbSubclassedKodeId returneres unike instanser per kode verdi. For mellomliggende
     * subklasser returners det ikke unike instanser per kode verdi. Se {@link {#getKodeSupport}} for videre forklaring.
     *
     * @return
     */
    @Override
    public final CDbKodeId<T> resolveInstance() {
        DbKodeSupport kodeSupport = getKodeSupport();
        if (kodeSupport==null) {
            return this;
        } else {
            return (CDbKodeId<T>) getKodeSupport().getOrCreateInstance(this);
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
