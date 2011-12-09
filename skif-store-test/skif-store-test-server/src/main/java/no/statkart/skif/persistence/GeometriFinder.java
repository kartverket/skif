package no.statkart.skif.persistence;

import com.google.inject.Inject;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSession;
import no.statkart.skif.store.persistence.hibernate.HibernateStoreSessionManager;
import no.statkart.skif.storetest.domain.demo.GeometricElement;
import no.statkart.skif.storetest.domain.demo.GeometricElementId;

import java.sql.Connection;
import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class GeometriFinder {

    @Inject
    private ConnectionManager connectionManager;

    public List<GeometricElementId>  findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {

        Connection connection = connectionManager.aquireConnection(snapshotVersion);

        QueryGenerator generator = new QueryGenerator("id", "geometricelement");
        generator.setConnection(connection, snapshotVersion);
        generator.addSelection("point", selectionPolygon);

        connectionManager.releaseConnection(connection, snapshotVersion);

        return generator.executeQueryForBubbleIdList(GeometricElementId.class);
    }

    public List<GeometricElementId> findGeometricElementsWithPolygonInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion) {

        Connection connection = connectionManager.aquireConnection(snapshotVersion);

        QueryGenerator generator = new QueryGenerator("id", "geometricelement");
        generator.setConnection(connection, snapshotVersion);
        generator.addSelection("polygon", selectionPolygon);

        connectionManager.releaseConnection(connection, snapshotVersion);

        return generator.executeQueryForBubbleIdList(GeometricElementId.class);
    }

}
