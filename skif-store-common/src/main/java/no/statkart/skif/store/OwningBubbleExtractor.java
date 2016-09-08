package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Hjelpeklasse for transparent å hente ut eiende boble for et objekt som enten kan være en boble eller en
 * {@code ComponentWithOwnerReference}.
 *
 * @since 2.8.0
 */
public abstract class OwningBubbleExtractor implements Serializable {
    private static final long serialVersionUID = 1L;
    public abstract BubbleObject getOwner();

    protected static class OwningBubbleExtractorForBubble extends OwningBubbleExtractor {
        private static final long serialVersionUID = 1L;
        private final BubbleObject owner;

        public OwningBubbleExtractorForBubble(BubbleObject owner) {
            this.owner = owner;
        }

        @Override
        public BubbleObject getOwner() {
            return owner;
        }
    }

    protected static class OwningBubbleExtractorForComponent extends OwningBubbleExtractor {
        private static final long serialVersionUID = 1L;
        final ComponentWithOwnerReference<?> component;

        public OwningBubbleExtractorForComponent(ComponentWithOwnerReference<?> component) {
            this.component = component;
        }

        @Override
        public BubbleObject getOwner() {
            return Components.getOwningBubble(component);
        }
    }

    public static OwningBubbleExtractor create(final BubbleObject owner) {
        return new OwningBubbleExtractorForBubble(owner);
    }

    public static OwningBubbleExtractor create(final ComponentWithOwnerReference<?> component) {
        return new OwningBubbleExtractorForComponent(component);
    }
}
