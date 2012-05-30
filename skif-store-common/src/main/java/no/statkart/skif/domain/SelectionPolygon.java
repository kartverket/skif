package no.statkart.skif.domain;

import com.vividsolutions.jts.geom.Polygon;

import java.io.Serializable;

/**
 * Klasse for søkepolygon som kan gis inn til f.eks. QueryGenerator for søk på SDO-geometri.
 *
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class SelectionPolygon implements Serializable {
    private static final long serialVersionUID = 1;

    private Polygon polygon;

    public SelectionPolygon() {
    }

    public SelectionPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

}
