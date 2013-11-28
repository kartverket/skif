package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCMany;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Bobleklasse;

/**
 * Mapper WS enum {@code Bobleklasse }til {@code Class<? extends StoreTestBubble>}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class BobleklasseTypeMapper extends AbstractTypeMapper<Bobleklasse,Class<? extends StoreTestBubble>,Mapping> {

    public BobleklasseTypeMapper() {
        super(Bobleklasse.class, (Class < Class <? extends StoreTestBubble>>)StoreTestBubble.class.getClass(), Mapping.class);
    }

    @Override
    public Bobleklasse mapDomainObject(Class<? extends StoreTestBubble> source) {
        Bobleklasse bobleklasse = Bobleklasse.fromValue(source.getSimpleName());
        return bobleklasse;
    }

    @Override
    public Class<? extends StoreTestBubble> mapWsapiObject(Bobleklasse source) {
        switch (source) {
            case STORE_TEST_BUBBLE:
                return StoreTestBubble.class;
            case SIMPLE:
                return Simple.class;
            case SUB_TYPED_BUBBLE:
                return SubTypedBubble.class;
            case BUBBLE_WITH_RELATION:
                return BubbleWithRelation.class;
            case X_1_AA:
                return X1AA.class;
            case X_1_BB_ONE:
                return X1BBOne.class;
            case X_1_CC_MANY:
                return X1CCMany.class;

            default:
                throw new ImplementationException("Could not map: " + source);
        }
    }
}
