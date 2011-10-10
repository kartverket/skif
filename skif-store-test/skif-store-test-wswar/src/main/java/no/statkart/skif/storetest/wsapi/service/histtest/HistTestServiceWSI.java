package no.statkart.skif.storetest.wsapi.service.histtest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.BarFoosIdList;
import no.statkart.skif.storetest.wsapi.domain.BarId;
import no.statkart.skif.storetest.wsapi.domain.FooIdList;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistTestServiceWSI extends ServiceWSI {

    public FooIdList findFooIdsForNavn(String navn, SnapshotVersion snapshotVersion);
    public BarFoosIdList findBarFoosIdsSomInneholderFooMedNavn(String navn, SnapshotVersion snapshotVersion);
    public BarFoosIdList findBarFoosIdsMedBarOgFoo(String fooNavn, BarId barId);

}
