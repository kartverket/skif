package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * @since 2.1
 * @author Jan Holmen
 */
public class ChildBubble extends AbstractBubbleObject implements StoreTestBubble {
    private String text;
    private TestBubbleId<TestBubble> testBubbleId;


    public ChildBubble() {
    }

    public ChildBubble(BubbleId<?> id) {
        super(id);
    }

    public ChildBubble(BubbleId<?> id, String text) {
        super(id);
        this.text = text;
    }
    public ChildBubble(BubbleId<?> id, String text, TestBubbleId<TestBubble> tbid) {
        super(id);
        this.text = text;
        this.testBubbleId = tbid;
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId((ChildBubbleId<?>) id);
    }

    @Override
    public ChildBubbleId<?> getId() {
        return (ChildBubbleId<?>) super.getId();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public TestBubbleId getTestBubbleId() {
        return testBubbleId;
    }

    public void setTestBubbleId(TestBubbleId testBubbleId) {
        this.testBubbleId = testBubbleId;
    }
    public TestBubble getTestBubble(){
        return store().get(testBubbleId);
    }

    @Override
    public String toString() {
        return "ChildBubble{" +
                "testBubbleId=" + testBubbleId +
                ", text='" + text + '\'' +
                '}';
    }

}
