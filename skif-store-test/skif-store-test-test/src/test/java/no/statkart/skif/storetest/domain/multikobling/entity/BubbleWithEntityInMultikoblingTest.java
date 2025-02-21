package no.statkart.skif.storetest.domain.multikobling.entity;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Tester {@link no.statkart.skif.store.multikobling.Multikobling} hvor element er en entity component definert ved
 * {@link BubbleWithEntityInMultikobling}. Se {@link no.statkart.skif.storetest.domain.multikobling.MultikoblingTest}
 * for mer grunnleggede tester av {@code Multikobling}.
 * <p>
 * NB: Denne klassen er avhengig av at hibernate-mappingen bruker en custom collection-type for koblingen.
 *
 * @author Henrik Fredholm
 * @since 2.8.0
 */
@Test
public class BubbleWithEntityInMultikoblingTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    /**
     * To entities er like hvis de har samme id.
     */
    public void testEntityEquals() {
        EntityInMultikobling entity1 = new EntityInMultikobling("Tekst A").withId(10);
        EntityInMultikobling entity2 = new EntityInMultikobling("Tekst A").withId(10);
        EntityInMultikobling entity3 = new EntityInMultikobling("Tekst A").withId(11);
        EntityInMultikobling entity4 = new EntityInMultikobling("Tekst A");
        EntityInMultikobling entity5 = new EntityInMultikobling("Tekst B").withId(10);
        Assert.assertEquals(entity1,entity2, "Entiteter med samme id skal være like");
        Assert.assertNotEquals(entity1, entity3, "Entiteter med forskjellig id skal være forskjellige");
        Assert.assertNotEquals(entity1, entity4, "Entiteter med forskjellig id skal være forskjellige, også når den ene id er null");
        Assert.assertEquals(entity1, entity5, "Entiteter med samme id skal være like");

        EntityInMultikobling entityWithPsudoId1 = new EntityInMultikobling("Tekst A");
        EntityInMultikobling entityWithPsudoId2 = new EntityInMultikobling("Tekst A");
        Assert.assertNotEquals(entityWithPsudoId1,entityWithPsudoId2, "Entities som ikke har fått tildelt pesudoId skal være like");

        Set<EntityInMultikobling> entitesWithPseudoIds = new HashSet<>(ImmutableSet.of(entityWithPsudoId1, entityWithPsudoId2));
        Assert.assertNotEquals(entityWithPsudoId1, entityWithPsudoId2, "Entities med id=null som har fått tildelt pesuodoId skal være forskjellige");
        Assert.assertNotEquals(entity1,entityWithPsudoId1);
        Assert.assertEquals(entitesWithPseudoIds.size(), 2);
        entitesWithPseudoIds.remove(entityWithPsudoId2);
        Assert.assertEquals(entitesWithPseudoIds.size(), 1);
    }

    /**
     * To koblingselementer er like hvis entiteten de wrapper er like uavhengig av rolle
     */
    public void testKoblingEquals() {
        EntityInMultikobling entity1 = new EntityInMultikobling("Tekst A").withId(10);
        EntityInMultikobling entity2 = new EntityInMultikobling("Tekst A").withId(10);
        EntityInMultikobling entity3 = new EntityInMultikobling("Tekst A").withId(11);
        EntityInMultikobling entity4 = new EntityInMultikobling("Tekst A"); // Id er null
        EntityInMultikobling entity5 = new EntityInMultikobling("Tekst B").withId(10);
        EntityInMultikoblingKobling kobling1a = new EntityInMultikoblingKobling("A", entity1);
        EntityInMultikoblingKobling kobling2a = new EntityInMultikoblingKobling("A", entity2);
        EntityInMultikoblingKobling kobling3a = new EntityInMultikoblingKobling("A", entity3);
        EntityInMultikoblingKobling kobling4a = new EntityInMultikoblingKobling("A", entity4);
        EntityInMultikoblingKobling kobling5a = new EntityInMultikoblingKobling("A", entity5);
        EntityInMultikoblingKobling kobling1b = new EntityInMultikoblingKobling("B", entity1);

        Assert.assertEquals(kobling1a, kobling2a, "Koblinger for entiteter med samme id skal være like");
        Assert.assertNotEquals(kobling1a, kobling3a, "Koblinger for entiteter med forskjellig id skal være forskjellige");
        Assert.assertNotEquals(kobling1a, kobling4a, "Koblinger for entiteter med forskjellig id eller null-id skal være forskjellige");
        Assert.assertEquals(kobling1a, kobling5a, "Koblinger for entiteter med samme id skal være like uavhengig av andre felter");
        Assert.assertEquals(kobling1a, kobling1b, "Koblinger for entiteter med samme id skal være like uavhengig av rolle");

        EntityInMultikobling entityWithPsudoId1 = new EntityInMultikobling("Tekst A");
        EntityInMultikobling entityWithPsudoId2 = new EntityInMultikobling("Tekst A");
        EntityInMultikoblingKobling koblingPsudo1a = new EntityInMultikoblingKobling("B", entityWithPsudoId1);
        EntityInMultikoblingKobling koblingPsudo1b = new EntityInMultikoblingKobling("B", entityWithPsudoId1);
        EntityInMultikoblingKobling koblingPsudo2a = new EntityInMultikoblingKobling("A", entityWithPsudoId2);

        // Legg inn entites i Set så de får tildelt pesudoId
        ImmutableSet.of(entityWithPsudoId1, entityWithPsudoId2);
        Assert.assertNotEquals(kobling1a, koblingPsudo1a, "Koblinger for entiteter med id=null som har fått tildelt pesuodoId skal være forskjellige");
        Assert.assertEquals(koblingPsudo1a, koblingPsudo1b, "Koblinger for samme entitet med id=null som har fått tildelt pesuodoId skal være like uavhengig av rolle");
        Assert.assertNotEquals(koblingPsudo1a, koblingPsudo2a, "Koblinger for entiteter med id=null som har fått tildelt pesuodoId skal være forskjellige");
    }


    @Test(groups = "singlevm-required")
    public void testPersistens() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();

        final BubbleWithEntityInMultikoblingId<?> bubbleId = mockupFacade.getIdService().getNextId(BubbleWithEntityInMultikoblingId.class);

        RunOnServerWithTxRequiresNewService runOnServerService = injector.getInstance(RunOnServerWithTxRequiresNewService.class);

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = new BubbleWithEntityInMultikobling();
                bubble.setId(bubbleId);

                Set<EntityInMultikobling> over = bubble.getEntities("Over");
                over.add(new EntityInMultikobling("Tekst 1"));
                bubble.getEntities("Over").add(new EntityInMultikobling("Tekst 2"));

                store.insert(bubble);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = store.lock(bubbleId);

                HashSet<Long> uniqueIds = new HashSet<>();
                Assert.assertEquals(bubble.getEntities("Over").size(), 2, "Feil antall 'over'");
                for (Iterator<EntityInMultikobling> iterator = bubble.getEntities("Over").iterator(); iterator.hasNext(); ) {
                    EntityInMultikobling next = iterator.next();
                    Assert.assertNotNull(next.getId(), "Entitet har ikke fått bubbleId");
                    Assert.assertTrue(uniqueIds.add(next.getId()), "Entitet har ikke fått unique bubbleId");
                }

                return null;
            }
        });

        final Object movedEntityId = runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = store.lock(bubbleId);
                EntityInMultikobling entryToChange = null;
                Assert.assertEquals(bubble.getEntities("Over").size(), 2, "Feil antall 'over'");
                for (Iterator<EntityInMultikobling> iterator = bubble.getEntities("Over").iterator(); iterator.hasNext(); ) {
                    EntityInMultikobling next = iterator.next();
                    if (next.getTekst().equals("Tekst 1")) {
                        entryToChange = next;
                        iterator.remove();
                        bubble.getEntities("Under").add(entryToChange);
                        break;
                    }
                }
                Assert.assertEquals(bubble.getEntities("Over").size(), 1, "Feil antall 'over'");
                Assert.assertEquals(bubble.getEntities("Under").size(), 1, "Feil antall 'under'");
                store.update(bubble);

                return entryToChange.getId();
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private StoreServer store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = store.lock(bubbleId);
                Assert.assertEquals(bubble.getEntities("Over").size(), 1, "Feil antall 'over'");
                Assert.assertEquals(bubble.getEntities("Under").size(), 1, "Feil antall 'under'");
                EntityInMultikobling entity = bubble.getEntities("Under").iterator().next();
                Assert.assertEquals(entity.getId(), movedEntityId);
                bubble.getEntities("Under").remove(entity);
                // En flush her vil føre til Hibernate exception, så ikke utfør flush mellom remove- og add operasjoner
                //store.flush();
                bubble.getEntities("Over").add(entity);
                store.update(bubble);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = store.lock(bubbleId);

                Assert.assertEquals(bubble.getEntities("Over").size(), 2, "Feil antall 'over'");
                for (Iterator<EntityInMultikobling> iterator = bubble.getEntities("Over").iterator(); iterator.hasNext(); ) {
                    EntityInMultikobling next = iterator.next();
                    if (next.getTekst().equals("Tekst 1")) {
                        iterator.remove();
                    }
                }
                store.update(bubble);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                BubbleWithEntityInMultikobling bubble = store.lock(bubbleId);

                Assert.assertEquals(bubble.getEntities("Over").size(), 1, "Feil antall 'over'");

                Assert.assertEquals(bubble.getEntities("Over").size(), 1, "Feil antall 'over'");
                Assert.assertEquals(bubble.getEntities("Over").iterator().next().getTekst(), "Tekst 2", "Feil verdi");

                return null;
            }
        });
    }

    public void testAddAndReset() {
        BubbleWithEntityInMultikobling bubble = new BubbleWithEntityInMultikobling();

        bubble.getEntities("A").add(new EntityInMultikobling("Tekst A").withId(10));
        Assert.assertEquals(bubble.getEntities("A").size(), 1);
        Assert.assertEquals(bubble.getKoblinger().size(), 1);
        Assert.assertEquals(bubble.getKoblinger().iterator().next(), new EntityInMultikoblingKobling("A", new EntityInMultikobling("Tekst A").withId(10).withOwner(bubble)));

        bubble.setKoblinger(new HashSet<EntityInMultikoblingKobling>());
        Assert.assertEquals(bubble.getKoblinger().size(), 0);
        Assert.assertEquals(bubble.getEntities("A").size(), 0);
    }

    public void testAddTwoAndClearOne() {
        BubbleWithEntityInMultikobling bubble = new BubbleWithEntityInMultikobling();

        bubble.getEntities("A").add(new EntityInMultikobling("Tekst A").withId(10));
        bubble.getEntities("B").add(new EntityInMultikobling("Tekst B").withId(11));
        Assert.assertEquals(bubble.getEntities("A").size(), 1);
        Assert.assertEquals(bubble.getEntities("B").size(), 1);
        Assert.assertEquals(bubble.getKoblinger().size(), 2);
        Assert.assertEquals(bubble.getKoblinger(), ImmutableSet.of(
                new EntityInMultikoblingKobling("A", new EntityInMultikobling("Tekst A").withId(10).withOwner(bubble)),
                new EntityInMultikoblingKobling("B", new EntityInMultikobling("Tekst B").withId(11).withOwner(bubble))));

        bubble.getEntities("A").clear();
        Assert.assertEquals(bubble.getEntities("A").size(), 0);
        Assert.assertEquals(bubble.getEntities("B").size(), 1);
        Assert.assertEquals(bubble.getKoblinger().size(), 1);
        Assert.assertEquals(bubble.getKoblinger(), ImmutableSet.of(new EntityInMultikoblingKobling("B", new EntityInMultikobling("Tekst B").withId(11).withOwner(bubble))));
    }
}
