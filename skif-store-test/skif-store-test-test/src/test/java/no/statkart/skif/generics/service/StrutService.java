package no.statkart.skif.generics.service;

import no.statkart.skif.generics.domain.Strut;
import no.statkart.skif.generics.domain.StrutId;

/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public interface StrutService {

    public <O extends Strut<? super I>, I extends StrutId<?, O>> O getStrut(I strutId);

    public <O extends Strut<I>, I extends StrutId<?, O>> O registerStrut(O strut);

}
