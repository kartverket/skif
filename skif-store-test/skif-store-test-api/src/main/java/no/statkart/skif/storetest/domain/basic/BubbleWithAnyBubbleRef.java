package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.ConcatenatedFields;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Boble uten historikk og som kan ha relasjon til en vilkårlig annen bobleId
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithAnyBubbleRef extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;

    private int nr;
    private BubbleId anyId;
    private SomeIdent someIdent;
    private AnyIdEmbeddable anyIdEmbeddable;
    private SomeIdentEmbeddable someIdentEmbeddable;

    public BubbleWithAnyBubbleRef() {
    }

    public BubbleWithAnyBubbleRef(BubbleWithAnyBubbleRefId<?> id) {
        super(id);
    }

    public BubbleWithAnyBubbleRef(BubbleWithAnyBubbleRefId<?> id, BubbleId anyId) {
        this.id = id;
        setAnyId(anyId);
    }

    @Override
    public BubbleWithAnyBubbleRefId<?> getId() {
        return (BubbleWithAnyBubbleRefId<?>) super.getId();
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public BubbleId getAnyId() {
        if (anyId == null && anyIdEmbeddable != null
                && anyIdEmbeddable.getValue() != null
                && anyIdEmbeddable.getClassName() != null) {
            setAnyIdEmbeddable(anyIdEmbeddable);
        }
        return anyId;
    }

    public void setAnyId(BubbleId anyId) {
        this.anyId = anyId;
        if (anyId == null) {
            this.anyIdEmbeddable = null;
        } else {
            AnyIdEmbeddable embeddable = new AnyIdEmbeddable();
            embeddable.setValue(String.valueOf(anyId.getValue()));
            embeddable.setClassName(anyId.getBaseIdType().getName());
            this.anyIdEmbeddable = embeddable;
        }
    }

    public SomeIdent getSomeIdent() {
        if (someIdent == null && someIdentEmbeddable != null
                && someIdentEmbeddable.getValue() != null
                && someIdentEmbeddable.getClassName() != null) {
            setSomeIdentEmbeddable(someIdentEmbeddable);
        }
        return someIdent;
    }

    public void setSomeIdent(SomeIdent someIdent) {
        this.someIdent = someIdent;
        if (someIdent == null) {
            this.someIdentEmbeddable = null;
        } else {
            SomeIdentEmbeddable embeddable = new SomeIdentEmbeddable();
            embeddable.setValue(someIdent.toConcatinatedFields().getValue());
            embeddable.setClassName(someIdent.getClass().getName());
            this.someIdentEmbeddable = embeddable;
        }
    }

    public AnyIdEmbeddable getAnyIdEmbeddable() {
        if (anyIdEmbeddable == null && anyId != null) {
            AnyIdEmbeddable embeddable = new AnyIdEmbeddable();
            embeddable.setValue(String.valueOf(anyId.getValue()));
            embeddable.setClassName(anyId.getBaseIdType().getName());
            anyIdEmbeddable = embeddable;
        }
        return anyIdEmbeddable;
    }

    public void setAnyIdEmbeddable(AnyIdEmbeddable anyIdEmbeddable) {
        this.anyIdEmbeddable = anyIdEmbeddable;
        if (anyIdEmbeddable == null || anyIdEmbeddable.getValue() == null || anyIdEmbeddable.getClassName() == null) {
            this.anyId = null;
            return;
        }
        try {
            Class<?> bubbleIdClass = Class.forName(anyIdEmbeddable.getClassName());
            if (!BubbleId.class.isAssignableFrom(bubbleIdClass)) {
                throw new IllegalArgumentException("Not a BubbleId class: " + anyIdEmbeddable.getClassName());
            }
            @SuppressWarnings("unchecked")
            Class<? extends BubbleId> typedClass = (Class<? extends BubbleId>) bubbleIdClass;
            Object value = resolveAnyIdValue(anyIdEmbeddable.getValue(), typedClass);
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            this.anyId = BubbleIds.createInstance(typedClass, value, snapshotVersion);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown BubbleId class: " + anyIdEmbeddable.getClassName(), e);
        }
    }

    public SomeIdentEmbeddable getSomeIdentEmbeddable() {
        if (someIdentEmbeddable == null && someIdent != null) {
            SomeIdentEmbeddable embeddable = new SomeIdentEmbeddable();
            embeddable.setValue(someIdent.toConcatinatedFields().getValue());
            embeddable.setClassName(someIdent.getClass().getName());
            someIdentEmbeddable = embeddable;
        }
        return someIdentEmbeddable;
    }

    public void setSomeIdentEmbeddable(SomeIdentEmbeddable someIdentEmbeddable) {
        this.someIdentEmbeddable = someIdentEmbeddable;
        if (someIdentEmbeddable == null || someIdentEmbeddable.getValue() == null || someIdentEmbeddable.getClassName() == null) {
            this.someIdent = null;
            return;
        }
        this.someIdent = (SomeIdent) ConcatenatedFields.createObject(
            someIdentEmbeddable.getClassName(),
            new ConcatenatedFields(someIdentEmbeddable.getValue())
        );
    }

    public static class AnyIdEmbeddable implements Serializable {
        private static final long serialVersionUID = 1L;

        private String value;
        private String className;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    public static class SomeIdentEmbeddable implements Serializable {
        private static final long serialVersionUID = 1L;

        private String value;
        private String className;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

    private Object resolveAnyIdValue(String value, Class<? extends BubbleId> idClass) {
        Class<?> valueType = BubbleIds.getValueType(idClass);
        if (valueType == Long.class) {
            return Long.valueOf(value);
        }
        if (valueType == String.class) {
            return value;
        }
        return value;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        getAnyIdEmbeddable();
        getSomeIdentEmbeddable();
        stream.defaultWriteObject();
    }
}
