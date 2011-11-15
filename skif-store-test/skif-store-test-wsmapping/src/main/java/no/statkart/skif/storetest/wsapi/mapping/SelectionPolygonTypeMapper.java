package no.statkart.skif.storetest.wsapi.mapping;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.storetest.wsapi.domain.basetyper.Position;
import no.statkart.skif.storetest.wsapi.domain.basetyper.PositionList;
import no.statkart.skif.exception.ValidationException;

import java.util.List;

/**
 * @author Roar Ingebrigtsen
 * @since 1.0
 */
public class SelectionPolygonTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon, SelectionPolygon> {

    public SelectionPolygonTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon.class, SelectionPolygon.class);
    }

    @Override
    public void mapDomainObject(SelectionPolygon source, no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon target) {
        super.mapDomainObject(source, target);

        target.setPositions(createPositionList(source.getPolygon()));
    }


    @Override
    public void mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.basetyper.SelectionPolygon source, SelectionPolygon target) {
        super.mapWsapiObject(source, target);

        target.setPolygon(createPolygon(source.getPositions()));

    }

    private PositionList createPositionList(Polygon polygon) {
        PositionList positionList = new PositionList();

        for (Coordinate coordinate : polygon.getExteriorRing().getCoordinates()) {
            Position position = new Position();
            position.setX(coordinate.x);
            position.setY(coordinate.y);
            position.setZ(coordinate.z);
            positionList.getItem().add(position);
        }

        Position sistePos = new Position();
        Coordinate sisteCoord = polygon.getExteriorRing().getCoordinateN(0);
        sistePos.setX(sisteCoord.x);
        sistePos.setY(sisteCoord.y);
        sistePos.setZ(sisteCoord.z);

        return positionList;
    }

    public Polygon createPolygon(PositionList positionList){

        Coordinate[] coordinates;

        List<Position> item = positionList.getItem();
        if(erSammePosition(item.get(0), (item.get(item.size() - 1)))) {
            coordinates = new Coordinate[item.size()];
        } else {
            throw new ValidationException("positionList må starte og slutte med samme position for å være lukket!");
        }

        int i = 0;
        for (Position position : item) {
            coordinates[i++] = new Coordinate(position.getX(), position.getY(), position.getZ());
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);

        return geometryFactory.createPolygon(linearRing, new LinearRing[0]);

    }

    private boolean erSammePosition(Position position, Position position2) {
        return position.getX() == position2.getX() && position.getY() == position2.getY() && ((position.getZ() == null && position2.getZ() == null) || position.getZ().equals(position2.getZ()));
    }

}