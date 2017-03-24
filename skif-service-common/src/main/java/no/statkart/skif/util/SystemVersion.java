package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;

import java.util.Arrays;

/**
 * Klasse for å holde på systemversjon og sammenligne disse.
 */
public class SystemVersion implements Comparable<SystemVersion> {

    int[] deler;

    public SystemVersion(String systemVersion) {
        if (systemVersion == null) {
            throw new ImplementationException("SystemVersion kan ikke være null");
        }

        systemVersion = systemVersion.trim().replace("-SNAPSHOT", "");
        systemVersion = systemVersion.replaceAll("-build([0-9]+)", "");
        systemVersion = systemVersion.replaceAll("([a-b])([0-9])", ".$2");

        if (systemVersion.length() > 0 && Character.isLetter(systemVersion.charAt(0))) {
            deler = null;
        } else {
            String[] ss = systemVersion.split("\\.");
            deler = new int[ss.length];

            for (int i = 0; i < ss.length; i++) {
                try {
                    deler[i] = Integer.parseInt(ss[i]);
                    if (deler[i] < 0) {
                        throw new ImplementationException("Ugyldig systemVersion: " + systemVersion);
                    }
                } catch (NumberFormatException e) {
                    throw new ImplementationException("Ugyldig systemVersion:  " + systemVersion);
                }
            }
        }
    }

    public boolean newerThanOrEqualTo(SystemVersion klientVersjon) {
        return compareTo(klientVersjon) >= 0;
    }

    /**
     * Sammenligner to SystemVersion-klasser
     *
     * @param that SystemVersion vi skal sammenligne med
     * @return -1 dersom this er eldre enn that, 0 hvis de er like og +1 hvis this er nyere enn that
     */
    public int compareTo(SystemVersion that) {
        if (that == null) {
            throw new ImplementationException("Kan ikke sammenlikne Matrikkelversjon mot null");
        }

        // hvis en av dem er trunk
        if (deler == null || that.deler == null) {
            if (deler == that.deler) {
                return 0; // begge er trunk
            }
            if (deler == null) {
                return 1; // this er trunk
            } else {
                return -1; // that er trunk
            }
        } else {
            // ingen av dem er trunk
            int len = Math.min(deler.length, that.deler.length);
            for (int i = 0; i < len; i++) {
                if (deler[i] != that.deler[i]) {
                    if (deler[i] < that.deler[i]) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
            // Alle felt er like
            if (deler.length == that.deler.length) {
                return 0;
            } else if (deler.length < that.deler.length) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    @Override
    public String toString() {
        if (deler == null) {
            return "trunk";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < deler.length; i++) {
                if (i != 0) {
                    sb.append('.');
                }
                sb.append(deler[i]);
            }
            return sb.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemVersion that = (SystemVersion) o;

        return Arrays.equals(deler, that.deler);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(deler);
    }

}
