package no.statkart.skif.persistence;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import no.statkart.skif.domain.JTSUtils;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.util.OracleUtils;
import oracle.sql.STRUCT;

import java.sql.Connection;

/**
 * @since 2.5
 * @author Leif Lislegård
 */
class OracleSDOStructBuilder {
    final Polygon polygon;

    protected Connection connection;
    int srid;

    private OracleSDOStructBuilder(Polygon polygon) {
        this.polygon = polygon;
    }

    public static OracleSDOStructBuilder from(SelectionPolygon selectionPolygon) {
        return new OracleSDOStructBuilder(selectionPolygon.getPolygon());
    }

    OracleSDOStructBuilder setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    OracleSDOStructBuilder setSrid(int srid) {
        this.srid = srid;
        return this;
    }

    public STRUCT build() {
        GeometriTilSdoStructMapper sdoStructMapper = new GeometriTilSdoStructMapper(OracleUtils.getOracleConnection(connection));
        Polygon mutablePolygon = instansierOgKopierPolygon(polygon);
        return sdoStructMapper.createStruct(mutablePolygon, srid);
    }

    private Polygon instansierOgKopierPolygon(Polygon polygon) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), srid);
        polygon = JTSUtils.kopierPolygon(polygon, geometryFactory);

        return polygon;
    }



}

