package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import no.statkart.skif.mockup.IdSelector;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;


/**
 * @author Thomas Berg
 */
@Test
public class X1AAFinderServiceTest extends StoreTestServerTestCase {
    @Inject
    Store store;

    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private X1AAFinderService x1AAFinderService;

    private StoreTestMockupFacade getWriteMockupFacadeAndSaveDataForTestSet1() {
        return mockupFacadeFactory.getWriteMockupFacadeAndSaveDateForIds(new IdSelector<StoreTestMockupFacade>() {
            @Override
            public Set<? extends BubbleId> selectFrom(StoreTestMockupFacade mockupFacade) {
                return ImmutableSet.copyOf(Iterables.concat(
                        mockupFacade.getX1AAMockupFactory().getAllIds(X1AAId.class),
                        mockupFacade.getX1BBOneMockupFactory().getAllIds(X1BBOneId.class),
                        mockupFacade.getX1CCManyMockupFactory().getAllIds(X1CCManyId.class)

                ));
            }
        });
    }

    public void testFindInvSomeBBIds() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AA aa1 = store.get(mockupFacade.getX1AAMockupFactory().getA1Id());
        X1BBOne bbOne2 = store.get(mockupFacade.getX1BBOneMockupFactory().getB2Id());
        Collection<X1BBOneId<?>> x1BBOneList = Lists.<X1BBOneId<?>>newArrayList(bbOne2.getId());
        Map<X1BBOneId<?>, Set<X1AAId<?>>> testMap = x1AAFinderService.findInvSomeBBIds(x1BBOneList);
        assertThat(testMap.get(bbOne2.getId())).containsExactly(aa1.getId());
    }

    public void testFindX1AAIdsForIdents() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1AA aa1 = store.get(mockupFacade.getX1AAMockupFactory().getA1Id());
        X1BBOne bbOne2 = store.get(mockupFacade.getX1BBOneMockupFactory().getB2Id());
        Collection<X1AAIdent> x1AAIdents = Lists.newArrayList(aa1.getIdent());
        Map<X1AAIdent, Set<X1AAId<?>>> testMap = x1AAFinderService.findX1AAIdsForIdents(x1AAIdents);
        assertThat(testMap.get(aa1.getIdent())).containsExactly(aa1.getId());
    }

    public void testX2BBOneFinderService() {
        StoreTestMockupFacade mockupFacade = getWriteMockupFacadeAndSaveDataForTestSet1();
        X1BBOne bbOne2 = store.get(mockupFacade.getX1BBOneMockupFactory().getB2Id());
        Collection<X1BBOneIdent> x1BBOneIdents = Lists.newArrayList(bbOne2.getIdent());
        Map<X1BBOneIdent, Set<X1BBOneId<?>>> testMap = x1AAFinderService.findX1BBOneIdsForIdents(x1BBOneIdents);
        assertThat(testMap.get(bbOne2.getIdent())).containsExactly(bbOne2.getId());
    }
}
