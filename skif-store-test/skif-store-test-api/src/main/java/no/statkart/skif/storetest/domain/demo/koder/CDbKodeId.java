package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;

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
public class CDbKodeId<T extends CDbKode> extends StoreTestDbKodeId<T> {

    protected CDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public boolean equals(Object id) {
        if (this==id) return true;
        // Todo: Tror ikke denne er helt riktig C1DbKodeId(1) skal ikke være lik C2DbKodeId(1).
        return (id instanceof CDbKodeId
                && getValue().equals((((CDbKodeId) id).getValue()))
                && getSnapshotVersion() == ((CDbKodeId) id).getSnapshotVersion());
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        throw new UnsupportedOperationException("Må implementeres i subklasse ");
    }
}
