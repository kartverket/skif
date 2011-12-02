package no.statkart.skif.util;

import java.util.Locale;

/**
 * Interface for lokalisering av kodeverdier.
 * @author Jan Holmen
 */
public interface KodeMsg {

    String getString(String key, Locale locale);


}
