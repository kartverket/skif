package no.statkart.skif.skiftest.mapping;

import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapping;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Tester at AbstractExceptionMapper gjør om EJBAccessException til PersmissionDeniedException.
 * Tester også at PersmissionDeniedException konverteres riktig til ServiceException og tilbake igjen.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class EjbExceptionMappingTest {
    public void testEjbAccessException() {
        SkifTestExceptionMapper skifTestExceptionMapper = new SkifTestExceptionMapper();
        SkifTestExceptionMapping mapping = skifTestExceptionMapper.getMapping();

        PermissionDeniedException domainException = new PermissionDeniedException("Test");

        Throwable wsapiException = mapping.d2w(domainException);
        assertTrue(wsapiException instanceof ServiceException, "ServiceException");

        Throwable domainException2 = mapping.w2d(wsapiException);
        assertTrue(domainException2 instanceof PermissionDeniedException, "PermissionDeniedException");
    }
}
