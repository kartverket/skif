package no.statkart.skif.storetest.wsapi.mapping;

import com.google.inject.util.Providers;
import com.google.inject.util.Types;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKodeId;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.basic.SimpleIdList;
import no.statkart.skif.storetest.wsapi.domain.koder.SimpleEnumKodeIdList;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

public class IdCollectionMappingTest {
    @Test
    public void openIdSet() {
        StoreTestMapping mapping = createMapping();

        Set<SimpleId<?>> simpleIds = new HashSet<>();
        simpleIds.add(new SimpleId<>(1L, SnapshotVersion.CURRENT));

        ParameterizedType simpleIdsType = Types.newParameterizedType(
                Set.class,
                Types.newParameterizedType(
                        SimpleId.class,
                        Types.subtypeOf(Object.class)
                )
        );

        Object simpleIdList = mapping.d2w(simpleIds, simpleIdsType, SimpleIdList.class);
        Assertions.assertThat(simpleIdList).isInstanceOf(SimpleIdList.class);

        Object o = mapping.w2d(simpleIdList, SimpleIdList.class, simpleIdsType);
        Assertions.assertThat(o).isEqualTo(simpleIds);
    }

    @Test
    public void openSuperIdSet() {
        StoreTestMapping mapping = createMapping();

        Set<BubbleId<?>> bubbleIds = new HashSet<>();
        bubbleIds.add(new SimpleId<>(1L, SnapshotVersion.CURRENT));

        ParameterizedType bubbleIdsType = Types.newParameterizedType(
                Set.class,
                Types.newParameterizedType(
                        BubbleId.class,
                        Types.subtypeOf(Object.class)
                )
        );

        Object bubbleIdList = mapping.d2w(bubbleIds, bubbleIdsType, StoreTestBubbleIdList.class);
        Assertions.assertThat(bubbleIdList).isInstanceOf(StoreTestBubbleIdList.class);

        Object o = mapping.w2d(bubbleIdList, StoreTestBubbleIdList.class, bubbleIdsType);
        Assertions.assertThat(o).isEqualTo(bubbleIds);
    }

    @Test
    public void closedIdSet() {
        StoreTestMapping mapping = createMapping();

        Set<SimpleEnumKodeId> simpleEnumKodeIds = new HashSet<>();
        simpleEnumKodeIds.add(new SimpleEnumKodeId(1L, SnapshotVersion.CURRENT));

        ParameterizedType simpleEnumKodeIdsType = Types.newParameterizedType(
                Set.class,
                SimpleEnumKodeId.class
        );

        Object simpleEnumKodeIdList = mapping.d2w(simpleEnumKodeIds, simpleEnumKodeIdsType, SimpleEnumKodeIdList.class);
        Assertions.assertThat(simpleEnumKodeIdList).isInstanceOf(SimpleEnumKodeIdList.class);

        Object o = mapping.w2d(simpleEnumKodeIdList, SimpleEnumKodeIdList.class, simpleEnumKodeIdsType);
        Assertions.assertThat(o).isEqualTo(simpleEnumKodeIds);
    }

    @Test
    public void closedSuperIdSet() {
        StoreTestMapping mapping = createMapping();

        Set<BubbleId<?>> bubbleIds = new HashSet<>();
        bubbleIds.add(new SimpleEnumKodeId(1L, SnapshotVersion.CURRENT));

        ParameterizedType bubbleIdsType = Types.newParameterizedType(
                Set.class,
                Types.newParameterizedType(
                        BubbleId.class,
                        Types.subtypeOf(Object.class)
                )
        );

        Object bubbleIdList = mapping.d2w(bubbleIds, bubbleIdsType, StoreTestBubbleIdList.class);
        Assertions.assertThat(bubbleIdList).isInstanceOf(StoreTestBubbleIdList.class);

        Object o = mapping.w2d(bubbleIdList, StoreTestBubbleIdList.class, bubbleIdsType);
        Assertions.assertThat(o).isEqualTo(bubbleIds);
    }

    @Test
    public void erasedIdSet() {
        StoreTestMapping mapping = createMapping();

        Set<SimpleId<?>> simpleIds = new HashSet<>();
        simpleIds.add(new SimpleId<>(1L, SnapshotVersion.CURRENT));

        ParameterizedType simpleIdsType = Types.newParameterizedType(
                Set.class,
                SimpleId.class
        );

        Object simpleIdList = mapping.d2w(simpleIds, simpleIdsType, SimpleIdList.class);
        Assertions.assertThat(simpleIdList).isInstanceOf(SimpleIdList.class);

        Object o = mapping.w2d(simpleIdList, SimpleIdList.class, simpleIdsType);
        Assertions.assertThat(o).isEqualTo(simpleIds);
    }


    @Test
    public void openIdOpenSet() {
        StoreTestMapping mapping = createMapping();

        Set<SimpleId<?>> simpleIds = new HashSet<>();
        simpleIds.add(new SimpleId<>(1L, SnapshotVersion.CURRENT));

        ParameterizedType simpleIdsType = Types.newParameterizedType(
                Set.class,
                Types.subtypeOf(
                        Types.newParameterizedType(
                                SimpleId.class,
                                Types.subtypeOf(Object.class)
                        )
                )
        );
        System.out.println(simpleIdsType);

        Object simpleIdList = mapping.d2w(simpleIds, simpleIdsType, SimpleIdList.class);
        Assertions.assertThat(simpleIdList).isInstanceOf(SimpleIdList.class);

        Object o = mapping.w2d(simpleIdList, SimpleIdList.class, simpleIdsType);
        Assertions.assertThat(o).isEqualTo(simpleIds);
    }

    @Test
    public void openSuperIdOpenSet() {
        StoreTestMapping mapping = createMapping();

        Set<BubbleId<?>> bubbleIds = new HashSet<>();
        bubbleIds.add(new SimpleId<>(1L, SnapshotVersion.CURRENT));

        ParameterizedType bubbleIdsType = Types.newParameterizedType(
                Set.class,
                Types.subtypeOf(
                        Types.newParameterizedType(
                                BubbleId.class,
                                Types.subtypeOf(Object.class)
                        )
                )
        );

        Object bubbleIdList = mapping.d2w(bubbleIds, bubbleIdsType, StoreTestBubbleIdList.class);
        Assertions.assertThat(bubbleIdList).isInstanceOf(StoreTestBubbleIdList.class);

        Object o = mapping.w2d(bubbleIdList, StoreTestBubbleIdList.class, bubbleIdsType);
        Assertions.assertThat(o).isEqualTo(bubbleIds);
    }

    @Test
    public void closedIdOpenSet() {
        StoreTestMapping mapping = createMapping();

        Set<SimpleEnumKodeId> simpleEnumKodeIds = new HashSet<>();
        simpleEnumKodeIds.add(new SimpleEnumKodeId(1L, SnapshotVersion.CURRENT));

        ParameterizedType simpleEnumKodeIdsType = Types.newParameterizedType(
                Set.class,
                Types.subtypeOf(
                        SimpleEnumKodeId.class
                )
        );

        Object simpleEnumKodeIdList = mapping.d2w(simpleEnumKodeIds, simpleEnumKodeIdsType, SimpleEnumKodeIdList.class);
        Assertions.assertThat(simpleEnumKodeIdList).isInstanceOf(SimpleEnumKodeIdList.class);

        Object o = mapping.w2d(simpleEnumKodeIdList, SimpleEnumKodeIdList.class, simpleEnumKodeIdsType);
        Assertions.assertThat(o).isEqualTo(simpleEnumKodeIds);
    }

    @Test
    public void closedSuperIdOpenSet() {
        StoreTestMapping mapping = createMapping();

        Set<BubbleId<?>> bubbleIds = new HashSet<>();
        bubbleIds.add(new SimpleEnumKodeId(1L, SnapshotVersion.CURRENT));

        ParameterizedType bubbleIdsType = Types.newParameterizedType(
                Set.class,
                Types.subtypeOf(
                        Types.newParameterizedType(
                                BubbleId.class,
                                Types.subtypeOf(Object.class)
                        )
                )
        );

        Object bubbleIdList = mapping.d2w(bubbleIds, bubbleIdsType, StoreTestBubbleIdList.class);
        Assertions.assertThat(bubbleIdList).isInstanceOf(StoreTestBubbleIdList.class);

        Object o = mapping.w2d(bubbleIdList, StoreTestBubbleIdList.class, bubbleIdsType);
        Assertions.assertThat(o).isEqualTo(bubbleIds);
    }

    private StoreTestMapping createMapping() {
        return new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();
    }
}
