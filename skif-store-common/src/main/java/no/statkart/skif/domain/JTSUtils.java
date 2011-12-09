package no.statkart.skif.domain;

import com.vividsolutions.jts.geom.*;

/**
 * Diverse verktøy for behandling av JTS
 *
 * @author Leif Lislegård
 * @since 1.9
 */
public class JTSUtils {

   /**
    * Gjør en dyp kopi av et MultiPolygon, da GeometryFactory.createGeometry(...) ikke kopierer Coordinate objektene
    * og dette fører til feil når vi transformerer det kopierte objektet.
    *
    * @param multiPolygon
    * @param geometryFactory
    * @return MultiPolygon
    */
   public static MultiPolygon kopierMultiPolygon(MultiPolygon multiPolygon, GeometryFactory geometryFactory) {
      Polygon[] polygons = new Polygon[multiPolygon.getNumGeometries()];
      for( int i = 0; i < multiPolygon.getNumGeometries(); i++ ) {
         Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);

         polygons[i] = kopierPolygon(polygon, geometryFactory);
      }

      return geometryFactory.createMultiPolygon(polygons);
   }

   /**
    * Gjør en dyp kopi av et Polygon, da GeometryFactory.createGeometry(...) ikke kopierer Coordinate objektene
    * og dette fører til feil når vi transformerer det kopierte objektet.
    *
    * @param polygon
    * @param geometryFactory
    * @return Geometry
    */
   public static Polygon kopierPolygon(Polygon polygon, GeometryFactory geometryFactory) {

      LinearRing intTab[] = new LinearRing[polygon.getNumInteriorRing()];
      LineString exteriorRing = polygon.getExteriorRing();

      Coordinate[] coordinates2 = exteriorRing.getCoordinates();
      Coordinate[] coordinates = new Coordinate[coordinates2.length];
      for( int j = 0; j < coordinates.length; j++ ) {
         coordinates[j] = new Coordinate(coordinates2[j]);
      }
      LinearRing linearRing = geometryFactory.createLinearRing(coordinates);

      for( int p = 0; p < polygon.getNumInteriorRing(); p++ ) {
         LineString interiorRing = polygon.getInteriorRingN(p);

         Coordinate[] intCoordinates2 = interiorRing.getCoordinates();
         Coordinate[] intCoordinates = new Coordinate[intCoordinates2.length];
         for( int j = 0; j < intCoordinates.length; j++ ) {
            intCoordinates[j] = new Coordinate(intCoordinates2[j]);
         }
         intTab[p] = geometryFactory.createLinearRing(intCoordinates);
      }

      return geometryFactory.createPolygon(linearRing, intTab);
   }
}
