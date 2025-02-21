package no.statkart.skif.skiftest.service.test;

import com.google.inject.Key;
import com.google.inject.name.Names;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.util.testsupport.SkifServerTestCase;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * Tester ut at server injectoren blir gjenbukt på tvers av testmetoder og testcases
 * for SkifServerCase tester. Denne test henter ut state fra serveren som allerede skal være lagt inn.
 *
 * NB: Denne test kan ikke kjøres for seg selv i Idea. Må kjøre hele pakken
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(dependsOnGroups = "SkifServerTest.createsInjector")
public class SkifServerTestCaseReusableInjectorReuseTest extends SkifServerTestCase {
    private static String TEST_VALUE="testvalue";

    public SkifServerTestCaseReusableInjectorReuseTest() {
        super(SkifTestServerModule.class);
    }

    /**
     * Denne test sjekker at TEST_VALUE allerede er satt via annen test i klassen {@code SkifServerTestReusableInjectorCreatorTest}
     */
    public void firstTestMethod() {
        final List list = injector.getInstance(Key.get(List.class, Names.named("SharedList")));
        assertTrue(list.contains(TEST_VALUE));
    }

}
