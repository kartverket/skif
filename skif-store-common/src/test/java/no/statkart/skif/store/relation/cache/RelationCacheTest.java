package no.statkart.skif.store.relation.cache;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

@Test
public class RelationCacheTest {

    enum Role implements RelationName {
        rel1,
        rel2
    }

    static class TestBubbleId extends AbstractBubbleId<BubbleObject> {
        private static final long serialVersionUID = 1L;

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }

        TestBubbleId(int idValue) {
            super(idValue);
        }
    }

    final static private TestBubbleId KEY = new TestBubbleId(0);
    final static private TestBubbleId KEY1 = new TestBubbleId(1);
    final static private TestBubbleId KEY2 = new TestBubbleId(2);

    final static private TestBubbleId ID_VALUE = new TestBubbleId(10);
    final static private TestBubbleId ID_VALUE1 = new TestBubbleId(11);
    final static private TestBubbleId ID_VALUE2 = new TestBubbleId(12);
    final static private TestBubbleId ID_VALUE3 = new TestBubbleId(13);
    final static private TestBubbleId ID_VALUE4 = new TestBubbleId(14);

    public void getCachedRelationNamesForEmptyCache() {
        RelationCache cache = new RelationCache();
        assertThat(cache.getCachedRelationNames()).isEmpty();
    }

    public void getCachedRelationNamesForNonEmptyCache() {
        RelationCache cache = new RelationCache();
        cache.setEnabled(0, true);
        cache.materialiseRelation(0, Role.rel1, KEY, ID_VALUE);
        assertThat(cache.getCachedRelationNames()).containsExactly(Role.rel1.toString());
    }

    public void filterOnRelationName() {
        RelationCache cache = new RelationCache();
        cache.setEnabled(0, true);
        cache.materialiseRelation(0, Role.rel1, KEY, ID_VALUE);
        cache.materialiseRelation(0, Role.rel2, KEY1, ID_VALUE1);
        cache.materialiseRelation(0, Role.rel2, KEY2, ID_VALUE2);
        assertThat(cache.getCachedRelationsForName(Role.rel1.toString())).hasSize(1);
        assertThat(cache.getCachedRelationsForName(Role.rel2.toString())).hasSize(2);
    }

    /**
     * Relasjoner som ikke er cachet skal returnere false
     */
    public void isMaterialisedForNonCachedRelation() {
        RelationCache cache = new RelationCache();
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isFalse();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isFalse();
    }

    /**
     * Relasjoner som er cachet men ikke materialisert skal returnere false
     */
    public void isMaterialisedForRelationThatIsNotMaterialised_Variant1() {
        RelationCache cache = new RelationCache();
        cache.getInverseRelation(Role.rel1, KEY, true);
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isFalse();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isFalse();
    }

    /**
     * Relasjoner som er cachet og har fått endret verdier men ikke materialisert skal returnere false
     */
    public void isMaterialisedForRelationThatIsNotMaterialised_Variant2() {
        RelationCache cache = new RelationCache();
        final RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(1, ID_VALUE);
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isFalse();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isFalse();
    }

    /**
     * Relasjoner som er cachet og materialisert skal returnere true
     */
    public void isMaterialisedForRelationThatIsMaterialisedAtLevel0() {
        RelationCache cache = new RelationCache();
        cache.materialiseRelation(0, Role.rel1, KEY, null);
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isTrue();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isTrue();
    }

    /**
     * Relasjoner som er cachet og materialisert på et høyere level blir også materialisert for underliggende levels
     */
    public void isMaterialisedForRelationThatIsMaterialisedAtLevel2() {
        RelationCache cache = new RelationCache();
        cache.materialiseRelation(1, Role.rel1, KEY, null);
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isTrue();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isTrue();
    }

    /**
     * Relasjoner som er cachet og eksplisitt kun materialisert på et høyere level blir ikke  materialisert for underliggende levels
     */
    public void isMaterialisedForRelationThatIsExclusivelyMaterialisedAtHigherLevel() {
        RelationCache cache = new RelationCache();
        final RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.materialise(2, null); // relasjonen er 'null' for level 2, vet ikke hva den er for underliggende levels
        assertThat(cache.isMaterialised(0, Role.rel1, KEY)).isFalse();
        assertThat(cache.isMaterialised(1, Role.rel1, KEY)).isFalse();
        assertThat(cache.isMaterialised(2, Role.rel1, KEY)).isTrue();
    }

    /**
     * Uthenting av RelationValueHolder for ikke cachet relasjon skal gi null
     */
    public void getRelationValueHolderForNonCachedRelation() {
        RelationCache cache = new RelationCache();
        assertThat(cache.getRelationValueHolder(0, Role.rel1, KEY)).isNull();
    }

    /**
     * Uthenting av RelationValueHolder for cachet relasjon som ikke er materialisert skal kaste exception
     */
    public void getRelationValueHolderForNonMaterialisedRelation() {
        RelationCache cache = new RelationCache();
        cache.getInverseRelation(Role.rel1, KEY, true);
        try {
            assertThat(cache.getRelationValueHolder(0, Role.rel1, KEY)).isNotNull();
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessageContaining("Forventet å finne en eller flere RelationTrackers i RelationEntry for level 0");
        }
    }

    /**
     * Uthenting av RelationValueHolder for cachet relasjon som er materialisert skal gi relasjonen i et holder objekt
     */
    public void getRelationValueHolderForMaterialisedRelation() {
        RelationCache cache = new RelationCache();
        cache.materialiseRelation(0, Role.rel1, KEY, null);
        assertThat(cache.getRelationValueHolder(0, Role.rel1, KEY)).isNotNull();
        assertThat(cache.getRelationValueHolder(0, Role.rel1, KEY).getValue()).isNull();
    }

    /**
     * Uthenting av RelationValueHolder for cachet relasjon som eksplisitt er materialisert for et høyere level  skal
     * gi relasjonen i et holder objekt for dette level og exception for lavere levels
     */
    public void getRelationValueHolderForExclusivelyMaterialisedRelationAtHigherLevel() {
        RelationCache cache = new RelationCache();
        final RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.materialise(2, null); // relasjonen er 'null' for level 2, vet ikke hva den er for underliggende levels
        assertThat(cache.getRelationValueHolder(2, Role.rel1, KEY)).isNotNull();
        assertThat(cache.getRelationValueHolder(2, Role.rel1, KEY).getValue()).isNull();
        try {
            assertThat(cache.getRelationValueHolder(0, Role.rel1, KEY)).isNotNull();
            failBecauseExceptionWasNotThrown(ImplementationException.class);
        } catch (ImplementationException e) {
            assertThat(e).hasMessageContaining("Forventet å finne en eller flere RelationTrackers i RelationEntry for level 0");
        }
    }

    /**
     * Oppretter ny relasjon og materialiserer med et Set som inneholder 2 verdier fra før for level 0. Uthenging for
     * alle levels skal bruke samme instans av Set. Alle relations[i>0] skal ikke være materialisert.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationValueNewRelationLevel0() {
        RelationCache cache = new RelationCache();
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        objects.add(ID_VALUE2);
        cache.materialiseRelation(0, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>) cache.getRelationValueHolder(0, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2);
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, false);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[0].getRelation()).isSameAs(objects1);
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2]).isNull();
        assertThat(relationEntry.getRelationValue(0)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(1)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(2)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(3)).isSameAs(objects1);
    }

    /**
     * Oppretter ny relasjon og materialiserer med et Set som inneholder 2 verdier fra før. Dette skjer for level 2.
     * Det at relasjonen blir materialisert for level 2 har ikke noe å si. Matrialisering skjer for level 0.
     * Uthenging for alle levels skal bruke samme instans av Set. Alle relations[i>0] skal ikke være materialisert.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationValueNewRelationLevel2() {
        RelationCache cache = new RelationCache();
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        objects.add(ID_VALUE2);
        cache.materialiseRelation(2, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>) cache.getRelationValueHolder(2, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2);
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, false);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[0].getRelation()).isSameAs(objects1);
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2]).isNull();
        assertThat(relationEntry.getRelationValue(0)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(1)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(2)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(3)).isSameAs(objects1);
    }


    /**
     * Oppretter ny relasjon og legger inn 'add' verdier for level 0. Materialiserer deretter en relasjon som har en
     * verdi fra før. Uthenting skal gi alle 3 verdier. Uthenging for alle levels skal bruke samme instans av Set.
     * Alle relations[i>0] skal ikke være materialisert.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationForExistingRelationWithChangesLevel0() {
        RelationCache cache = new RelationCache();
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(0, ID_VALUE2);
        relationEntry.addId(0, ID_VALUE3);
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        cache.materialiseRelation(0, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>)cache.getRelationValueHolder(0, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2]).isNull();
        assertThat(relationEntry.getRelationValue(0)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(1)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(2)).isSameAs(objects1);
        assertThat(relationEntry.getRelationValue(3)).isSameAs(objects1);
    }

    /**
     * Oppretter ny relasjon og legger inn 'add' verdier for level 0 og 1. Materialiserer deretter en relasjon som har en
     * verdi fra før for level 2. Uthenting skal gi alle 3 verdier for level 2. Relasjonen på level 2 må
     * materialiseres siden den materialiserte relasjonen for level 0 er forskjellig pga. operasjoner på level 1.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationForExistingRelationLevelNVariant1() {
        RelationCache cache = new RelationCache();
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(0, ID_VALUE2);
        relationEntry.addId(1, ID_VALUE3);
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        cache.materialiseRelation(2, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>) cache.getRelationValueHolder(2, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1].isMaterialised()).isFalse();
        assertThat(relationEntry.relations[2].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[3]).isNull();
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(0)).containsOnly(ID_VALUE1, ID_VALUE2);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(2)).isSameAs(objects1);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(3)).isSameAs(objects1);
    }

    /**
     * Oppretter ny relasjon. Legge inn 'add' verdier for level 0 og 2. Materialiserer deretter en relasjon som har en
     * verdi fra før for level 2. Uthenting skal gi alle 3 verdier for level 2. Relasjonen på level 2 må
     * materialiseres siden den materialiserte relasjonen for level 0 er forskjellig pga. operasjoner på level 2.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationForExistingRelationLevelNVariant2() {
        RelationCache cache = new RelationCache();
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(0, ID_VALUE2);
        relationEntry.addId(2, ID_VALUE3);
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        cache.materialiseRelation(2, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>) cache.getRelationValueHolder(2, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[3]).isNull();
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(0)).containsOnly(ID_VALUE1, ID_VALUE2);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(2)).isSameAs(objects1);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(3)).isSameAs(objects1);
    }

    /**
     * Oppretter ny relasjon og legger inn 'add' verdier for level 0 og 2. Materialiserer deretter en relasjon som har en
     * verdi fra før for level 4. Uthenting skal gi alle 3 verdier for level 4 . Relasjonen på level 4 må
     * materialiseres siden den materialiserte relasjonen for level 0 er forskjellig pga operasjoner på level 2.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationForExistingRelationLevelNVariant3() {
        RelationCache cache = new RelationCache();
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(0, ID_VALUE2);
        relationEntry.addId(2, ID_VALUE3);
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        cache.materialiseRelation(4, Role.rel1, KEY, objects);
        Set<BubbleId<?>> objects1 = (Set<BubbleId<?>>) cache.getRelationValueHolder(4, Role.rel1, KEY).getValue();
        assertThat(objects1).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2].isMaterialised()).isFalse();
        assertThat(relationEntry.relations[3]).isNull();
        assertThat(relationEntry.relations[4].isMaterialised()).isTrue();
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(0)).containsOnly(ID_VALUE1, ID_VALUE2);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(4)).isSameAs(objects1);
    }

    /**
     * Oppretter ny relasjon og legger inn 'add' verdier for level 0 og 2. Materialiserer deretter en relasjon som har en
     * verdi fra før for level 0. Henter deretter ut relasjon for level 2. Legger inn en 'add' operasjon for level 3 og
     * henter deretter ut for level 4. Da skal level 0, 2, 4 være materialiser.
     */
    @SuppressWarnings("unchecked")
    public void materialiseRelationForExistingRelationLevelNVariant4() {
        RelationCache cache = new RelationCache();
        RelationCache.RelationEntry relationEntry = cache.getInverseRelation(Role.rel1, KEY, true);
        relationEntry.addId(0, ID_VALUE2);
        relationEntry.addId(2, ID_VALUE3);
        Set<BubbleId<?>> objects = new HashSet<>();
        objects.add(ID_VALUE1);
        cache.materialiseRelation(0, Role.rel1, KEY, objects);
        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(2)).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3);
        relationEntry.addId(3, ID_VALUE4);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[3].isMaterialised()).isFalse();
        assertThat(relationEntry.relations[4]).isNull();

        assertThat((Set<BubbleId<?>>) relationEntry.getRelationValue(4)).containsOnly(ID_VALUE1, ID_VALUE2, ID_VALUE3, ID_VALUE4);
        assertThat(relationEntry.relations[0].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[1]).isNull();
        assertThat(relationEntry.relations[2].isMaterialised()).isTrue();
        assertThat(relationEntry.relations[3].isMaterialised()).isFalse();
        assertThat(relationEntry.relations[4].isMaterialised()).isTrue();
    }
}
