package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent som ikke har id og som lagres i databasen i samme tabell som
 * det eiende objektet {@code <T> }. Siden det eiende objektet selv kan være en {@code CompositeComponent} så er det
 * også nødvendig å angi typen på rootOwner {@code <O>}.
 *
 * <P>En CompositeComponent kan inneholde ValueObject-er og EntityComponent-er samt collections av disse. En CompositeComponent
 * kan også inneholde andre CompositeComponent-er, men ikke collections av disse (ved slike behov bør man i stedet bruke
 * {@link ValueObject} eller {@link EntityComponent}.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.3
 */
public interface CompositeComponent<O, T> extends ComponentWithOwnerReference<T> {
   O getCompositeRootOwner();
   void onSetCompositeRootOwner();

}
