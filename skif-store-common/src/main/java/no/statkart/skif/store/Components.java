package no.statkart.skif.store;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Hjelpeklasser for standardisert implementasjon av Component funksjonalitet for owner håndtering. Components bør delegere til disse metoder fremfor
 * å implementere tilsvarende logikk selv.
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class Components {
    /**
     * Hjelpemetode som sikre at komponent blir sjekket og satt riktig påeiende objekt.
     * <p/>
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
        if (thisComponent != null && component==null) {
            // Komponent fjernes fra owner
            thisComponent.setOwner(null);
        } else if (thisComponent == component) {
            // begge er null eller like, det er greit
        } else {
            if (thisComponent != null) {
                if (isNullComponent(thisComponent) || thisComponent instanceof EntityComponent) {
                    thisComponent.setOwner(null);
                } else {
                    throw new IllegalStateException(String.format("Owner already has a component: owner=%s component=%s", thisComponent.getOwner(), component));
                }
            }
            // Owner har ingen komponent og får en ny component (som ikke kan ha owner satt, men kan gjerne ha id fra før)
            if (component.getOwner() != null) {
                throw new IllegalStateException("Attempt to assign component to a new owner: component=" + component);
            }
        }
        setOwner(component, owner);
        return component;
    }

    static <O, C extends ComponentWithOwnerReference<O>> void setOwner(@Nullable C newComponent, O owner) {
        if (newComponent != null) {
            newComponent.setOwner(owner);
        }
    }

    /**
     * Hjelpemetode som sikre at tilbakepeker til owner blir sjekket og satt riktig for komponenter
     * <p/>
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


    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(CompositeComponent<O,?> owner) {
        return new CompositeComponentSet<O, E>(owner, Sets.<E>newHashSet());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentSet<O, E> newSet(O owner) {
        return new ComponentSet<O, E>(owner, Sets.<E>newHashSet());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(CompositeComponent<O, ?> owner) {
        return new CompositeComponentList<O, E>(owner, Lists.<E>newArrayList());
    }

    static public <O, E extends ComponentWithOwnerReference<O>> AbstractComponentList<O, E> newList(O owner) {
        return new ComponentList<O, E>(owner, Lists.<E>newArrayList());
    }

    static public <E extends Component> void setFrom(Collection<E> collection, Set<E> newElements) {
        collection.clear();
        collection.addAll(newElements);
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> void setDelegate(Set<E> componentSet, Set<E> newElements) {
        ((ComponentSet)componentSet).setDelegate(newElements);
    }

    static public <E extends ComponentWithOwnerReference<?>> Set<E> getDelegate(Set<E> componentSet) {
        return ((ComponentSet)componentSet).delegate();
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> void setDelegate(List<E> componentList, List<E> newElements) {
        ((ComponentList)componentList).setDelegate(newElements);
    }

    @SuppressWarnings("unchecked")
    static public <E extends ComponentWithOwnerReference<?>> List<E> getDelegate(List<E> componentList) {
        return ((ComponentList)componentList).delegate();
    }
}
