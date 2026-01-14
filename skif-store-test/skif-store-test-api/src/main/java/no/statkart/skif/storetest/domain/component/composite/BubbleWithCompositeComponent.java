package no.statkart.skif.storetest.domain.component.composite;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Components;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public class BubbleWithCompositeComponent extends AbstractStoreTestBubble {
    private static final long serialVersionUID = 1L;
    private static final Field RAW_ID_FIELD = resolveRawIdField();

    private int nr;
    private String text;
    private Level1CompositeComponent level1Component;

    public BubbleWithCompositeComponent() {
    }

    public BubbleWithCompositeComponent(BubbleWithCompositeComponentId<?> id) {
        super(id);
    }

    public BubbleWithCompositeComponent(BubbleWithCompositeComponentId id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public BubbleWithCompositeComponentId<?> getId() {
        try {
            Object rawId = null;
            if (RAW_ID_FIELD != null) {
                try {
                    rawId = RAW_ID_FIELD.get(this);
                } catch (Throwable ignored) {
                    rawId = null;
                }
            }
            if (rawId == null) {
                rawId = super.getBubbleId();
            }
            return coerceId(rawId);
        } catch (Throwable e) {
            return null;
        }
    }

    private static Field resolveRawIdField() {
        try {
            Field field = AbstractBubbleObject.class.getDeclaredField("id");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    private BubbleWithCompositeComponentId<?> coerceIdFromRawField() {
        if (RAW_ID_FIELD == null) {
            return null;
        }
        try {
            return coerceId(RAW_ID_FIELD.get(this));
        } catch (Throwable e) {
            return null;
        }
    }

    private BubbleWithCompositeComponentId<?> coerceId(Object rawId) {
        if (rawId == null) {
            return null;
        }
        if (rawId instanceof BubbleWithCompositeComponentId) {
            return (BubbleWithCompositeComponentId<?>) rawId;
        }
        if (rawId instanceof BubbleId) {
            BubbleId<?> bubbleId = (BubbleId<?>) rawId;
            return coerceIdValue(bubbleId.getValue(), bubbleId.getSnapshotVersion());
        }
        return coerceIdValue(rawId, SnapshotVersionContext.getInstance().getSnapshotVersion());
    }

    private BubbleWithCompositeComponentId<?> coerceIdValue(Object value, SnapshotVersion snapshotVersion) {
        try {
            Long longValue;
            if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            } else {
                longValue = Long.valueOf(String.valueOf(value));
            }
            return new BubbleWithCompositeComponentId<>(longValue, snapshotVersion);
        } catch (Throwable e) {
            return null;
        }
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Nullable
    public Level1CompositeComponent getLevel1Component() {
        return level1Component;
    }

    public void setLevel1Component(Level1CompositeComponent level1Component) {
        this.level1Component = Components.checkSetComponent(this, this.level1Component, level1Component);
    }
}
