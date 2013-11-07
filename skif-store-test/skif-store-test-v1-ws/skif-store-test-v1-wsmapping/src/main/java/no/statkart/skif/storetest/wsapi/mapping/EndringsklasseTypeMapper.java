package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;
import no.statkart.skif.storetest.wsapi.domain.endringslogg.Endringsklasse;

/**
 * Mapper WS enum {@code Endringsklasse }til {@code Class<? extends Endring>}
 *
 * @author Henrik Fredholm
 * @since 2.4
 */
public class EndringsklasseTypeMapper extends AbstractTypeMapper<Endringsklasse,Class<? extends Endring>,Mapping> {

    public EndringsklasseTypeMapper() {
        super(Endringsklasse.class, (Class < Class <? extends Endring>>)Endring.class.getClass(), Mapping.class);
    }

    @Override
    public Endringsklasse mapDomainObject(Class<? extends Endring> source) {
        Endringsklasse endringsklasse = Endringsklasse.fromValue(source.getSimpleName());
        return endringsklasse;
    }

    @Override
    public Class<? extends Endring> mapWsapiObject(Endringsklasse source) {
        switch (source) {
            case ENDRING:
                return Endring.class;
            case SIMPLE_ENDRING:
                return SimpleEndring.class;
            case SUB_TYPED_BUBBLE_ENDRING:
                return SubTypedBubbleEndring.class;
            case BUBBLE_WITH_RELATION_ENDRING:
                return BubbleWithRelationEndring.class;
/*
            case X_1_AA:
                return Endring.class;
            case X_1_BB_ONE:
                return Endring.class;
            case X_1_CC_MANY:
                return Endring.class;
*/
            default:
                throw new ImplementationException("Could not map: " + source);
        }
    }
}
