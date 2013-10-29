package no.statkart.skif.storetest.endringslogg;

import com.google.inject.Singleton;
import no.statkart.skif.store.endringslogg.AbstractEndringManagerConfiguration;
import no.statkart.skif.storetest.domain.basic.BubbleWithRelationId;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SubTypedBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;

/**
 * Deklarasjon av endringslogg generering for domenet
 *
 * @author Leif Lislegård
 * @since 2.4 - ny grunnbok sprint 28
 */
@Singleton
public class EndringManagerConfiguration<E extends Endring<?>> extends AbstractEndringManagerConfiguration<Endring> {

    public EndringManagerConfiguration() {

        declare(SimpleEndring.class).forBubbleId(SimpleId.class);
        declare(BubbleWithRelationEndring.class).forBubbleId(BubbleWithRelationId.class);
        declare(SubTypedBubbleEndring.class).forBubbleId(SubTypedBubbleId.class);

    }
}
