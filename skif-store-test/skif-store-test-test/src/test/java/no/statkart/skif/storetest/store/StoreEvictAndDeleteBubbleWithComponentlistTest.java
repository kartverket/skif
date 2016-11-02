package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import java.util.HashSet;

/**
 * Tester feilen som oppstår i SKIF-231.
 *
 * Problemet her ser ut til å gå på at når man refererer til andre bobler fra en boble i en collection så vil hibernate
 * laste inn den refererte boblen fully initialized.
 *
 * Når man etterpå kjører refresh på den refererte boblen fordi man ønsker å låse den vil hibernate si at den referte boblens
 * collections ikke lengre er initialized. Dette problemet oppstår kun dersom den refererte boblen har mer enn én collection
 * av referte objekter, i dette tilfellet har den to set med components.
 *
 * Når man etter å ha låst den refererte boblen sier delete på denne vil man få en NonUniqueObjectException da components
 * finnes flere ganger i persistencecontext.
 *
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
@Test
public class StoreEvictAndDeleteBubbleWithComponentlistTest extends StoreTestMixedTestCase {

    public void testLockAndDeleteObject(){

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithList bwl = new BubbleWithList();
                bwl.setId(new BubbleWithListId<BubbleWithList>(2201l));
                bwl.setText("text");

                HashSet<BubbleWithListComponent> components = new HashSet<BubbleWithListComponent>();
                BubbleWithListComponent e = new BubbleWithListComponent(2202l, "component for 2201", AEnumKodeId.KodeAId);
                e.setBubbleWithList(bwl);
                components.add(e);
                bwl.setComponents(components);

                HashSet<BubbleWithListComponent2> components2 = new HashSet<BubbleWithListComponent2>();
                BubbleWithListComponent2 e2 = new BubbleWithListComponent2(2203l, "component for 2201");
                e2.setBubbleWithList(bwl);
                components2.add(e2);
                bwl.setComponents2(components2);

                BubbleWithList bwl2 = new BubbleWithList();
                bwl2.setId(new BubbleWithListId<BubbleWithList>(2204l));
                bwl2.setText("text");

                HashSet<BubbleWithListId<?>> otherBWLIds = new HashSet<BubbleWithListId<?>>();
                otherBWLIds.add(new BubbleWithListId<BubbleWithList>(2204L));
                bwl2.setOtherBWLIds(otherBWLIds);

                store.lock(new BubbleWithListId<BubbleWithList>(2204L));
                store.update(bwl2);

                store.lock(bwl.getId());
                store.delete(bwl);

                return null;
            }
        });
    }

}
