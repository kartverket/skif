package no.statkart.skif.store.localization;

import no.statkart.skif.store.BubbleObject;

import java.util.Map;

/**
 * Marker-interface for bobler som er localizable.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public interface Localized extends BubbleObject {

    /**
     * Henter ut alle lokaliseringer for boblen.
     *
     * @return alle lokaliseringene (dette er det interne mappet, så oppdateringer er live)
     */
    Map<LocalizationMap.LocalizationKey, String> getLocalizationMap();

    /**
     * Setter lokalisering for boblen. Dette gjøres enten av Hibernate (for databasekoder) eller av
     * rammeverket (for enumkoder).
     *
     * @param map    alle lokaliseringer, det tas <i>ikke</i> kopi
     */
    void setLocalizationMap(Map<LocalizationMap.LocalizationKey, String> map);

}
