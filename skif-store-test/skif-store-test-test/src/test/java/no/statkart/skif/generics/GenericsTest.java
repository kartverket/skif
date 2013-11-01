package no.statkart.skif.generics;

import no.statkart.skif.generics.domain.AbstractStrutId;
import no.statkart.skif.generics.domain.Strut;
import no.statkart.skif.generics.domain.StrutId;
import no.statkart.skif.generics.domain.impl.AStrut;
import no.statkart.skif.generics.domain.impl.AStrutId;
import no.statkart.skif.generics.service.StrutService;
import org.testng.annotations.Test;

import java.util.HashMap;

/**
 * Demonstrasjon av generics
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public class GenericsTest {

    @Test
    public void testNewInstance() {
        final AStrut aStrut = new AStrut();
        final AStrut<AStrutId<?>> typedAStrut = aStrut;

        aStrut.setId(new AStrutId(100));

        final AbstractStrutId id = aStrut.getId();
        final AbstractStrutId id1 = aStrut.getId();


        final StrutService strutService = buildStrutService();
        strutService.registerStrut(aStrut);

//        final AbstractStrut object = id.getObject(strutService);

        final AStrut object = typedAStrut.getId().getObject(strutService);

    }
    StrutService buildStrutService() {
        return new StrutService() {
            final HashMap<StrutId, Strut> store = new HashMap<StrutId, Strut>();

            @Override
            public <O extends Strut<? super I>, I extends StrutId<?, O>> O getStrut(I strutId) {
                return (O) store.get(strutId);
            }

            @Override
            public <O extends Strut<I>, I extends StrutId<?, O>> O registerStrut(O strut) {
                return (O) store.put(strut.getId(), strut);
            }

//            @Override
//            public <O extends Strut<? super I>, I extends StrutId<O>> O getStrut(I strutId) {
//                return (O) store.get(strutId);
//            }
//
//            @Override
//            public <O extends Strut<I>, I extends StrutId<O>> O registerStrut(O strut) {
//                return (O) store.put(strut.getId(), strut);
//            }


        };
    }




//    @Test
//    public void testList() {
//
//
//        {
//            final Item item = new Item(); //ikke closed erasure
//            final ItemId id1 = new ItemId(200);
//            item.setId(id1);
//        }
//        {
//            final Item<ItemId<?>> item = new Item();
//            final SubItem subItem = new SubItem();
//            final ItemId id1 = new ItemId(200);
//            final ItemId subItemId = new ItemId<SubItem>(9999);
//            final SubItemId subItemId2 = new SubItemId(1000000);
//            item.setId(subItemId);
//            item.setId(id1);
//
//            subItem.setId(subItemId);
//            subItem.setId(subItemId2);
//            subItem.setId(id1);
//
//
//            final ItemId<? extends Item> id = item.getId();
//        }
//
//        {
//            final Item<ItemId<?>> item = new Item<ItemId<?>>();
//            final ItemId id1 = new ItemId(200);
//            item.setId(id1);
//        }
//
//        final Item item = new Item();
//        final ItemId id1 = new ItemId(200);
//        item.setId(id1);
//
//        final ItemId id = item.getId();
//
//        final ItemService itemService = buildItemService();
//        itemService.registerItem(item);
//
//        final Item object = id.getObject(itemService);
//
//        final Item item1 = itemService.getItem(id);
//        final List<Item<? extends ItemId>> itemList = itemService.getItemList(id);
//
//        List debug = new ArrayList(itemList);
//
//    }
//    ItemService buildItemService() {
//        return new ItemService() {
//            final HashMap<ItemIdInterface, Item> store = new HashMap<ItemIdInterface, Item>();
//
//            @Override
//            public <O extends ItemInterface<? extends I>, I extends ItemIdInterface<O>> O getItem(I itemId) {
//                return (O) store.get(itemId);
//            }
//
//            @Override
//            public <O extends ItemInterface<? extends I>, I extends ItemIdInterface<O>> List<O> getItemList(I itemId) {
//                final List<O> list = new ArrayList<O>();
//                list.add(getItem(itemId));
//                return list;
//            }
//
//
//            @Override
//            public <O extends Item<I>, I extends ItemId<O>> O registerItem(O strut) {
//                return (O) store.put(strut.getId(), strut);
//            }
//        };
//    }

}
