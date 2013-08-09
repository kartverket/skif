package no.statkart.skif.storetest.service.histtest;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.vividsolutions.jts.geom.*;
import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.*;
import no.statkart.skif.storetest.mockup.MockupSnapshots;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tester historisk findere og navigering mellom bobler via servicekall og Store
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
@Test
public class HistTestServiceTest extends StoreTestTestCase {
    @Inject
    private HistTestService histTestService;
    @Inject
    private Store store;
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;


    public void findHistSimpleIdsForTextUsingJDBC() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Set<HistSimpleId<?>> ids = histTestService.findHistSimpleIdsForTextUsingJDBC("KART-VEIEN", mockupFacade.getTestNumber().getNumber(), MockupSnapshots.S3_30);
        assertEquals(ids.size(), 1);
        final HistSimpleId<?> histSimpleId = ids.iterator().next();
        assertEquals(histSimpleId.getSnapshotVersion(), MockupSnapshots.S3_30);

        // Ta utgangspunkt i oppdateringsdato og sjekk at vi finner samme foo på nytt
        HistSimple histSimple = store.get(histSimpleId);
        assertEquals(histSimple.getText(), "KART-VEIEN");
        Set<HistSimpleId<?>> ids2 = histTestService.findHistSimpleIdsForTextUsingJDBC("KART-VEIEN", mockupFacade.getTestNumber().getNumber(), SnapshotVersion.createInstance(histSimple.getOppdateringsdato()));
        assertEquals(ids2.size(), 1);
        HistSimpleId<?> histSimpleId2 = ids2.iterator().next();
        HistSimple histSimple2 = store.get(histSimpleId2);
        assertTrue(histSimple.sameVersion(histSimple2));
    }

    public void findHistSimpleIdsForTextUsingHibernate() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Set<HistSimpleId<?>> ids = histTestService.findHistSimpleIdsForTextUsingHibernate("KART-VEIEN", mockupFacade.getTestNumber().getNumber(), MockupSnapshots.S3_30);
        assertEquals(ids.size(), 1);
        final HistSimpleId<?> histSimpleId = ids.iterator().next();
        assertEquals(histSimpleId.getSnapshotVersion(), MockupSnapshots.S3_30);

        // Ta utgangspunkt i oppdateringsdato og sjekk at vi finner samme foo på nytt
        HistSimple histSimple = store.get(histSimpleId);
        assertEquals(histSimple.getText(), "KART-VEIEN");
        Set<HistSimpleId<?>> ids2 = histTestService.findHistSimpleIdsForTextUsingJDBC("KART-VEIEN", mockupFacade.getTestNumber().getNumber(), SnapshotVersion.createInstance(histSimple.getOppdateringsdato()));
        assertEquals(ids2.size(), 1);
        HistSimpleId<?> histSimpleId2 = ids2.iterator().next();
        HistSimple histSimple2 = store.get(histSimpleId2);
        assertTrue(histSimple.sameVersion(histSimple2));
    }

    public void findHistWithRelationIdsRelatedToHistSimpleWithText() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Set<HistWithRelationId<?>> histWithRelationIds = histTestService.findHistWithRelationIdsRelatedToHistSimpleWithText("GAMMELVEIEN", mockupFacade.getTestNumber().getNumber(), MockupSnapshots.S2_01);
        assertEquals(histWithRelationIds.size(), 1);
        HistWithRelationId<?> histWithRelationId = histWithRelationIds.iterator().next();
        HistWithRelation histWithRelation = store.get(histWithRelationId);
        HistSimple histSimple = store.get(histWithRelation.getHistSimpleId());
        assertEquals(histSimple.getText(), "GAMMELVEIEN");
        assertEquals(histSimple.getTestSetNumber(), mockupFacade.getTestNumber().getNumber());
        assertTrue(MockupSnapshots.S2_02.between(histSimple.getOppdateringsdato(), histSimple.getSluttdato()));
    }

    public void findHistWithRelationIdsWithTextRelatedToHistSimpleId() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Set<HistWithRelationId<?>> histWithRelationIds = histTestService.findHistWithRelationIdsWithTextRelatedToHistSimpleId("Gruppe A", mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2(), MockupSnapshots.S2_02);
        assertEquals(histWithRelationIds.size(), 1);
        HistWithRelationId<?> histWithRelationId = histWithRelationIds.iterator().next();
        HistWithRelation histWithRelation = store.get(histWithRelationId);
        assertEquals(histWithRelation.getText(), "Gruppe A");
        HistSimple histSimple = store.get(histWithRelation.getHistSimpleId());
        assertEquals(histSimple.getTestSetNumber(), mockupFacade.getTestNumber().getNumber());
        assertTrue(MockupSnapshots.S2_02.between(histSimple.getOppdateringsdato(), histSimple.getSluttdato()));
    }

    public void findHistWithRelationIdsWithTextRelatedToHistSimpleIds() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final ImmutableList<HistSimpleId<?>> histSimpleIds = ImmutableList.of(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(), mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2());
        Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> histWithRelationIdsMap = histTestService.findHistWithRelationIdsWithTextRelatedToHistSimpleIds("Gruppe A", histSimpleIds, MockupSnapshots.S2_02);
        assertEquals(histWithRelationIdsMap.size(), 1);
        final HistSimpleId<?> key = mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2().asSnapshotVersion(MockupSnapshots.S2_02);
        assertThat(histWithRelationIdsMap.get(key)).containsExactly(mockupFacade.getHistWithRelationMockupFactory().getHistWithRelationId1().asSnapshotVersion(MockupSnapshots.S2_02));
    }

    public void findHistSimpleIdsAliveAtSnapshotUsingOracleArray() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final ImmutableList<HistSimpleId<?>> histSimpleIds = ImmutableList.of(
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(),
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2()
        );
        final List<HistSimpleId<?>> histSimpleIdsAliveAtSnapshot = histTestService.findHistSimpleIdsAliveAtSnapshotUsingOracleArray(histSimpleIds, MockupSnapshots.S1);
        assertEquals(histSimpleIdsAliveAtSnapshot.size(), 1);
        assertThat(histSimpleIdsAliveAtSnapshot).contains(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1().asSnapshotVersion(MockupSnapshots.S1));
    }

    public void findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final ImmutableList<HistSimpleId<?>> histSimpleIds = ImmutableList.of(
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1(),
                mockupFacade.getHistSimpleMockupFactory().getHistSimpleId2()
        );
        final List<HistSimpleId<?>> histSimpleIdsAliveAtSnapshot = histTestService.findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(histSimpleIds, MockupSnapshots.S1);
        assertEquals(histSimpleIdsAliveAtSnapshot.size(), 1);
        assertThat(histSimpleIdsAliveAtSnapshot).contains(mockupFacade.getHistSimpleMockupFactory().getHistSimpleId1().asSnapshotVersion(MockupSnapshots.S1));
    }

    public void testFindGeometricElementsWithPointInSelectionPolygon() {

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(100), -1);
        Polygon polygon = factory.createPolygon(factory.createLinearRing(
                new Coordinate[]{
                        new Coordinate(607950, 6649950),
                        new Coordinate(608050, 6649950),
                        new Coordinate(608050, 6650050),
                        new Coordinate(608050, 6650050),
                        new Coordinate(607950, 6649950)
                }), new LinearRing[0]);
        SelectionPolygon selectionPolygon = new SelectionPolygon(polygon);

        List<GeometricElementId> ids = histTestService.findGeometricElementsWithPointInSelectionPolygon(selectionPolygon, SnapshotVersion.createInstance("2011-10-02 08:05:30.00"));

        assertEquals(ids.size(), 1);
    }

    public void testFindGeometricElementsWithPolygonInSelectionPolygon() {

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(100), -1);
        Polygon polygon = factory.createPolygon(factory.createLinearRing(
                new Coordinate[]{
                        new Coordinate(607950, 6649950),
                        new Coordinate(608050, 6649950),
                        new Coordinate(608050, 6650050),
                        new Coordinate(608050, 6650050),
                        new Coordinate(607950, 6649950)
                }), new LinearRing[0]);
        SelectionPolygon selectionPolygon = new SelectionPolygon(polygon);

        List<GeometricElementId> ids = histTestService.findGeometricElementsWithPolygonInSelectionPolygon(selectionPolygon, SnapshotVersion.createInstance("2011-10-02 08:05:30.00"));

        assertEquals(ids.size(), 1);
    }
}
