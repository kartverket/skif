package no.statkart.skif.storetest.domain.relation.many;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = "singlevm-required")
public class OneToManyBubbleRefTest extends StoreTestTestCase {
    @Inject
    private RunOnServerWithTxRequiredService runOnServerWithTxRequiredService;

    public void persist() {
        BubbleWithManyBubblesId<?> rootId = (BubbleWithManyBubblesId<?>) runOnServerWithTxRequiredService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithManyBubbles root = new BubbleWithManyBubbles();
                root.setText("root");
                store.insert(root);

                ManyBubbles first = new ManyBubbles();
                first.setText("first");
                store.insert(first);
                ManyBubbles second = new ManyBubbles();
                second.setText("second");
                store.insert(second);

                root.getMyBubbleIds().add(first.getId());
                root.getMyBubbleIds().add(second.getId());

                return root.getId();
            }
        });

        runOnServerWithTxRequiredService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithManyBubbles root = store.get(rootId);
                Assert.assertEquals("root", root.getText());

                List<ManyBubbles> manyBubbles = root.getManyBubbles();
                Assert.assertEquals(2, manyBubbles.size());
                Assert.assertEquals("first", manyBubbles.get(0).getText());
                Assert.assertEquals("second", manyBubbles.get(1).getText());

                return null;
            }
        });
    }
}
