package no.statkart.skif.storetest.service.histtest;

import no.statkart.skif.domain.SelectionPolygon;
import no.statkart.skif.mapper.MapperInfo;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public interface HistTestService {
    /**
     * Finner {@code HistSimple} objekter med text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * for gitt tidspunkt {@code snapshotVersion}
     * @param text
     * @param testsettNummer
     * @param snapshotVersion
     * @return  id'er på objekter som ble funnet
     */
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingJDBC(String text, int testsettNummer, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistSimple} objekter med text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * for gitt tidspunkt {@code snapshotVersion}
     * @param text
     * @param testsettNummer
     * @param snapshotVersion
     * @return  id'er på objekter som ble funnet
     */
    public Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingHibernate(String text, int testsettNummer, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som peker på et {@code HistSimple} objekt som har text lik {@code text} hørende til
     * mockup testsett {@code testsettNumber} for gitt tidspunkt {@code snapshotVersion}
     * @param text
     * @param testsettNummer
     * @return id'er på objekter som ble funnet
     */
    public Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som har text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * og som er relatert til et {@code HistSimple} objekt med id lik {@code histSimpleId} for gitt tidspunkt {@code snapshotVersion}
     */
    public Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som har text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * og som er relatert til et {@code HistSimple} objekt med id lik {@code histSimpleId} for gitt tidspunkt {@code snapshotVersion}
     */
    @MapperInfo({HistSimpleId.class, Set.class})
    public Map<HistSimpleId<?>,Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion);

    /**
     * Finner id-ene til alle HistSimple av de som har fått oppgitt sine id-er og som eksisterte på gitt snapshot-tidspunkt.
     *
     * @since 2.1
     */
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingQueryGenerator(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion);

    /**
     * Finner id-ene til alle HistSimple av de som har fått oppgitt sine id-er og som eksisterte på gitt snapshot-tidspunkt.
     *
     * @since 2.1
     */
    public List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingOracleArray(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion);

    public List<GeometricElementId> findGeometricElementsWithPointInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion);

    public List<GeometricElementId> findGeometricElementsWithPolygonInSelectionPolygon(SelectionPolygon selectionPolygon, SnapshotVersion snapshotVersion);

}
