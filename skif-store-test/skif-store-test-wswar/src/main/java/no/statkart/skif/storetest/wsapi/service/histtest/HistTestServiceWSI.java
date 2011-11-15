package no.statkart.skif.storetest.wsapi.service.histtest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListForStoreTestBubbleIdsMap;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon;
import no.statkart.skif.storetest.wsapi.domain.demo.*;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistTestServiceWSI extends ServiceWSI {

    public FooIdList findFooIdsForNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public BarFoosIdList findBarFoosIdsSomInneholderFooMedNavn(@WebParam(name = "navn") String navn, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public BarFoosIdList findBarFoosIdsMedBarOgFoo(@WebParam(name = "fooNavn") String fooNavn, @WebParam(name = "barId") BarId barId, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public FooIdList findFooIdsForNr(@WebParam(name = "nr") long nr, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public BarIdList findBarIdsAliveAtSnapshot(@WebParam(name = "barIds") BarIdList barIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext);

    public StoreTestBubbleIdListForStoreTestBubbleIdsMap findBarIdsForFooIds(FooIdList fooIds, SnapshotVersion snapshotVersion, @WebParam(name="storeTestContext") StoreTestContext storeTestContext);

    public GeometricElementIdList findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion,@WebParam(name="storeTestContext") StoreTestContext storeTestContext);

    public GeometricElementIdList findGeometricElementsWithPolygonInSelectionPolygon(@WebParam(name="selectionPolygon") SelectionPolygon selectionPolygon, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion,@WebParam(name="storeTestContext") StoreTestContext storeTestContext);
}
