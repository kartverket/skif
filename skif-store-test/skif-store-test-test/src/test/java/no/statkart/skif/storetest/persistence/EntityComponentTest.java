package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractEntityComponent;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponents;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponentsComponent;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponentsId;
import no.statkart.skif.storetest.mockup.MockupFacade;
import no.statkart.skif.storetest.mockup.MockupFacadeFactory;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * Tester identitet for komponenter basert på {@link no.statkart.skif.store.AbstractEntityComponent}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test
public class EntityComponentTest extends SkifServerTestCase {
    @Inject
    private MockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreServer store;

    public EntityComponentTest() {
        super(StoreTestServerModule.class);
    }

    public void pseudoId() {
        BubbleWithComponentsComponent component = new BubbleWithComponentsComponent();

        Assert.assertEquals(component.hashCode(), getPseudoId(component).hashCode(), "Hashcode er ikke fra pseudo-id");

        BubbleWithComponentsComponent copy = CopyHelper.copy(component);

        Assert.assertEquals(getPseudoId(copy), getPseudoId(component), "Pseudo-id ikke bevart ved kopiering");
    }

    public void hashCodePersistentAcrossPersist() {
        final MockupFacade mockupFacade = mockupFacadeFactory.getForWriteTest();

        BubbleWithComponents bubbleWithComponents = new BubbleWithComponents();
        bubbleWithComponents.setId(mockupFacade.getIdService().getNextId(BubbleWithComponentsId.class));

        final BubbleWithComponentsComponent component = new BubbleWithComponentsComponent();
        bubbleWithComponents.setComponents(Collections.singleton(component));

        Assert.assertNull(component.getId(), "Component id ikke null");

        final int initialHashCode = component.hashCode();
        final Long initialPseudoId = getPseudoId(component);

        store.beginTransaction();
        store.insert(bubbleWithComponents);
        store.commitTransaction();

        Assert.assertNotNull(component.getId(), "Component id er null");
        Assert.assertEquals(getPseudoId(component), initialPseudoId, "PseudoId har endret seg");
        Assert.assertEquals(component.hashCode(), initialHashCode, "Hashcode har endret seg");
        Assert.assertNotSame(getPseudoId(component), component.getId(), "Id er pseudo-id");

        // Få Store og Hibernate til å glemme objektet i minnet, og lese det opp fra databasen på nytt
        store.evict(bubbleWithComponents.getId());

        store.beginTransaction();
        BubbleWithComponents persistedBubble = store.get(bubbleWithComponents.getId());
        store.commitTransaction();

        BubbleWithComponentsComponent persistedComponent = persistedBubble.getComponents().iterator().next();

        Assert.assertEquals(persistedComponent.getId(), component.getId(), "Id-ene er forskjellig etter opplesing");
        Assert.assertEquals(getPseudoId(persistedComponent), persistedComponent.getId(), "Id er ikke pseudo-id");
        Assert.assertFalse(component.equals(persistedComponent), "Nytt objekt er ikke foventet å være likt det samme objektet i neste transaksjon");
    }

    private static Long getPseudoId(AbstractEntityComponent component) {
        try {
            Method getPseudoId = AbstractEntityComponent.class.getDeclaredMethod("getPseudoId");
            getPseudoId.setAccessible(true);
            return (Long) getPseudoId.invoke(component);
        } catch (NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }
}
