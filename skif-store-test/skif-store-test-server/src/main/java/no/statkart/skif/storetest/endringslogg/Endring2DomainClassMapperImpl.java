package no.statkart.skif.storetest.endringslogg;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.inject.Singleton;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.Endring2DomainClassMapper;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class Endring2DomainClassMapperImpl implements Endring2DomainClassMapper {
    BiMap<Class<? extends AbstractEndring>, Class<? extends BubbleObject>> map = ImmutableBiMap.<Class<? extends AbstractEndring>,Class<? extends BubbleObject>>of(
            Endring.class, StoreTestBubble.class,
            BubbleWithRelationEndring.class, BubbleWithRelation.class,
            SubTypedBubbleEndring.class, SubTypedBubble.class,
            SimpleEndring.class, Simple.class
    );

    @Override
    public Class<? extends BubbleObject> getDomainClass(Class<? extends AbstractEndring<?, ?>> endringClass) {
        return checkNotNull(map.get(endringClass), "Fant ingen bobleklasse hørende til %s", endringClass.getName());
    }

    @Override
    public Class<? extends AbstractEndring> getEndringClass(Class<? extends BubbleObject> domainClass) {
        return checkNotNull(map.inverse().get(domainClass), "Bobleklasse %s kan ikke brukes som filter", domainClass.getSimpleName());
    }
}
