package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import com.google.inject.Key;
import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.storetest.wsapi.exception.impl.mapping.StoreTestExceptionMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Henrik Fredholm
 */
@Test
public class StoreServiceTest extends StoreTestTestCase {

    @Inject
    private StoreService storeService;

    public void testStoreService() {
        StoreService store = injector.getInstance(Key.get(StoreService.class));
        TestBubbleId<?> a1Id = new TestBubbleId<TestBubble>(1);
        List<TestBubbleId> ids = new ArrayList<TestBubbleId>();
        ids.add(a1Id);

        TestBubble bubble = store.getObject(a1Id);
        assertEquals(a1Id, bubble.getId());

        List<TestBubble> bubbles = store.getObjects(ids);
        assertEquals(1, bubbles.size());
        assertEquals(a1Id, bubbles.get(0).getId());
    }

    public void testStoreGetOld() {
        StoreService store = injector.getInstance(Key.get(StoreService.class));
        TestBubbleId<?> a1Id = new TestBubbleId<TestBubble>(1L, SnapshotVersion.OLD);

        TestBubble bubble = store.getObject(a1Id);
        assertEquals(a1Id, bubble.getId());
        assertEquals(bubble.getId().getSnapshotVersion(), SnapshotVersion.OLD);
    }

}