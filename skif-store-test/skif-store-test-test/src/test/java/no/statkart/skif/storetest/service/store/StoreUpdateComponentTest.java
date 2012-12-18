package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import no.statkart.skif.util.CopyHelper;
import org.hibernate.Session;
import org.hibernate.collection.PersistentSet;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * Se SKIF-210 og SKIF-214 for mer informasjon om problemet som testes her.
 *
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreUpdateComponentTest extends StoreTestMixedTestCase {

    @Inject
    Store store;

    /**
     * Test som tester at vi får lagt til en entitycomponent i en liste på et bobleobjekt uten at eksisterende elementer
     * i listen får nye versjoner.
     */
    public void leggTilEntryIComponentListe() {
        // TODO Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke (SKIF-237)
        store.evictAll();

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
            Assert.assertEquals(bubbleWithListComponent.getVersjonId(), 1);
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
     * Test som tester at bobleobjekt kan oppdateres uten at eksisterende entitycomponents får nye versjoner.
     */
    public void testMapUtOgTilbake() {
        // TODO Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke (SKIF-237)
        store.evictAll();

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
            Assert.assertEquals(bubbleWithListComponent.getVersjonId(), 1);
        }
    }

    public void testUpdateUtenAtObjektErLastetIHibernate() {
        // TODO Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke (SKIF-237)
        store.evictAll();

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
            Assert.assertEquals(bubbleWithListComponent.getVersjonId(), 1);
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
     * Tester at entitycomponents kan fjernes fra ikke-persistent collection.
     */
    public void testUpdateMedRemoveAvComponentUtenAtObjektErLastetIStore() {
        // TODO Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke (SKIF-237)
        store.evictAll();

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

    /**
     * Tester at entitycomponents kan fjernes fra persistent collection.
     */
    public void testUpdateMedRemoveAvComponentEtterAtObjektErLastetIStore() {
        // TODO Store på klient kan inneholde objekter fra andre tester. Må bestemme oss for hvordan dette skal virke (SKIF-237)
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
     * Belyser problemet beskrevet i SKIF-214.
     * Skalslette alle komponenter i ParrentBubble 1, men ingenting skjer pga SKIF-214
     *
     * Med SKIF-214 løst blir komponenter slettet.
     *
     * TODO: Skrive om testen slik at det ikke endre på data som legges inn via loadData og er en del av "readsettet"
     */
    @Test
    public void testUpdateParrentBubbleMedHashSet() {

        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            StoreServer store;

            @Override
            public Object run() {
                ParrentBubble parrentBubble = store.get(new ParrentBubbleId<ParrentBubble>(1));
                ParrentBubble copy = CopyHelper.copy(parrentBubble);
                store.evict(parrentBubble.getId());

                store.lock(copy.getId());

                final HashSet<ChildForParrent> childForParrents = new HashSet<ChildForParrent>();
                //childForParrents.add(CopyHelper.copy(parrentBubble.getChildForParrents().iterator().next()));
                copy.setChildForParrents(childForParrents);
                store.update(copy);

                store.flush();

                final Session session = store.getInstance(Session.class);
                final BigDecimal count = (BigDecimal) session.createSQLQuery("select count(*) from childforparrent where parrentbubbleId=:parentId").setParameter("parentId", parrentBubble.getId()).uniqueResult();
                assertEquals(count.intValue(), 0, "relasjon ble ikke slettet");
                return null;
            }
        });
    }
}

