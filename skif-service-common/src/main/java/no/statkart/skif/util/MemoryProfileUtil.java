package no.statkart.skif.util;

import no.statkart.skif.exception.OperationalException;

import javax.swing.*;
import java.io.IOException;

/**
 * Hjelpeklasse for å stanse programmet på veldefinerte steder slik at man kan studere memoryforbruk via JProfiler.
 * Klassen har en switch for å disable prompts slik at programmet ikke stopper opp. Det har også en switch for å angi
 * om det skal brukes MessageBox eller lese fra System.in
 *
 * @author Henrik Fredholm
 */
public class MemoryProfileUtil {
    private static boolean isEnabled = true;
    private static boolean useMessageBox = true;


    public static void promptAndWait(String prompt) {
        if (isEnabled) {
            if (useMessageBox) {
                System.out.println("Take profile snapshot [" + prompt + "]. Press ok in messagebox to continue");
                String infoMessage = "Memory profile position [" + prompt + "]. Press return to continue.";
                JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + prompt, JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("Take profile snapshot [" + prompt + "]. Press return to continue.");
                try {
                    System.in.read();
                } catch (IOException e) {
                    throw new OperationalException(e);
                }
            }
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public static void setUseMessageBox() {
        useMessageBox =true;
    }

    public static void setUseSystemIn() {
        useMessageBox =false;
    }

}
