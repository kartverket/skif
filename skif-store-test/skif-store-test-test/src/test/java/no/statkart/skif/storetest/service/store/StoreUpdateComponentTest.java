package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.HashSet;

/**
 * Test som tester at vi får lagt til en entitycomponent i en liste på et bobleobjekt uten at eksisterende elementer
 * i listen får nye versjoner.
 * <p/>
 * Se SKIF-210 og SKIF-214 for mer informasjon om problemet som testes her.
 *
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
@Test
public class StoreUpdateComponentTest extends StoreTestServerTestCase {

    @Inject
    Store store;

    public void leggTilEntryIComponentListe() {

        final long idToUse = Calendar.getInstance().getTimeInMillis();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);
                BubbleWithList bubbleWithList = store.get(bubbleId);
                store.evict(bubbleId);

                BubbleWithList bubbleWithListCopy = CopyHelper.copy(bubbleWithList);
                store.lock(bubbleWithListCopy.getId());

                BubbleWithListComponent e = new BubbleWithListComponent(idToUse, "dette er component nr 2", AEnumKodeId.KodeBId);
                e.setBubbleWithList(bubbleWithListCopy);
                bubbleWithListCopy.getComponents().add(e);

                store.update(bubbleWithListCopy);

                return null;
            }
        });

        BubbleWithList bubbleWithList = store.get(new BubbleWithListId<BubbleWithList>(2201L));
        for (BubbleWithListComponent bubbleWithListComponent : bubbleWithList.getComponents()) {
            Assert.assertEquals(bubbleWithListComponent.gettVersion(), 1);
        }

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);
                BubbleWithList bubbleWithList = store.get(bubbleId);
                store.evict(bubbleId);

                BubbleWithList bubbleWithListCopy = CopyHelper.copy(bubbleWithList);
                store.lock(bubbleWithListCopy.getId());

                BubbleWithListComponent toRemove = null;
                for (BubbleWithListComponent bubbleWithListComponent : bubbleWithListCopy.getComponents()) {
                    if (bubbleWithListComponent.getId() != 2202L) {
                        toRemove = bubbleWithListComponent;
                        break;
                    }
                }
                bubbleWithListCopy.getComponents().remove(toRemove);
                store.update(bubbleWithListCopy);

                return null;
            }
        });

    }

    /**
     *
     */
    public void testMapUtOgTilbake() {

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);
                BubbleWithList bubbleWithList = store.get(bubbleId);
                store.evict(bubbleId);

                BubbleWithList copy = new BubbleWithList(bubbleWithList);
                store.lock(copy.getId());

                store.update(copy);
                return null;
            }
        });

        BubbleWithList bubbleWithList = store.get(new BubbleWithListId<BubbleWithList>(2201L));
        for (BubbleWithListComponent bubbleWithListComponent : bubbleWithList.getComponents()) {
            Assert.assertEquals(bubbleWithListComponent.gettVersion(), 1);
        }
    }

    public void testUpdateUtenAtObjektErLastetIHibernate() {

        final long idToUse = Calendar.getInstance().getTimeInMillis();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);

                BubbleWithList copy = new BubbleWithList();
                copy.setId(bubbleId);
                copy.setComponents(new HashSet<BubbleWithListComponent>());
                BubbleWithListComponent e = new BubbleWithListComponent(2202L, "component for 2201", AEnumKodeId.KodeAId);
                e.setBubbleWithList(copy);
                copy.getComponents().add(e);
                BubbleWithListComponent e1 = new BubbleWithListComponent(idToUse, "dette er component nr 2", AEnumKodeId.KodeBId);
                e1.setBubbleWithList(copy);
                copy.getComponents().add(e1);
                store.lock(copy.getId());

                store.update(copy);
                return null;
            }
        });

        BubbleWithList bubbleWithList = store.get(new BubbleWithListId<BubbleWithList>(2201L));
        for (BubbleWithListComponent bubbleWithListComponent : bubbleWithList.getComponents()) {
            Assert.assertEquals(bubbleWithListComponent.gettVersion(), 1);
        }

        //Fjern nr 2 slik at vi kan kjøre tester på nytt
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);
                BubbleWithList bubbleWithList = store.get(bubbleId);
                store.evict(bubbleId);

                BubbleWithList bubbleWithListCopy = CopyHelper.copy(bubbleWithList);
                store.lock(bubbleWithListCopy.getId());

                BubbleWithListComponent toRemove = null;
                for (BubbleWithListComponent bubbleWithListComponent : bubbleWithListCopy.getComponents()) {
                    if (bubbleWithListComponent.getId() != 2202L) {
                        toRemove = bubbleWithListComponent;
                        break;
                    }
                }
                bubbleWithListCopy.getComponents().remove(toRemove);
                store.update(bubbleWithListCopy);

                return null;
            }
        });
    }


    /**
     * Et eksempel på problemet beskrevet i 	SKIF-214
     */
    @Test(enabled = false)
    public void testUpdateMedRemoveAvComponentUtenAtObjektErLastetIStore() {

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);

                BubbleWithList copy = new BubbleWithList();
                copy.setId(bubbleId);
                copy.setText("updated");
                copy.setComponents(new HashSet<BubbleWithListComponent>());
                store.lock(copy.getId());

                store.update(copy);
                return null;
            }
        });

        BubbleWithList bubbleWithList = store.get(new BubbleWithListId<BubbleWithList>(2201L));
        Assert.assertEquals(bubbleWithList.getComponents().size(), 0);

        //Legg inn igjen slik at tester kan kjøres flere ganger
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);

                BubbleWithList copy = new BubbleWithList();
                copy.setId(bubbleId);
                copy.setText("text");
                copy.setComponents(new HashSet<BubbleWithListComponent>());
                BubbleWithListComponent e = new BubbleWithListComponent(2202L, "component for 2201", AEnumKodeId.KodeAId);
                e.setBubbleWithList(copy);
                copy.getComponents().add(e);
                store.lock(copy.getId());

                store.update(copy);
                return null;
            }
        });
    }

    public void testUpdateMedRemoveAvComponentEtterAtObjektErLastetIStore() {
        // Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke
        store.evictAll();

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);
                BubbleWithList bubbleWithList = store.get(bubbleId);
                store.lock(bubbleWithList.getId());

                bubbleWithList.getComponents().clear();
                store.update(bubbleWithList);

                return null;
            }
        });

        BubbleWithList bubbleWithList = store.get(new BubbleWithListId<BubbleWithList>(2201L));
        Assert.assertEquals(bubbleWithList.getComponents().size(), 0);

        //Legg inn igjen
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                BubbleWithListId<BubbleWithList> bubbleId = new BubbleWithListId<BubbleWithList>(2201L);

                BubbleWithList copy = new BubbleWithList();
                copy.setId(bubbleId);
                copy.setComponents(new HashSet<BubbleWithListComponent>());
                BubbleWithListComponent e = new BubbleWithListComponent(2202L, "component for 2201", AEnumKodeId.KodeAId);
                e.setBubbleWithList(copy);
                copy.getComponents().add(e);
                store.lock(copy.getId());

                store.update(copy);
                return null;
            }
        });
    }


    /**
     * Belyser problemet beskrevet i 	SKIF-214
     */
    @Test(enabled = false)
    public void testUpdateParrentBubbleMedHashSet() {

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                ParrentBubble parrentBubble = store.get(new ParrentBubbleId<ParrentBubble>(1));
                ParrentBubble copy = CopyHelper.copy(parrentBubble);
                store.evict(parrentBubble.getId());

                store.lock(copy.getId());

                copy.setChildForParrents(new HashSet<ChildForParrent>());
                store.update(copy);

                return null;
            }
        });
    }

    /**
     * OBS! Ødelegger data i databasen så kan ikke kjøres mer enn en gang
     */
    @Test(enabled = false)
    public void testUpdateParrentBubbleMedPersistentSet() {

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store store;

            @Override
            public Object run() {
                ParrentBubble parrentBubble = store.get(new ParrentBubbleId<ParrentBubble>(1));
                ParrentBubble copy = CopyHelper.copy(parrentBubble);
                store.evict(parrentBubble.getId());

                store.lock(copy.getId());

                copy.setChildForParrents(new HashSet<ChildForParrent>());
                store.update(copy);

                return null;
            }
        });
    }
}

