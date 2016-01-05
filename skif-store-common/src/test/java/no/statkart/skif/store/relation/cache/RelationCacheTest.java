package no.statkart.skif.store.relation.cache;

import org.testng.annotations.Test;

import java.util.LinkedHashSet;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test av basis funksjonalitet for {@link no.statkart.skif.store.relation.cache.RelationCache}
 * @author Henrik Fredholm
 *
 *
 */
@Test
public class RelationCacheTest {

    enum Role implements RelationName {
        rel1,
        rel2

    }

   public void getCachedRelationNamesForEmptyCache() {
       RelationCache cache = new RelationCache();
       assertThat(cache.getCachedRelationNames()).isEmpty();
   }

   public void getCachedRelationNamesForNonEmptyCache() {
       RelationCache cache = new RelationCache();
       cache.setEnabled(0, true);
       cache.setRelationValue(0, Role.rel1, "key", "value");
       assertThat(cache.getCachedRelationNames()).containsExactly(Role.rel1.toString());
   }

    public void filterOnRelationName() {
        RelationCache cache = new RelationCache();
        cache.setEnabled(0, true);
        cache.setRelationValue(0, Role.rel1, "key", "value");
        cache.setRelationValue(0, Role.rel2, "key1", "value1");
        cache.setRelationValue(0, Role.rel2, "key2", "value2");
        assertThat(cache.filterOnRelationName(Role.rel1.toString())).hasSize(1);
        assertThat(cache.filterOnRelationName(Role.rel2.toString())).hasSize(2);
    }
}
