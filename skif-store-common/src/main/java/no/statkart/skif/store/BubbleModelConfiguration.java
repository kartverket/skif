package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class BubbleModelConfiguration implements BubbleDependencyComparator, Serializable {
    private static final long serialVersionUID = 1;

    private final Set<Class<?>> baseClasses = new LinkedHashSet<>();
    private final Map<Class<? extends BubbleObject>, Integer> bubbleClassDependencyIndex = new HashMap<>();
    private int nextOrderIndex;

    @SafeVarargs
    public final <T extends BubbleObject> BubbleModelConfiguration addBubbleWithSubclasses(Class<T> baseclass, Class<? extends T>... subclasses) {
        return addBubbleWithSubclassesUseNextIndex(baseclass, subclasses);

    }

    @SafeVarargs
    public final <T extends BubbleObject> BubbleModelConfiguration addBubbleWithSubclassesUseNextIndex(Class<T> baseclass, Class<? extends T>... subclasses) {
        addBubble(baseclass);
        addSubclassUseSameIndex(subclasses);
        return this;
    }

    public BubbleModelConfiguration addBubble(Class<? extends BubbleObject> clazz) {
        return addBubbleUseNextIndex(clazz);
    }

    public BubbleModelConfiguration addBubbleUseNextIndex(Class<? extends BubbleObject> clazz) {
        nextOrderIndex++;
        return addBubbleUseSameIndex(clazz);
    }

    public BubbleModelConfiguration addBubbleUseSameIndex(Class<? extends BubbleObject> clazz) {
        baseClasses.add(clazz);
        createDependencyIndex(clazz);
        return this;

    }



    @SafeVarargs
    public final BubbleModelConfiguration addSubclassUseNextIndex(Class<? extends BubbleObject>... classes) {
        nextOrderIndex++;
        addSubclassUseSameIndex(classes);
        return this;
    }

    @SafeVarargs
    public final BubbleModelConfiguration addSubclassUseSameIndex(Class<? extends BubbleObject>... classes) {
        for (Class<? extends BubbleObject> clazz : classes) {
            createDependencyIndex(clazz);
        }
        return this;
    }

    protected void createDependencyIndex(Class<? extends BubbleObject> clazz) {
        final Integer previousIndex = bubbleClassDependencyIndex.put(clazz, nextOrderIndex);
        if (previousIndex != null) {
            throw new ImplementationException("Dependency index for BubbleObject is already defined:" + clazz.getName());
        }
    }


    public BubbleModelConfiguration addEntity(Class<?> clazz) {
        baseClasses.add(clazz);
        return this;
    }


    public Collection<Class<?>> getBaseClasses() {
        return Collections.unmodifiableCollection(baseClasses);
    }

    @Override
    public int compare(BubbleObject o1, BubbleObject o2) {
        // Håndtere også Lazy klasser
        int i1 = getIndex(o1);
        int i2 = getIndex(o2);
        if (i1<i2) return -1;
        if (i1==i2) return 0;
        return 1;
    }

    private Integer getIndex(BubbleObject bubbleObject) {
        final BubbleId<?> bubbleId = bubbleObject.getId();
        final Integer index = bubbleClassDependencyIndex.get(bubbleId.getType());
        if (index==null) {
            throw new ImplementationException("No dependency index defined for BubbleObject class (all subclasses must be specified in BubbleModelConfiguration): " + bubbleId.getType());
        }
        return index;
    }
}
