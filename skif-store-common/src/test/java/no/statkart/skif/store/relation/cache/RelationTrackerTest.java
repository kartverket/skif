package no.statkart.skif.store.relation.cache;

import org.testng.annotations.Test;

import java.util.LinkedHashSet;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test av basis funksjonalitet for {@link RelationTracker}
 * @author Henrik Fredholm
 */
@Test
public class RelationTrackerTest {

   public void trackUnmaterialisedManyRelationAndThenMaterialise() {
       RelationTracker t = new RelationTracker();
       assertFalse(t.isMaterialised());
       t.add(new Id(1));
       t.add(new Id(2));
       t.add(new Id(3));
       t.remove(new Id(1));
       try  {
           t.getRelation();
           failBecauseExceptionWasNotThrown(IllegalStateException.class);
       } catch  (IllegalStateException e) {
           assertThat(e).hasMessage("Relation is not materialised");
           // Expected
       }
       t.materialise(new LinkedHashSet());
       assertThat(t.getManyRelation()).containsExactly(new Id(2), new Id(3));
   }

    public void trackUnmaterialisedOneRelationAndThenMaterialiseNull() {
        RelationTracker t = new RelationTracker();
        assertFalse(t.isMaterialised());
        t.add(new Id(1));
        t.remove(new Id(1));
        t.add(new Id(2));
        try  {
            t.getRelation();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch  (IllegalStateException e) {
            assertThat(e).hasMessage("Relation is not materialised");
            // Expected
        }
        t.materialise(null);
        assertThat(t.getOneRelation()).isEqualTo(new Id(2));
    }

    public void trackUnmaterialisedOneRelationAndThenMaterialiseNotNull() {
        RelationTracker t = new RelationTracker();
        assertFalse(t.isMaterialised());
        t.remove(new Id(1));
        t.add(new Id(2));
        try  {
            t.getRelation();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch  (IllegalStateException e) {
            assertThat(e).hasMessage("Relation is not materialised");
            // Expected
        }
        t.materialise(new Id(1));
        assertThat(t.getOneRelation()).isEqualTo(new Id(2));
    }

    public void trackMaterialisedManyRelationAndThenRetrieve() {
        RelationTracker t = new RelationTracker();
        t.materialise(new LinkedHashSet());
        assertTrue(t.isMaterialised());
        t.add(new Id(1));
        t.add(new Id(2));
        t.add(new Id(3));
        t.remove(new Id(1));
        assertThat(t.getManyRelation()).containsExactly(new Id(2), new Id(3));
        try {
            t.materialise(new LinkedHashSet());
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Relation is already materialised");
        }
    }

    public void trackMaterialisedOneRelationThatIsNullAndThenRetrieve() {
        RelationTracker t = new RelationTracker();
        t.materialise(null);
        assertTrue(t.isMaterialised());
        t.add(new Id(1));
        t.remove(new Id(1));
        t.add(new Id(2));
        assertThat(t.getOneRelation()).isEqualTo(new Id(2));
        try {
            t.materialise(null);
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("Relation is already materialised");
        }
    }

    public void trackMaterialisedOneRelationThatIsNotNullAndThenRetrieve() {
        RelationTracker t = new RelationTracker();
        t.materialise(new Id(1));
        assertTrue(t.isMaterialised());
        t.remove(new Id(1));
        t.add(new Id(2));
        t.remove(new Id(2));
        t.add(new Id(3));
        assertThat(t.getOneRelation()).isEqualTo(new Id(3));
    }

    public void commitIntoUnmaterialisedManyRelation() {
        RelationTracker t = new RelationTracker();
        t.add(new Id(1));
        t.add(new Id(2));
        t.add(new Id(3));
        t.remove(new Id(1));
        RelationTracker t2 = new RelationTracker();
        t2.add(new Id(4));
        t2.remove(new Id(2));
        t2.commitInto(t);
        t.materialise(new LinkedHashSet());
        assertThat(t.getManyRelation()).containsExactly(new Id(3), new Id(4));
    }

    public void commitIntoUnmaterialisedOneRelation() {
        RelationTracker t = new RelationTracker();
        t.add(new Id(1));
        t.remove(new Id(1));
        t.add(new Id(2));
        RelationTracker t2 = new RelationTracker();
        t2.remove(new Id(2));
        t2.add(new Id(3));
        t2.commitInto(t);
        t.materialise(null);
        assertThat(t.getOneRelation()).isEqualTo(new Id(3));
    }

    public void commitIntoMaterialisedManyRelation() {
        RelationTracker t = new RelationTracker();
        t.materialise(new LinkedHashSet());
        assertTrue(t.isMaterialised());
        t.add(new Id(1));
        t.add(new Id(2));
        t.add(new Id(3));
        t.remove(new Id(1));
        RelationTracker t2 = new RelationTracker();
        t2.materialise(t.getRelation());
        t2.add(new Id(4));
        t2.remove(new Id(2));
        assertThat(t2.getManyRelation()).containsExactly(new Id(3), new Id(4));
        t2.commitInto(t);
        assertThat(t.getManyRelation()).containsExactly(new Id(3), new Id(4));

    }

    /**
     * Tester 'commitInto' for umaterialisert relasjon på level 1 hvor relasjon for level 0 er realisert for en
     * 'one'-relation. Resultatet etter commit skal være den siste verdien som ble lagt til level 1 relasjonen.
     */
    public void commitIntoMaterialisedOneRelation() {
        RelationTracker t = new RelationTracker();
        t.materialise(null);  // Initiell verdi for level 0 relasjon er 'null'.
        assertTrue(t.isMaterialised());
        RelationTracker t2 = new RelationTracker();
        assertFalse(t2.isMaterialised());
        t2.add(new Id(1));
        t2.remove(new Id(1));
        final Id FINAL_RESULT = new Id(2);
        t2.add(FINAL_RESULT);

        t2.commitInto(t);
        assertTrue(t.isMaterialised());
        assertThat(t.getRelation()).isSameAs(FINAL_RESULT);
    }

   public static class Id {
       private final long value;

       public Id(long value) {
           this.value = value;
       }

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;

           Id id = (Id) o;

           if (value != id.value) return false;

           return true;
       }

       @Override
       public int hashCode() {
           return (int) (value ^ (value >>> 32));
       }
   }
}
