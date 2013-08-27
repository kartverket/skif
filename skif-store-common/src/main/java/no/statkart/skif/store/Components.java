package no.statkart.skif.store;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import javax.inject.Provider;

/**
 * Hjelpeklasser for CompositeComponent med metoder som hjelper til med å implementere sjekk for setOwner() slik at
 * komponenter ikke kan deles.
 *
 * @author Henrik Fredholm
 * @since 2.3
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
        if (thisComponent != null && component == null) {
            // Komponent fjernes fra owner
            thisComponent.setOwner(null);
        } else if (thisComponent == component) {
            // begge er null eller like, det er greit
        } else if (thisComponent == null || isNullComponent(thisComponent)) {
            // Owner har ingen komponent og får en ny component (som ikke kan ha annen owner eller ha eksisterende id)
            if (component.getOwner() != null) {
                throw new IllegalStateException("Attempt to assign component to a new owner: component=" + component);
            }
            if (component instanceof EntityComponent && ((EntityComponent) component).getId() != null) {
                throw new IllegalStateException("Attempt to assign component to a new owner: component=" + component);
            }
        } else {
            throw new IllegalStateException(String.format("Owner already has a component: owner=%s component=%s", thisComponent.getOwner(), component));
        }
        return component;
    }

    public static <C extends Component> C checkSetComponentWithoutOwner(@Nullable C thisComponent, @Nullable C component) {
        Preconditions.checkState(thisComponent == null || thisComponent == component || checkNullComponent(thisComponent));
        return component;
    }

    public static <O, C extends ComponentWithOwnerReference<O>> void setOwner(@Nullable C newComponent, O owner) {
        if (newComponent != null) {
            newComponent.setOwner(owner);
        }
    }

    private static <C> boolean isNullComponent(C thisComponent) {
        return (thisComponent instanceof CompositeComponentWithCollections) && ((CompositeComponentWithCollections) thisComponent).isNullComponent();
    }

    private static <C> boolean checkNullComponent(C thisComponent) {
        return (thisComponent instanceof CompositeComponentWithCollections) && ((CompositeComponentWithCollections) thisComponent).isNullComponent();
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
     *               new Function<BubbleWithCompositeComponent, Level1CompositeComponent>() {
     *                   public Level1CompositeComponent apply(BubbleWithCompositeComponent owner) {return owner.getLevel1Component();}
     *               }
     *       );
     *  }
     * </pre>
     */
    @Nullable
    public static <O, C> O checkSetOwner(C component, O currentComponentOwner, O newComponentOwner, Function<O, C> componentOfNewOwner) {
        Preconditions.checkState(currentComponentOwner == null || currentComponentOwner == newComponentOwner || newComponentOwner == null, "Component already has another owner: %s", component);
        Preconditions.checkState(newComponentOwner == null || componentOfNewOwner.apply(newComponentOwner) == component, "New owner does not point to component: owner=%s component=%s", newComponentOwner, component);
        return newComponentOwner;
    }
}
