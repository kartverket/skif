package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockupframework.MockupFacade;
import no.statkart.skif.storetest.mockupframework.MockupFacadeFactory;
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
        final List<SimpleId> ids = new ArrayList<SimpleId>();
        final MockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();
        final IdService idService = mockupFacade.getStore().getInstance(IdService.class);

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {


                for (int i = 0; i < 1000; ++i) {
                    SimpleId<?> id = idService.getNextId(SimpleId.class);
                    ids.add(id);
                    Simple Simple = new Simple();
                    Simple.setId(id);
                    Simple.setText(String.valueOf(i));
                    store.insert(Simple);
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

                    for (SimpleId id : ids) {
                        Simple Simple = new Simple();
                        Simple.setId(id);
                        Simple.setText(String.valueOf(j * 10000 + id.getValue()));
                        store.update(Simple);
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
