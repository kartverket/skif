package no.statkart.skif.storetest.wsapi.service.histtest;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.storetest.wsapi.domain.StoreTestContext;
import no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp;
import no.statkart.skif.storetest.wsapi.domain.basic.*;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;

import javax.jws.WebParam;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface HistTestServiceWSI extends ServiceWSI {

    HistSimpleIdList findHistSimpleIdsForTextUsingJDBC(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistSimpleIdList findHistSimpleIdsForTextUsingHibernate(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistWithRelationIdList findHistWithRelationIdsRelatedToHistSimpleWithText(@WebParam(name = "text") String text, @WebParam(name = "testSetNummer") int testSetNummer, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistWithRelationIdList findHistWithRelationIdsWithTextRelatedToHistSimpleId(@WebParam(name = "text") String text, @WebParam(name = "histSimpleId") HistSimpleId histSimpleId, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistSimpleIdToHistWithRelationIdsMap findHistWithRelationIdsWithTextRelatedToHistSimpleIds(@WebParam(name = "text") String text, @WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    HistSimpleIdList findHistSimpleIdsAliveAtSnapshotUsingOracleArray(@WebParam(name = "histSimpleIds") HistSimpleIdList histSimpleIds, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    GeometricElementIdList findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

    GeometricElementIdList findGeometricElementsWithPolygonInSelectionPolygon(@WebParam(name = "selectionPolygon") SelectionPolygon selectionPolygon, @WebParam(name = "snapshotVersion") Timestamp snapshotVersion, @WebParam(name = "storeTestContext") StoreTestContext storeTestContext) throws ServiceException;

}
