package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.PermissionDeniedException;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.ServiceFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.impl.SimpleFaultInfo;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapping;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @since 2.2.0
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@Test
public class ExceptionMappingTest {
    /**
     * Tester at AbstractExceptionMapper gjør om EJBAccessException til PersmissionDeniedException.
     * Tester også at PersmissionDeniedException konverteres riktig til ServiceException og tilbake igjen.
     */
    public void testEjbAccessException() {
        SkifTestExceptionMapper skifTestExceptionMapper = new SkifTestExceptionMapper();
        SkifTestExceptionMapping mapping = skifTestExceptionMapper.getMapping();

        PermissionDeniedException domainException = new PermissionDeniedException("Test");

        Throwable wsapiException = mapping.d2w(domainException);
        assertTrue(wsapiException instanceof ServiceException, "ServiceException");

        Throwable domainException2 = mapping.w2d(wsapiException);
        assertTrue(domainException2 instanceof PermissionDeniedException, "PermissionDeniedException");
    }

    public void testSubclassMapping() {
        SkifTestExceptionMapper skifTestExceptionMapper = new SkifTestExceptionMapper();
        SkifTestExceptionMapping mapping = skifTestExceptionMapper.getMapping();

        ImplementationException domainImplementationException = new ImplementationException("Test IE");
        ServiceException wsImplementationException = (ServiceException) mapping.d2w(domainImplementationException);
        assertEquals(wsImplementationException.getMessage(), domainImplementationException.getMessage());
        assertEquals(wsImplementationException.getFaultInfo().getClass(), ServiceFaultInfo.class);
        Throwable mappedImplementationException = mapping.w2d(wsImplementationException);
        assertEquals(mappedImplementationException.getMessage(), domainImplementationException.getMessage());
        assertEquals(mappedImplementationException.getClass(), ImplementationException.class);

        FinderException domainFinderException = new FinderException("Test FE");
        ServiceException wsFinderException = (ServiceException) mapping.d2w(domainFinderException);
        assertEquals(wsFinderException.getMessage(), domainFinderException.getMessage());
        assertEquals(wsFinderException.getFaultInfo().getClass(), ServiceFaultInfo.class);
        Throwable mappedFinderException = mapping.w2d(wsFinderException);
        assertEquals(mappedFinderException.getMessage(), domainFinderException.getMessage());
        assertEquals(mappedFinderException.getClass(), FinderException.class);

        SimpleException domainSimpleException = new SimpleException("Test SE", "Info");
        ServiceException wsSimpleException = (ServiceException) mapping.d2w(domainSimpleException);
        assertEquals(wsSimpleException.getMessage(), domainSimpleException.getMessage());
        assertEquals(wsSimpleException.getFaultInfo().getClass(), SimpleFaultInfo.class);
        Throwable mappedSimpleException = mapping.w2d(wsSimpleException);
        assertEquals(mappedSimpleException.getMessage(), domainSimpleException.getMessage());
        assertEquals(((SimpleException) mappedSimpleException).getInfoField(), domainSimpleException.getInfoField());
        assertEquals(mappedSimpleException.getClass(), SimpleException.class);
    }
}
