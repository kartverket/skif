package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AA;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOne;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCMany;
import no.statkart.skif.storetest.wsapi.endringslogg.Domeneklasse;

/**
 * Mapper WS enum {@code Domeneklasse }til {@code Class<? extends StoreTestBubble>}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class DomeneklasseTypeMapper extends AbstractTypeMapper<Domeneklasse,Class<? extends StoreTestBubble>,Mapping> {

    public DomeneklasseTypeMapper() {
        super(Domeneklasse.class, (Class < Class <? extends StoreTestBubble>>)StoreTestBubble.class.getClass(), Mapping.class);
    }

    @Override
    public Domeneklasse mapDomainObject(Class<? extends StoreTestBubble> source) {
        Domeneklasse endringsklasse = Domeneklasse.fromValue(source.getSimpleName());
        return endringsklasse;
    }

    @Override
    public Class<? extends StoreTestBubble> mapWsapiObject(Domeneklasse source) {
        switch (source) {
            case SIMPLE:
                return Simple.class;
            case SUB_TYPED_BUBBLE:
                return SubTypedBubble.class;
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
