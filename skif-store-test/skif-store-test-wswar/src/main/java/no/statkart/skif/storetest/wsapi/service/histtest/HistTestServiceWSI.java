package no.statkart.skif.storetest.wsapi.service.histtest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.demo.*;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistTestServiceWSI extends ServiceWSI {

    public FooIdList findFooIdsForNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public BarFoosIdList findBarFoosIdsSomInneholderFooMedNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public BarFoosIdList findBarFoosIdsMedBarOgFoo(@WebParam(name = "fooNavn") String fooNavn, @WebParam(name = "barId") BarId barId, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public FooIdList findFooIdsForNr(@WebParam(name = "nr") long nr, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);
}
