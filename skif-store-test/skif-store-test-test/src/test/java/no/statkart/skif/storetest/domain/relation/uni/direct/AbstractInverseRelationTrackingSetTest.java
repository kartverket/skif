package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.store.AbstractInverseRelationTrackingSet;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.InverseRelationParticipation;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWork;
import no.statkart.skif.store.ValueObject;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.BeforeMethod;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tester tracking av inversrelasjoner når objekter med relasjonspeker ligger i et Set. Når innhold i settet endres
 * så endres også inversrelasjoner til den boblen som eier settet. Hvis settets elementer er Component eller
 * ValueObject kan flere inversrelasjoner endres seg samtidig.
 * <p/>
 * Model 1:
 * X1AA1 -REL-*> X1CCMany. X1AA1 har en one-many relasjon til X1CCMany. Dvs. det er kun X1AA som kan peke på samme
 * X1CCMany. Som relasjonsnavn brukes REL1.
 * <p/>
 * Model 2:
 * X1AA1 *-REL-*> X1CCMany. X1AA1 har en many-many relasjon til X1CCMany. Dvs. det er fler X1AA som kan peke på samme
 * X1CCMany. Som relasjonsnavn brukes REL1.
 * <p/>
 * Model 3:
 * X1AA1 ---*> VO -REL1-> X1CCMany og X1AA1 ---*> VO -REL2-> X1CCMany . X1AA1 har one-many relasjoner til VO som har to
 * relasjoner til X1CCMany. REL1 og REL2 representerer de to inversrelasjonene.
 *
 * @since 2.8.0
 */
public class AbstractInverseRelationTrackingSetTest extends StoreTestTestCase {
    @Inject
    Store store;

    final static RelationName REL1 = new RelationName() {
    };
    final static RelationName REL2 = new RelationName() {
    };

    static class RelationTrackingSetWithOneRelation extends AbstractInverseRelationTrackingSet<X1CCManyId<?>> {
        public <O extends BubbleObject & InverseRelationParticipation> RelationTrackingSetWithOneRelation(O owner, Set<X1CCManyId<?>> delegate) {
            super(owner, delegate);
        }

        @Override
        protected Map<RelationName, Object> getInverseRelationValues(X1CCManyId<?> element) {
            return ImmutableMap.<RelationName, Object>of(REL1, element);
        }
    }

    static class VO implements ValueObject {
        private X1CCManyId<?> ref1Id;
        private X1CCManyId<?> ref2Id;

        public VO(X1CCManyId<X1CCMany> ref1Id, X1CCManyId<X1CCMany> ref2Id) {
            this.ref1Id = ref1Id;
            this.ref2Id = ref2Id;
        }

        X1CCManyId<?> getRef1Id() {
            return ref1Id;
        }

        X1CCManyId<?> getRef2Id() {
            return ref2Id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VO vo = (VO) o;

            if (ref1Id != null ? !ref1Id.equals(vo.ref1Id) : vo.ref1Id != null) return false;
            if (ref2Id != null ? !ref2Id.equals(vo.ref2Id) : vo.ref2Id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = ref1Id != null ? ref1Id.hashCode() : 0;
            result = 31 * result + (ref2Id != null ? ref2Id.hashCode() : 0);
            return result;
        }
    }

    static class RelationTrackingSetWithMultipleRelations extends AbstractInverseRelationTrackingSet<VO> {
        public <O extends BubbleObject & InverseRelationParticipation> RelationTrackingSetWithMultipleRelations(O owner, Set<VO> delegate) {
            super(owner, delegate);
        }

        @Override
        protected Map<RelationName, Object> getInverseRelationValues(VO element) {
            return ImmutableMap.<RelationName, Object>of(REL1, element.getRef1Id(), REL2, element.getRef2Id());
        }
    }

    StoreRelationCache relationCache;
    X1AA a1;
    X1AA a2;
    HashSet<X1CCManyId<?>> a1ccManyIdsDelegate;
    HashSet<X1CCManyId<?>> a2ccManyIdsDelegate;
    HashSet<VO> a1VODelegate;
    RelationTrackingSetWithOneRelation a1ccManyIds;
    RelationTrackingSetWithOneRelation a2ccManyIds;
    RelationTrackingSetWithMultipleRelations a1V0s;
    final X1CCManyId<X1CCMany> ccManyId1 = new X1CCManyId<>(1L);
    final X1CCManyId<X1CCMany> ccManyId2 = new X1CCManyId<>(2L);
    final X1CCManyId<X1CCMany> ccManyId3 = new X1CCManyId<>(3L);

    @BeforeMethod
    protected void setUp() {
        relationCache = store.getRelationCache();
        a1 = new X1AA();
        a2 = new X1AA();
        a1ccManyIdsDelegate = new HashSet<>();
        a1ccManyIds = new RelationTrackingSetWithOneRelation(a1, a1ccManyIdsDelegate);
        a2ccManyIdsDelegate = new HashSet<>();
        a2ccManyIds = new RelationTrackingSetWithOneRelation(a2, a2ccManyIdsDelegate);
        a1VODelegate = new HashSet<>();
        a1V0s = new RelationTrackingSetWithMultipleRelations(a1, a1VODelegate);
    }

    /**
     * Model 1: legg til objekt i settet
     */
    public void addOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.add(ccManyId1);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.add(ccManyId2);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.add(ccManyId3);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 1: legg til flere objekter i settet samtidig
     */
    public void addAllOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2, ccManyId3));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 1: fjern tidligere objekt
     */
    public void removeOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2, ccManyId3));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            a1ccManyIds.remove(ccManyId2);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 1: Fjern mange objekter
     */
    public void removeAllOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2, ccManyId3));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            a1ccManyIds.removeAll(ImmutableSet.of(ccManyId1, ccManyId2, ccManyId3));
            assertThat(a1ccManyIdsDelegate).isEmpty();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isNull();
        }
    }

    /**
     * Model 1: Fjern alle objekter
     */
    public void clearOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2, ccManyId3));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            a1ccManyIds.clear();
            assertThat(a1ccManyIdsDelegate).isEmpty();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isNull();
        }
    }

    /**
     * Model 1: flytt tidligere lagt til objekt til et set tilhørende en annen boble
     */
    public void moveOperationForSingleInverseRelationWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            a1ccManyIds.add(ccManyId1);
            a2ccManyIds.add(ccManyId2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a2.getId());
            a2ccManyIds.remove(ccManyId2);
            a1ccManyIds.add(ccManyId2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekOne()).isEqualTo(a1.getId());
        }
    }

    ///

    /**
     * Model 2: legg til objekter i flere settet samtidig (a1 og a2).
     */
    public void addOperationForSingleInverseRelationWithCardinalityMany() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.add(ccManyId1);
            a2ccManyIds.add(ccManyId1);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1);
            assertThat(a2ccManyIdsDelegate).containsOnly(ccManyId1);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekMany()).containsOnly(a1.getId(), a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.add(ccManyId3);
            a2ccManyIds.add(ccManyId3);
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId3);
            assertThat(a2ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId3);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekMany()).containsOnly(a1.getId(), a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekMany()).containsOnly(a1.getId(), a2.getId());
        }
    }

    /**
     * Model 2: legg til flere objekter hvor objektene ligger i flere settet samtidig (a1 og a2).
     */
    public void addManyOperationForSingleInverseRelationWithCardinalityMany() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            a2ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            assertThat(a2ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekMany()).containsOnly(a1.getId(), a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekMany()).containsOnly(a1.getId(), a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
        }
    }

    /**
     * Model 2: fjern objekt hvor objektene ligger i flere set samtidig (a1 og a2).
     */
    public void removeOperationForSingleInverseRelationWithCardinalityMany() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            a2ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            assertThat(a2ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            a1ccManyIds.remove(ccManyId1);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekMany()).containsOnly(a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekMany()).containsOnly(a1.getId(), a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
        }
    }

    /**
     * Model 2: fjern objekter hvor objektene ligger i flere set samtidig (a1 og a2).
     */
    public void removeAllOperationForSingleInverseRelationWithCardinalityMany() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            a2ccManyIds.addAll(ImmutableSet.of(ccManyId1, ccManyId2));
            assertThat(a1ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            assertThat(a2ccManyIdsDelegate).containsOnly(ccManyId1, ccManyId2);
            a1ccManyIds.removeAll(ImmutableSet.of(ccManyId1, ccManyId2));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekMany()).containsOnly(a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2).peekMany()).containsOnly(a2.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
        }
    }

    /**
     * Model 3: legg til et VO objekt med flere relasjoner
     */
    public void addOperationForMultipleInverseRelationsWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1V0s.add(new VO(ccManyId1, ccManyId2));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 3: legg til flere VO objekter med flere relasjoner
     */
    public void addManyOperationForMultipleInverseRelationsWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3)).isNull();
            a1V0s.addAll(ImmutableSet.of(new VO(ccManyId1, ccManyId2), new VO(ccManyId3, ccManyId3)));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 3: fjern ett VO objekt med flere relasjoner
     */
    public void removeOperationForMultipleInverseRelationsWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            a1V0s.addAll(ImmutableSet.of(new VO(ccManyId1, ccManyId2), new VO(ccManyId3, ccManyId3)));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId3).peekOne()).isEqualTo(a1.getId());
            a1V0s.remove(new VO(ccManyId1, ccManyId2));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }

    /**
     * Model 3: fjern ett VO objekt med flere relasjoner
     */
    public void removeAllOperationForMultipleInverseRelationsWithCardinalityOne() {
        try (UnitOfWork ignore = store.beginUnitOfWork()) {
            relationCache.setEnabled(true);
            store.insert(a1);
            store.insert(a2);
            a1V0s.addAll(ImmutableSet.of(new VO(ccManyId1, ccManyId2), new VO(ccManyId3, ccManyId3)));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId3).peekOne()).isEqualTo(a1.getId());
            a1V0s.removeAll(ImmutableSet.of(new VO(ccManyId1, ccManyId2)));
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId1).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId2)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId1)).isNull();
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId2).peekOne()).isNull();
            assertThat(relationCache.peekRelationTracker(REL1, ccManyId3).peekOne()).isEqualTo(a1.getId());
            assertThat(relationCache.peekRelationTracker(REL2, ccManyId3).peekOne()).isEqualTo(a1.getId());
        }
    }
}
