package no.statkart.skif.storetest.util;

import com.google.inject.Inject;
import no.statkart.skif.util.KodeMsg;
import no.statkart.skif.util.ResourceMsg;

import java.util.Locale;

/**
 * Implementasjon av kode lokalisering.
 * @author Jan Holmen
 */
public class DemoKodeMsg extends ResourceMsg implements KodeMsg{


    private static ResourceMsg instance;

    public static String[] getBasenameArray() {
        return new String[] {"no.statkart.skif.storetest.lokalisering.DemoKodeMsg"};
    }


     @Inject
     public DemoKodeMsg() {
        super(getBasenameArray());
    }



}
