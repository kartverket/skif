package no.statkart.skif.storetest.service.store2;

import com.google.inject.Inject;
import com.google.inject.Key;
import no.statkart.skif.storetest.domain2.TestBubble2;
import no.statkart.skif.storetest.domain2.TestBubbleId2;
import no.statkart.skif.storetest.service2.store2.StoreService2;
import no.statkart.skif.storetest.util.testsupport2.StoreTestTestCase2;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreServiceTest2 extends StoreTestTestCase2 {



    @Inject
    private StoreService2 storeService;

    public void testStoreService() {
        StoreService2 store = injector.getInstance(Key.get(StoreService2.class));
        TestBubbleId2<?> a1Id = new TestBubbleId2<TestBubble2>(1);
        List<TestBubbleId2> ids = new ArrayList<TestBubbleId2>();
        ids.add(a1Id);

        TestBubble2 bubble = store.getObject(a1Id);
        assertEquals(a1Id, bubble.getId());

        List<TestBubble2> bubbles = store.getObjects(ids);
        assertEquals(1, bubbles.size());
        assertEquals(a1Id, bubbles.get(0).getId());
    }
}