package no.statkart.skif.persistence;

import com.google.inject.Provider;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.util.OracleUtils;
import oracle.jdbc.OracleConnection;
import oracle.sql.STRUCT;
import org.geotools.data.oracle.sdo.GeometryConverter;

import javax.inject.Inject;
import java.sql.Connection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class GeometriTilSdoStructMapper {

    private OracleConnection connection;

    public GeometriTilSdoStructMapper(OracleConnection connection) {
        this.connection = connection;
    }

    public STRUCT createStruct(Geometry geometry, int srid) {
        return createStruct(geometry, srid, 100);
    }

    /**
     * Lager en Oracle SDO STRUCT av en JTS geometri.
     */
    public STRUCT createStruct(Geometry geometry, int srid, int precision) {
        try {
            for (Coordinate coordinate : geometry.getCoordinates()) {
                coordinate.z = Double.NaN;
            }

            GeometryConverter geometryConverter = new GeometryConverter(connection, new GeometryFactory(new PrecisionModel(precision), srid));
            return geometryConverter.toSDO(geometry);
        } catch (Exception e) {
            throw new ImplementationException("Error during transformation of JTS geometry to SDO STRUCT. JTS geometry is " + geometry.toText(), e);
        }
    }

}
