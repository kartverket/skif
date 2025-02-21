package no.statkart.skif.generics.service;

import no.statkart.skif.generics.domain.Strut;
import no.statkart.skif.generics.domain.StrutId;

/**
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public interface StrutService {

    <O extends Strut<? super I>, I extends StrutId<?, O>> O getStrut(I strutId);

    <O extends Strut<I>, I extends StrutId<?, O>> O registerStrut(O strut);

}
