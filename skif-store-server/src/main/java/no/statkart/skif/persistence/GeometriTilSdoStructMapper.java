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

    //TODO: Flytt disse til property-fil eller statisk klasse
    public static final int srid = -1;
    public static final int precision = 100;

    /**
     * Lager en Oracle SDO STRUCT av en JTS geometri.
     */
    public STRUCT createStruct(Geometry geometry) {
        try {
            for (Coordinate coordinate : geometry.getCoordinates()) {
                coordinate.z = Double.NaN;
            }

            GeometryConverter geometryConverter = new GeometryConverter(connection, new GeometryFactory(new PrecisionModel(precision), OracleUtils.getOracleIntSRID()));
            return geometryConverter.toSDO(geometry);
        } catch (Exception e) {
            throw new ImplementationException("Error during transformation of JTS geometry to SDO STRUCT. JTS geometry is " + geometry.toText(), e);
        }
    }

}
