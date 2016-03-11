package no.statkart.skif.storetest.endringslogg;

import com.google.common.collect.ImmutableBiMap;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.endringslogg.AbstractEndringManagerConfiguration;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelation;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SubTypedBubble;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;

/**
 * Deklarasjon av endringslogg generering for domenet
 *
 * @author Leif Lislegård
 * @author Henrik Fredholm
 * @since 2.4
 */
public class EndringManagerConfiguration extends AbstractEndringManagerConfiguration<Endring> {

    public EndringManagerConfiguration() {
        super(
                ImmutableBiMap.<Class<? extends Endring>, Class<? extends BubbleObject>>of(
                        Endring.class, StoreTestBubble.class,
                        BubbleWithRelationEndring.class, BubbleWithRelation.class,
                        SubTypedBubbleEndring.class, SubTypedBubble.class,
                        SimpleEndring.class, Simple.class
                )
        );
    }
}
