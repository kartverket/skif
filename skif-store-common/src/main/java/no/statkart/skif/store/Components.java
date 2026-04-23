package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import no.statkart.skif.store.relation.cache.RelationName;
import no.statkart.skif.store.relation.cache.StoreRelationCache;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Hjelpeklasser for standardisert implementasjon av Component funksjonalitet for owner håndtering. Components bør delegere til disse metoder fremfor
 * å implementere tilsvarende logikk selv.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
@SuppressWarnings("WeakerAccess")
public class Components {
    /**
     * Hjelpemetode som sikre at komponent blir sjekket og satt riktig påeiende objekt.
     * <p>
     * <P>Eksempel på implementasjon i eiende objekt:
     * <pre>
     *    public void setLevel1Component(Level1CompositeComponent level1Component) {
     *        this.level1Component = Components.checkSetComponentWithOwner(this.level1Component, level1Component);
     *        Components.setOwner(this.level1Component, this);
     *    }
     * </pre>
     */
    @Nullable
    public static <O, C extends ComponentWithOwnerReference<O>> C checkSetComponent(O owner, @Nullable C thisComponent, @Nullable C component) {
        if (thisComponent != null && component == null) {
            // Komponent fjernes fra owner
            thisComponent.setOwner(null);
        } else if (thisComponent != component) {
            if (thisComponent != null) {
                // TODO: SKIF-565. Midlertidig endret. Uklart hvorfor EntityComponent kan byttes ut, men ikke ComposisteBubbleComponent.
                // Burde egentlig litt motsatt. Hvis id-ene er forskjellige kan det bli orphan objekter
                // (med mindre det håndteres våres hibernate forbedringer)
                thisComponent.setOwner(null);
//                if (isNullComponent(thisComponent) || thisComponent instanceof EntityComponent) {
//                    thisComponent.setOwner(null);
//                } else {
//                    throw new IllegalStateException(String.format("Owner already has a component: owner=%s component=%s", thisComponent.getOwner(), component));
//                }
            }
            // Owner får en ny component (som ikke kan ha owner satt, men kan gjerne ha id fra før)
            if (component.getOwner() != null) {
                throw new IllegalStateException("Attempt to assign component to a new owner: component=" + component);
            }
        }
        setOwner(component, owner);
        return component;
    }

    static <O, C extends ComponentWithOwnerReference<O>> void setOwner(@Nullable C component, O owner) {
        if (component != null) {
            if (component.getOwner() != owner) {
                // Component kan ikke bytte eier, men kan settes til null ved sletting
                if (component instanceof InverseRelationParticipation) {
                    // originalOwningBubble vil være null ved 'add' og være satt ved 'remove'
                    BubbleObject originalOwningBubble = getOwningBubble(component);
                    StoreRelationCache relationCacheOriginalBubble = Bubbles.getRelationCacheIfBubbleAttachedToStoreAndCacheEnabledOtherwiseNull(originalOwningBubble);
                    if (relationCacheOriginalBubble != null) {
                        relationCacheOriginalBubble.updateRemoved(originalOwningBubble.getBubbleId(), (InverseRelationParticipation) component);
                    }
                    component.setOwner(owner);
                    // originalOwningBubble vil være satt ved 'add' og være null ved 'remove'
                    BubbleObject owningBubble = getOwningBubble(component);
                    StoreRelationCache relationCacheNewBubble = Bubbles.getRelationCacheIfBubbleAttachedToStoreAndCacheEnabledOtherwiseNull(owningBubble);
                    if (relationCacheNewBubble != null) {
                        relationCacheNewBubble.updateAdded(owningBubble.getBubbleId(), (InverseRelationParticipation) component);
                    }
                } else {
                    component.setOwner(owner);
                }
            }
        }
    }

    /**
     * Hjelpemetode som sikre at tilbakepeker til owner blir sjekket og satt riktig for komponenter
     * <p>
     * <P>Eksempel på bruk:
     * <pre>
     *    public void setOwner(BubbleWithCompositeComponent owner) {
     *       this.owner = Components.checkSetOwner(this, this.owner, owner);
     *  }
     * </pre>
     */
    @Nullable
    public static <O, C extends ComponentWithOwnerReference<O>> O checkSetOwner(C component, O currentComponentOwner, O newComponentOwner) {
        Preconditions.checkState(currentComponentOwner == null || currentComponentOwner == newComponentOwner || newComponentOwner == null, "Component already has another owner: %s", component);
        return newComponentOwner;
    }


    /**
     * Returnere true hvis {@code component} er null eller hvis componenten er en CompositeComponentWithCollections og {@code component.isNullComponent()} er true
     */
    public static boolean isNullComponent(Object component) {
        return component == null || (component instanceof CompositeComponentWithCollections) && ((CompositeComponentWithCollections) component).isNullComponent();
    }

    /**
     * Returnere true hvis {@code component} er null eller hvis componenten er en CompositeComponentWithCollections og {@code component.isNullComponent()} er true
     */
    public static boolean isNullComponent(CompositeComponentWithCollections thisComponent) {
        return thisComponent == null || thisComponent.isNullComponent();
    }


    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(CompositeComponent<O, ?> owner) {
        return newSet(owner, Sets.<E>newHashSet());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(CompositeComponent<O, ?> owner, Set<E> set) {
        return new CompositeComponentSet<>(owner, set);
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(O owner) {
        return newSet(owner, Sets.<E>newHashSet());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(O owner, Set<E> set) {
        return new ComponentSet<>(owner, set);
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(CompositeComponent<O, ?> owner) {
        return newList(owner, Lists.<E>newArrayList());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(CompositeComponent<O, ?> owner, List<E> list) {
        return new CompositeComponentList<>(owner, list);
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(O owner) {
        return newList(owner, Lists.<E>newArrayList());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(O owner, List<E> list) {
        return new ComponentList<>(owner, list);
    }

    static public <E extends Component> void setFrom(Collection<E> collection, Collection<E> newElements) {
        // Må ta en kopi av newElements, i tilfelle newElements er den samme som collection eller en form for wrapper for den.
        ImmutableList<E> copy = ImmutableList.copyOf(newElements);
        collection.clear();
        collection.addAll(copy);
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> void setDelegate(Set<E> componentSet, Set<E> newElements) {
        ((AbstractComponentSet<?, E>) componentSet).setDelegate(newElements);
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> Set<E> getDelegate(Set<E> componentSet) {
        return ((AbstractComponentSet<?, E>) componentSet).delegate();
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> void setDelegate(List<E> componentList, List<E> newElements) {
        ((AbstractComponentList<?, E>) componentList).setDelegate(newElements);
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> List<E> getDelegate(List<E> componentList) {
        return ((AbstractComponentList<?, E>) componentList).delegate();
    }

    public static BubbleObject getOwningBubble(ComponentWithOwnerReference<?> component) {
        Object result = component;
        do {
            result = ((ComponentWithOwnerReference<?>) result).getOwner();
            if (result == null) return null;
        } while (result instanceof ComponentWithOwnerReference<?>);
        return ((BubbleObject) result);
    }

    public static BubbleObject getOwningBubbleNullSafe(ComponentWithOwnerReference<?> component) {
        return requireNonNull(getOwningBubble(component));
    }

    public static <E> E onChangeRelation(ComponentWithOwnerReference<?> component, RelationName relationName, E oldValue, E newValue) {
        BubbleObject owningBubble = Components.getOwningBubble(component);
        Bubbles.onChangeRelationImpl(owningBubble, relationName, oldValue, newValue);
        return newValue;
    }


}
