package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

@Test
public class SystemVersionTest {
    private void parseFeilSystemVersion(String feilSystemVersion) {
        try {
            new SystemVersion(feilSystemVersion);
        } catch (IllegalArgumentException | ImplementationException e) {
            return;
        }
        fail("Skulle fått IllegalArgumentException");
    }

    public void testFeilSystemVersion() {
        parseFeilSystemVersion(null);
        parseFeilSystemVersion("");
        parseFeilSystemVersion("1.10.b1");
    }

    public void testSystemVersionOK() {
//      assertEquals("3.2", new SystemVersion("3.2").toString());
//      assertEquals("3.2.0", new SystemVersion("3.2.0").toString());
//      assertEquals("3.2.0.0", new SystemVersion("3.2.0.0").toString());
//      assertEquals("3.2.1", new SystemVersion("3.2a1").toString());
//      assertEquals("3.2.0.1", new SystemVersion("3.2.0a1").toString());
        assertEquals("3.3", new SystemVersion("3.3-build6").toString());
    }

    public void testSystemVersionEqualsHashCode() {
        SystemVersion versjon1 = new SystemVersion("1.10");
        SystemVersion versjon2 = new SystemVersion("1.10");
        SystemVersion versjon3 = new SystemVersion("trunk");
        SystemVersion versjon4 = new SystemVersion("dev1.11");

        assertEquals(versjon1, versjon2, "Skulle vært like");
        assertEquals(versjon1.hashCode(), versjon2.hashCode(), "Skulle vært like");
        assertNotEquals(versjon1, versjon3, "Skulle ikke vært like");
        assertNotEquals(versjon1.hashCode(), versjon3.hashCode(), "Skulle vært like");
        assertNotSame(versjon1, versjon3, "Skulle ikke vært like");
        assertEquals(versjon3, versjon4, "Skulle vært like");
        assertEquals(versjon3.hashCode(), versjon4.hashCode(), "Skulle vært like");
    }

    public void testSystemVersionCompare() {
        List<SystemVersion> versjonerSortert = new ArrayList<>();
        versjonerSortert.add(new SystemVersion("0"));
        versjonerSortert.add(new SystemVersion("0.0"));
        versjonerSortert.add(new SystemVersion("0.0.0"));
        versjonerSortert.add(new SystemVersion("0.1"));
        versjonerSortert.add(new SystemVersion("1.10"));
        versjonerSortert.add(new SystemVersion("1.10.2"));
        versjonerSortert.add(new SystemVersion("1.10.3"));
        versjonerSortert.add(new SystemVersion("1.11.1"));
        versjonerSortert.add(new SystemVersion("2.0"));
        versjonerSortert.add(new SystemVersion("trunk"));
        List<SystemVersion> versjonerReversert = new ArrayList<>(versjonerSortert);
        Collections.reverse(versjonerReversert);
        SortedSet<SystemVersion> versjoner = new TreeSet<>(versjonerReversert);
        int i = 0;
        for (SystemVersion SystemVersion : versjoner) {
            SystemVersion versjon = versjonerSortert.get(i);
            assertEquals(versjon, SystemVersion, "Skulle vært like");
            i++;
        }
    }
}