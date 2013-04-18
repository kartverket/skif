package no.statkart.skif.storetest2.multikobling;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest2.domain.multikobling.Multirefererende;
import no.statkart.skif.storetest2.domain.multikobling.MultirefererendeId;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2TestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester {@link no.statkart.skif.store.multikobling.Multikobling} via {@link Multirefererende}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class MultikoblingTest extends StoreTest2TestCase {
    @Inject
    private StoreTest2MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private RunOnServerWithTxRequiresNewService runOnServerService;

    public void testPersistens() {
        final StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

        final MultirefererendeId<?> id = mockupFacade.getIdService().getNextId(MultirefererendeId.class);

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = new Multirefererende();
                multirefererende.setId(id);

                multirefererende.getMultikobling().get("Over").add("1");
                multirefererende.getMultikobling().get("Over").add("2");

                store.insert(multirefererende);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = store.lock(id);

                Assert.assertEquals(multirefererende.getMultikobling().get("Over").size(), 2, "Feil antall 'over'");

                multirefererende.getMultikobling().get("Over").remove("1");

                store.update(multirefererende);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = store.get(id);

                Assert.assertEquals(multirefererende.getMultikobling().get("Over").size(), 1, "Feil antall 'over'");
                Assert.assertEquals(multirefererende.getMultikobling().get("Over").iterator().next(), "2", "Feil verdi");

                return null;
            }
        });
    }
}
