package no.statkart.skif.store;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.inject.Provider;
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
     * <P>Eksempel på bruk:
     * <pre>
     *    public void setLevel1Component(Level1CompositeComponent level1Component) {
     *        this.level1Component = Components.checkSetComponentWithOwner(this.level1Component, level1Component);
     *        Components.setOwner(this.level1Component, this);
     *    }
     * </pre>
     */
    @Nullable
    public static <C extends ComponentWithOwnerReference> C checkSetComponentWithOwner(@Nullable C thisComponent, @Nullable C component) {
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
        return component;
    }

    public static <O, C extends ComponentWithOwnerReference<O>> void setOwner(@Nullable C newComponent, O owner) {
        if (newComponent != null) {
            newComponent.setOwner(owner);
        }
    }

    public static boolean isNullComponent(Object thisComponent) {
        return thisComponent == null || (thisComponent instanceof CompositeComponentWithCollections) && ((CompositeComponentWithCollections) thisComponent).isNullComponent();
    }

    public static boolean isNullComponent(CompositeComponentWithCollections thisComponent) {
        return thisComponent == null || thisComponent.isNullComponent();
    }

    /**
     * Hjelpemetode som sikre at tilbakepeker til owner blir sjekket og satt riktig for komponenter
     * <p/>
     * <P>Eksempel på bruk:
     * <pre>
     *    public void setOwner(BubbleWithCompositeComponent owner) {
     *       this.owner = Components.checkSetOwner(
     *               this,
     *               this.owner,
     *               owner,
     *               new OwnerCheck<BubbleWithCompositeComponent, Level1CompositeComponent>() {
     *                   public boolean apply(BubbleWithCompositeComponent owner, Level1CompositeComponent child) {
     *                      return owner.getLevel1Component()==child;
     *                   }
     *               }
     *       );
     *  }
     * </pre>
     */
    @Nullable
    public static <O, C extends ComponentWithOwnerReference<O>> O checkSetOwner(C component, O currentComponentOwner, O newComponentOwner, OwnerCheck<O, C> ownerCheck) {
        Preconditions.checkState(currentComponentOwner == null || currentComponentOwner == newComponentOwner || newComponentOwner == null, "Component already has another owner: %s", component);
        Preconditions.checkState(newComponentOwner == null || ownerCheck.apply(newComponentOwner, component), "New owner does not point to component: owner=%s component=%s", newComponentOwner, component);
        return newComponentOwner;
    }

    static public <E extends Component> void setFrom(Collection<E> collection, Set<E> newElements) {
        collection.clear();
        collection.addAll(newElements);
    }

    static public <O, E extends ComponentWithOwnerReference<O>> void setFrom(O owner, Collection<E> collection, Set<E> newElements) {
        for (E e : collection) {
            e.setOwner(null);
        }
        collection.clear();
        for (E newElement : newElements) {
            if (collection.add(newElement)) {
                newElement.setOwner(owner);
            }
        }
    }

    static public <O, E extends ComponentWithOwnerReference<O>> Set<E> get(O owner, Set<E> internalSet) {
        return new ComponentSet<O, E>(owner, internalSet);
    }

    static public <O, E extends ComponentWithOwnerReference<O>> List<E> get(O owner, List<E> internalSet) {
        return new ComponentList<O, E>(owner, internalSet);
    }
}
