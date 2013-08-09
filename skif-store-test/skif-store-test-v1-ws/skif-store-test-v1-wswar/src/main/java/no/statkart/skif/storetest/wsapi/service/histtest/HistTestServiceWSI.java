package no.statkart.skif.storetest.wsapi.service.histtest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.SnapshotVersion;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListForStoreTestBubbleIdsMap;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon;
import no.statkart.skif.storetest.wsapi.domain.basic.*;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistTestServiceWSI extends ServiceWSI {

    public HistSimpleIdList findHistSimpleIdsForTextUsingJDBC(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public HistSimpleIdList findHistSimpleIdsForTextUsingHibernate(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public HistWithRelationIdList findHistWithRelationIdsRelatedToHistSimpleWithText(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public HistWithRelationIdList findHistWithRelationIdsWithTextRelatedToHistSimpleId(@WebParam(name = "text") String text, @WebParam(name = "histSimpleId") HistSimpleId histSimpleId, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public StoreTestBubbleIdListForStoreTestBubbleIdsMap findHistWithRelationIdsWithTextRelatedToHistSimpleIds(@WebParam(name = "text") String text, @WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingOracleArray(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") SnapshotVersion snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext);

    public GeometricElementIdList findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion,@WebParam(name="storeTestContext") StoreTestContext storeTestContext);

    public GeometricElementIdList findGeometricElementsWithPolygonInSelectionPolygon(@WebParam(name="selectionPolygon") SelectionPolygon selectionPolygon, @WebParam(name="snapshotVersion") SnapshotVersion snapshotVersion,@WebParam(name="storeTestContext") StoreTestContext storeTestContext);
}
