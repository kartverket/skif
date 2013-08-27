package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Interface som angir at et domeneobjekt er en komponent i en annet objekt som fullstendig eier det. Komponenter kan
 * ikke deles eller gis bort til andre komponenter.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface Component extends Serializable {
}
