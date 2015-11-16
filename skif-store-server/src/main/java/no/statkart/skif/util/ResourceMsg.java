package no.statkart.skif.util;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

/**
 * Superklasse for å hente tekster fra <code>ResourceBundles</code>. Denne klassen kan kun brukes
 * via en subklasse. <p>
 * <p>
 * Internasjonalisering håndteres ved å plassere alle tekster i en resourcebundle som navngis etter
 * den Javapakken hvor den plasseres og det lokale som resourcebundlen representerer. For eksempel
 * for pakken <code>no.statkart.matrikkel.presentasjon.adresse</code> lages det en resoucebundle
 * propertyfil som heter <code>AdresseMsg_no_NO_B.properties</code>. I tillegg lages det en
 * klasse som heter <code>no.statkart.matrikkel.presentasjon.adresse.AdresseMsg</code> som kan hente ut tekster
 * fra resourcebundlen.<p>
 * <p>
 * Konvensjonen for plassering av tekster:
 * <ul>
 * <li>Tekster plasseres fortrinnsvis i pakkens resourcebundle.
 * <li>Hvis en pakke ikke har noen resourcebundle brukes superpakkens resourcebundle.
 * </ul>
 * <p>
 * Det er mulig å kjede resourcebundles sammen så en resourcebundleklasse leter i flere
 * resourcebundles. For eksempel kan <code>AdresseMsg</code> settes opp til først å lete i
 * resourcebundlen <code>no.statkart.matrikkel.presentasjon.adresse.AdresseMsg</code> og dernest i
 * <code>no.statkart.matrikkel.MatrikkelMsg</code>. Får å få dette til må
 * <code>AdresseMsg</code> settes opp til å bruke begge resourcebundles.<p>
 * <p>
 * Konvensjon for navngivning av tekster:
 * <ul>
 * <li>For generelle tekster som brukes av mange klasser skrives nøkkelen med små bokstaver og
 * "_" for å skille ord. Eksempel: <code>"generell_tekst"</code>, <code>"adressekode"</code>.
 * <li>For klassespecifikke tekster prefikses nøkkelen med klassenavnet.
 * Eksempel: <code>"VegView.gatenr_ikke_nummer_numerisk"</code>
 * <li>For klassespesifikke tekster prefikses nøkkelen med klassenavnet.
 * <li>For klassespesifikke tekster prefikses nøkkelen med klassenavnet.
 * Eksempel: <code>"VegView.adressekode_ikke_nummer_numerisk"</code>
 * </ul><p>
 * <p>
 * <strong>Bruk</strong>
 * <pre>
 *    AdresseMsg.getString("adressekode");
 *    AdresseMsg.getString("VegView.adressekode_ikke_numerisk", adressekodeText);
 * </pre>
 *
 * @author Henrik Fredholm
 * @author Jan Holmen
 */
public abstract class ResourceMsg {
    private static Logger logger = LoggerFactory.getLogger(ResourceMsg.class);
    private static final String LABEL_POSTFIX = ":";
    private static final String NO_POSTFIX = "";

    private Map<Locale, ResourceBundle[]> bundleMap;
    private String[] baseNameArray;




    public ResourceMsg(String[] baseNameArray) {
        this.baseNameArray = baseNameArray;
    }

   private void initializeBundle(Locale locale){
        if(bundleMap == null){
            bundleMap = new HashMap<Locale, ResourceBundle[]>();
        }
        if(bundleMap.get(locale) == null){
            ResourceBundle[] bundleFiles = new ResourceBundle[baseNameArray.length];
            for (int i = 0; i < bundleFiles.length; i++) {
                String baseName = baseNameArray[i];
                bundleFiles[i] = ResourceBundle.getBundle(baseName, locale);
            }
            bundleMap.put(locale, bundleFiles);
        }
    }



    private String getStringImpl(String key, Locale locale) {
        if (locale == null) {
            throw new ImplementationException("Locale can not be null");
        }
        MissingResourceException firstException = null;
        initializeBundle(locale);
        try {
            for (ResourceBundle resourceBundle : bundleMap.get(locale)) {
                try {
                    return resourceBundle.getString(key);
                } catch (MissingResourceException e) {
                    if (firstException == null) firstException = e;
                }
            }
        } catch (NullPointerException e) {
            throw new ImplementationException("Requested Locale is not in list");
        }
        logger.error("Resource key ('" + key + "') ikke funnet locale=" + locale);
        throw firstException;
    }

    public String getString(String key, Locale locale) {
        try {
            return getStringImpl(key, locale);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
            //return "...";
        }
    }

    public String getLabel(String key, Locale locale) {
        try {
            return getStringImpl(key, locale) + ":";
        } catch (MissingResourceException e) {
            return "!" + key + "!";
            //return "...";
        }
    }

    public String getString(String key, Locale locale, Object arg) {
        Object[] args = new Object[]{arg};
        return createLocalizedMessage(key, NO_POSTFIX, args, locale);
    }

    public String getLabel(String key, Locale locale, Object arg) {
        Object[] args = new Object[]{arg};
        return createLocalizedMessage(key, LABEL_POSTFIX, args, locale);
    }

    public String getString(String key, Locale locale, Object arg1, Object arg2) {
        Object[] args = new Object[]{arg1, arg2};
        return createLocalizedMessage(key, NO_POSTFIX, args, locale);
    }

    public String getString(String key, Locale locale, Object arg1, Object arg2, Object arg3) {
        Object[] args = new Object[]{arg1, arg2, arg3};
        return createLocalizedMessage(key, NO_POSTFIX, args, locale);
    }

    public String getString(String key, Locale locale, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] args = new Object[]{arg1, arg2, arg3, arg4};
        return createLocalizedMessage(key, NO_POSTFIX, args, locale);
    }

    public String getString(String key, Locale locale, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        Object[] args = new Object[]{arg1, arg2, arg3, arg4, arg5};
        return createLocalizedMessage(key, NO_POSTFIX, args, locale);
    }

    /**
     * Lager en melding som ikke skal være label i GUI.
     *
     * @param key             nøkkel for meldingen
     * @param args            verdier som skal flettes inn i meldingen
     * @param postFix         tegn som skal legges til på slutten av meldingen. F.eks. ':' for label i GUI
     * @return en ferdig lokalisert melding
     */
    private String createLocalizedMessage(String key, String postFix, Object[] args, Locale locale) {
        String nonArgMessage;
        try {
            nonArgMessage = getStringImpl(key, locale);
        } catch (MissingResourceException e) {
            return "!" + key + "!";
        }
        //Ønsker ikke 'null' i meldingen, erstatter med ''
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                args[i] = "";
        }
        String messageWithArguments = MessageFormat.format(nonArgMessage, args);

        if (postFix != null && postFix.length() > 0)
            return messageWithArguments + postFix;
        else
            return messageWithArguments;
    }

    protected String getString(String key, String[] args, Locale locale) {
        return MessageFormat.format(getStringImpl( key, locale), (Object[]) args);
    }

}
