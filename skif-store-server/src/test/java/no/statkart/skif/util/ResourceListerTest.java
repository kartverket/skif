package no.statkart.skif.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Test for {@link ResourceLister}.
 *
 * @author Tor Egil R. Strand
 */
@Test
public class ResourceListerTest {
    /**
     * javax.swing.plaf.basic.icons.JavaCup16.png vil nok alltid være i en jar-fil (resources.jar).
     * <p>
     * Av en eller annen grunn virker ikke dette trikset alltid for system-jar-filter. Det ser ut til å virker for
     * våre egne heldigvis, og det er der vi stort sett skal lete etter ressurser.
     *
     * @throws IOException
     */
    @Test(enabled = false)
    public void finnJavaCup() throws IOException {
        ResourceLister resourceLister = new ResourceLister("javax.swing.plaf.basic.icons");

        boolean funnet = false;
        for (String filnavn : resourceLister) {
            if (filnavn.equals("JavaCup16.png")) {
                Assert.assertFalse(funnet, "javax/swing/plaf/basic/icons/JavaCup16.png flere ganger");
                funnet = true;
            }
        }

        Assert.assertTrue(funnet, "Fant ikke javax/swing/plaf/basic/icons/JavaCup16.png");
    }

    /**
     * Denne klassen kan selv være på classpathen som en fil i en katalog.
     *
     * @throws IOException
     */
    public void finnSegSelv() throws IOException {
        ResourceLister resourceLister = new ResourceLister("no.statkart.skif.util");

        boolean funnet = false;
        for (String filnavn : resourceLister) {
            if (filnavn.equals("ResourceListerTest.class")) {
                Assert.assertFalse(funnet, "Fant no/statkart/skif/util/ResourceListerTest.class flere ganger");
                funnet = true;
            }
        }

        Assert.assertTrue(funnet, "Fant ikke no/statkart/skif/util/ResourceListerTest.class");
    }

}
