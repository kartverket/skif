package no.statkart.skif.storetest.persistence;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.store.AbstractEntityComponent;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.config.StoreTestServerModule;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponents;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponentsComponent;
import no.statkart.skif.storetest.domain.demo.BubbleWithComponentsId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * Tester identitet for komponenter basert på {@link no.statkart.skif.store.AbstractEntityComponent}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Deprecated // Skrives om til å bruke andre objekter og mockupfactory
public class EntityComponentTest extends SkifServerTestCase {

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

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
        IdService idService = mockupFacadeFactory.getWriteMockupFacade().getIdService();

        BubbleWithComponents bubbleWithComponents = new BubbleWithComponents();
        bubbleWithComponents.setId(idService.getNextId(BubbleWithComponentsId.class));

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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }
}
