package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.mockup.MockupFacade;
import no.statkart.skif.storetest.mockup.MockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tester ytelse på massive oppdateringsoperasjoner.
 */
@Test(enabled = false)
public class UpdateYtelseTest extends StoreTestMixedTestCase {
    @Inject
    private MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private Store clientStore;

    public void test() {
        final List<TestBubbleId> ids = new ArrayList<TestBubbleId>();
        final MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {


                for (int i = 0; i < 1000; ++i) {
                    TestBubbleId<?> id = idService.getNextId(TestBubbleId.class);
                    ids.add(id);
                    TestBubble testBubble = new TestBubble();
                    testBubble.setId(id);
                    testBubble.setText(String.valueOf(i));
                    store.insert(testBubble);
                }

                return null;
            }
        });

        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;
        long total = 0;
        final int ITERATIONS = 100;

        for (int i = 0; i < ITERATIONS; ++i) {
            final int j = i;

            clientStore.lock(ids);

            final long start = System.nanoTime();

            server.runInTxRequiresNew(new RunOnServerMethod() {
                @Inject
                private Store store;

                @Override
                public Object run() {
//                    store.lock(ids);
                    store.get(ids);

                    for (TestBubbleId id : ids) {
                        TestBubble testBubble = new TestBubble();
                        testBubble.setId(id);
                        testBubble.setText(String.valueOf(j * 10000 + id.getValue()));
                        store.update(testBubble);
                    }

                    return null;
                }
            });

            final long end = System.nanoTime();
            final long time = end - start;

            max = Math.max(max, time);
            min = Math.min(min, time);
            total += time;
        }

        long avg = total / ITERATIONS;

        System.out.println(min);
        System.out.println(avg);
        System.out.println(max);
    }
}
