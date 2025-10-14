package no.statkart.skif.storetest.domain.relation.many;

import no.statkart.skif.store.WithOneToManyBubbleRef;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Boble som eier en relasjon til mangle {@link ManyBubbles}.
 */
public class BubbleWithManyBubbles extends AbstractStoreTestBubble implements WithOneToManyBubbleRef {
    private String text;
    private List<ManyBubblesId<?>> myBubbleIds = new ArrayList<>();
    private transient List<ManyBubbles> myBubblesSet = new ArrayList<>();

    @Override
    public BubbleWithManyBubblesId<?> getId() {
        return (BubbleWithManyBubblesId<?>) super.getId();
    }

    public void setId(BubbleWithManyBubblesId<?> id) {
        super.setId(id);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ManyBubbles> getManyBubbles() {
        return store().getOrdered(getMyBubbleIds());
    }

    public List<ManyBubblesId<?>> getMyBubbleIds() {
        return myBubbleIds;
    }

    public void setMyBubbleIds(List<ManyBubblesId<?>> myBubbleIds) {
        this.myBubbleIds = myBubbleIds;
    }

    // Hibernate
    private List<ManyBubbles> getMyBubblesSet() {
        return myBubblesSet;
    }

    // Hibernate
    private void setMyBubblesSet(List<ManyBubbles> manyBubblesSet) {
        this.myBubblesSet = manyBubblesSet;
    }

    @Override
    public void preFlush(BiFunction<Class<?>, Serializable, Object> loader) {
        System.out.println("preFlush");
        List<ManyBubbles> newList = getMyBubbleIds().stream().map(id -> ((ManyBubbles) loader.apply(ManyBubbles.class, id))).collect(Collectors.toList());
        if (!newList.equals(myBubblesSet)) {
            System.out.println("preFlushX");
            // Kan muligens optimaliseres bedre. Sjekk hvordan Hibernate gjør dirty-tracking på PersistentList
            myBubblesSet.clear();
            myBubblesSet.addAll(newList);
        }
    }

    @Override
    public void postLoad() {
        System.out.println("postLoad");
        myBubbleIds = myBubblesSet.stream().map(ManyBubbles::getId).collect(Collectors.toList());
    }
}
