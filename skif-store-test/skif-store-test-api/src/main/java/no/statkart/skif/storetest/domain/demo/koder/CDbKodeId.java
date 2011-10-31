package no.statkart.skif.storetest.domain.demo.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kode.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kode.StoreTestDbSubclassedKodeIdImpl;

/**
 * Id superklasse for alle koder av type CDbKode. Denne klasse kan ikke være abstrakt siden hibernate oppretter
 * instanser av denne type når idene lastes. I hibernate interceptoren bytte CDbKodeId ut med id av riktig
 * subtype.
 *
 * Det at en kode aver fra CDbKode betyder i denne sammenheng blot at koden skal hentes fra tabellen til CDbKode.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class CDbKodeId<T extends CDbKode> extends StoreTestDbSubclassedKodeIdImpl<T> implements StoreTestDbKodeId<T> {

    protected CDbKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

}
