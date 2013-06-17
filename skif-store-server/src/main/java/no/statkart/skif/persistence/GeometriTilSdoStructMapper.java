package no.statkart.skif.persistence;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.util.OracleUtils;
import oracle.jdbc.OracleConnection;
import oracle.sql.STRUCT;
import org.geotools.data.oracle.sdo.GeometryConverter;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class GeometriTilSdoStructMapper {

    private OracleConnection connection;

    OracleUtils oracleUtils = new OracleUtils();

    public GeometriTilSdoStructMapper(OracleConnection connection) {
        this.connection = connection;
    }

    public GeometriTilSdoStructMapper(OracleConnection connection, OracleUtils oracleUtils) {
        this.connection = connection;
        this.oracleUtils = oracleUtils;
    }

    //TODO: Flytt disse til property-fil eller statisk klasse
    public static final int srid = -1;
    public static final int precision = 100;

    public void setOracleUtils(OracleUtils oracleUtils) {
        this.oracleUtils = oracleUtils;
    }

    /**
     * Lager en Oracle SDO STRUCT av en JTS geometri.
     */
    public STRUCT createStruct(Geometry geometry) {
        try {
            for (Coordinate coordinate : geometry.getCoordinates()) {
                coordinate.z = Double.NaN;
            }

            GeometryConverter geometryConverter = new GeometryConverter(connection, new GeometryFactory(new PrecisionModel(precision), oracleUtils.getOracleIntSRID()));
            return geometryConverter.toSDO(geometry);
        } catch (Exception e) {
            throw new ImplementationException("Feil ved transformasjon av JTS geometri til SDO STRUCT. JTS geometri er " + geometry.toText(), e);
        }
    }

}
