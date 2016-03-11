package no.statkart.skif.store.relation.cache;

import no.statkart.skif.store.BubbleId;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test av basis funksjonalitet for {@link no.statkart.skif.store.relation.cache.RelationCache}
 *
 * @author Henrik Fredholm
 */
@Test
public class RelationCacheTest {

    enum Role implements RelationName {
        rel1,
        rel2
    }

    final static BubbleId<?> KEY = mock(BubbleId.class);
    final static BubbleId<?> KEY1 = mock(BubbleId.class);
    final static BubbleId<?> KEY2 = mock(BubbleId.class);
    static {
        when(KEY.getValue()).thenReturn(0);
        when(KEY.getValue()).thenReturn(1);
        when(KEY.getValue()).thenReturn(2);
    }

    public void getCachedRelationNamesForEmptyCache() {
        RelationCache cache = new RelationCache();
        assertThat(cache.getCachedRelationNames()).isEmpty();
    }

    public void getCachedRelationNamesForNonEmptyCache() {

        RelationCache cache = new RelationCache();
        cache.setEnabled(0, true);
        cache.setRelationValue(0, Role.rel1, KEY, "value");
        assertThat(cache.getCachedRelationNames()).containsExactly(Role.rel1.toString());
    }

    public void filterOnRelationName() {
        RelationCache cache = new RelationCache();
        cache.setEnabled(0, true);
        cache.setRelationValue(0, Role.rel1, KEY, "value");
        cache.setRelationValue(0, Role.rel2, KEY1, "value1");
        cache.setRelationValue(0, Role.rel2, KEY2, "value2");
        assertThat(cache.filterOnRelationName(Role.rel1.toString())).hasSize(1);
        assertThat(cache.filterOnRelationName(Role.rel2.toString())).hasSize(2);
    }
}
